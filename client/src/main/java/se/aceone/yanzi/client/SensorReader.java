package se.aceone.yanzi.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SensorReader implements MqttCallback {

	private final static Integer BROKER_PORT = 1883;
	private final static String BROKER_URL = "tcp://localhost:" + BROKER_PORT;
	private final static String CLIENT_ID = "YanziServerSubscriber";
	private final static String TOPIC = "se/aceone/yanzi/reading";

	private ObjectMapper objectMapper = new ObjectMapper();
	private MqttClient sampleClient;
	private MqttConnectOptions connOpts;

	private Timer reconnetionTimer = new Timer();
	private Timer readingsTimer = new Timer();
	private Random r = new Random();
	private List<Reading> readingsStore = new ArrayList<>();

	public static void main(String[] args) throws MqttException, JsonMappingException, IOException {
		new SensorReader().start();
	}

	public SensorReader() throws MqttException {
		MemoryPersistence persistence = new MemoryPersistence();

		sampleClient = new MqttClient(BROKER_URL, CLIENT_ID + r.nextInt(), persistence);
		connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true);
		sampleClient.setCallback(this);

	}

	private void connect() {
		reconnetionTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("Connecting to broker: " + BROKER_URL);
				try {
					sampleClient.connect(connOpts);
					System.out.println("Connected");
					if (sampleClient.isConnected()) {
						cancel();
					}
				} catch (MqttException e) {
				}
			}
		}, 500, 5000);
	}

	private void reading() {
		Reading reading = new Reading(Instant.now().getEpochSecond(), r.nextDouble() * 100);
		if (sampleClient.isConnected()) {
			readingsStore.stream().forEach(x -> post(x));
			readingsStore.clear();
			post(reading);
		} else {
			readingsStore.add(reading);
		}
	}

	private void post(Reading reading) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			objectMapper.writeValue(os, reading);

			MqttMessage m = new MqttMessage();
			m.setPayload(os.toByteArray());

			m.setQos(1);

			sampleClient.publish(TOPIC, m);
		} catch (MqttException | IOException e) {
		}

	}

	private void start() {
		connect();
		readingsTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				reading();
			}
		}, 1000, 1000);
	}

	@Override
	public void connectionLost(Throwable cause) {
		System.out.println("Connection lost, start reconecting");
		connect();
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
	}
}
