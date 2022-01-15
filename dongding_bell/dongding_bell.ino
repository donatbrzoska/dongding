#include <WiFi.h>
const char* ssid = ""; //WIFI SSID
const char* password = ""; //WIFI PASSWORD
byte ip[] = { 192, 168, 178, 74}; //Server IP in local network
byte signal_byte = 1; // bell ring
WiFiClient client;

void setup() {
  // put your setup code here, to run once:
    Serial.begin(9600);
    delay(1000);
    
    pinMode(18, INPUT);
    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
      delay(1000);
      Serial.println("Connecting to WiFi..");
    }
    Serial.println("Connected to the WiFi network");
    Serial.println("Trying to connect to server...");
    int connection_state = client.connect(ip, 22222);
    while (connection_state != 1){
      connection_state = client.connect(ip, 22222);
      Serial.println("Trying to connect to server...");
    }
    client.write(signal_byte);
    Serial.println("Connected to server");
    Serial.println(WiFi.localIP());
}

void loop() {
  // put your main code here, to run repeatedly:
    if (client) {
      while(client.connected()) {
        if(digitalRead(18) == HIGH){
           Serial.println("dingdong");
           client.write(signal_byte);
           delay(5000);
        }
      }
    }   
}
