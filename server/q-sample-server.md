Assignment Q
========================

### Objective

Write a client-server application that downloads sensor readings.

The server should generate one 64-bit float sensor reading every second from
midnight until current time.

(How the  sensor reading is generated is not important, just generating a 
(float) counter value every second is fine). The client should connect to the 
server, and write the sensor data to a file in the working directory.

### Background
Yanzi is building a massively distributed sensor platform. Ensuring that sensor
data is backed up, available, and near real time, is a core concern. Hence this
assignment is designed to give a glimpse of such a challenge.

### Requirements

1. The server and client should communicate over IP, however specific protocol 
/ message format etc is up to you.

2. The server must support multiple clients.

3. Once started, the client should download all previous sensor readings.

4. The client must resume download after a network disconnect.

5. One sensor reading consists of one timestamp and one float value

### Example Output
```
2018-04-15T19:02:46+00:00 1.01
2018-04-15T19:02:47+00:00 2.01
2018-04-15T19:02:48+00:00 3.01
2018-04-15T19:02:49+00:00 4.01
2018-04-15T19:02:50+00:00 5.01
2018-04-15T19:02:51+00:00 6.01
2018-04-15T19:02:52+00:00 7.01
2018-04-15T19:02:53+00:00 8.01
...
```

Use  Java to solve this task.
You are allowed to cheat and make any reasonable assumption as long as any
cheating/assuming is documented and justified. The code should be production
quality. Please feel free to ask questions.

When turning in the assignment, please provide instructions on how to run/test
the code.




