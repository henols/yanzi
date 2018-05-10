package se.aceone.yanzi.server;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.moquette.BrokerConstants;
import io.moquette.server.Server;
import io.moquette.server.config.MemoryConfig;

public class DataCollector implements MqttCallback {

	private final static String BROKER_HOST = "0.0.0.0";
	private final static Integer BROKER_PORT = 1883;
	private final static String BROKER_URL = "tcp://localhost:" + BROKER_PORT;
	private final static String CLIENT_ID = "YanziServerSubscriber";
	private final static String TOPIC = "se/aceone/yanzi/reading";

	private Server mqttBroker;
	private MemoryConfig config;
	private MqttClient readingsClient;
	private MqttConnectOptions connOpts;

	public static void main(String[] args) throws IOException, MqttException {
		new DataCollector().start();
	}

	public DataCollector() throws IOException, MqttException {
		mqttBroker = new Server();
		Properties props = new Properties();

		props.setProperty(BrokerConstants.PORT_PROPERTY_NAME, Integer.toString(BROKER_PORT));
		props.setProperty(BrokerConstants.HOST_PROPERTY_NAME, BROKER_HOST);

		config = new MemoryConfig(props);

		MemoryPersistence persistence = new MemoryPersistence();

		readingsClient = new MqttClient(BROKER_URL, CLIENT_ID, persistence);
		readingsClient.setCallback(this);

		connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true);

		Runtime.getRuntime().addShutdownHook(new Thread(mqttBroker::stopServer));
	}

	public void start() throws MqttException, IOException {
		mqttBroker.startServer(config, Collections.emptyList());
		readingsClient.connect(connOpts);
		readingsClient.subscribe(TOPIC, 1);
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		if (TOPIC.equals(topic)) {
			ObjectMapper objectMapper = new ObjectMapper();
			byte[] payload = message.getPayload();
			Reading reading = objectMapper.readValue(payload, Reading.class);
			System.out.println(reading);
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
	}

	@Override
	public void connectionLost(Throwable cause) {
	}

}