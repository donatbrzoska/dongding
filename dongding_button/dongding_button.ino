#include <WiFi.h>
const char* ssid = ""; //WIFI SSID
const char* password = ""; //WIFI PASSWORD
byte ip[] = { 192, 168, 178, 74}; //Server IP in local network
byte signal_byte = 2; //Food is ready notification
WiFiClient client;

void setup() {
    Serial.begin(9600);
    delay(1000);

 
    pinMode(21, INPUT_PULLUP); //BUTTON 
    pinMode(23, OUTPUT); //LAMP
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
    
    Serial.println("Connected to server");
    Serial.println(WiFi.localIP());
}

void loop() {
    if (client) {
      while(client.connected()) {
        if(digitalRead(21) == LOW){
           Serial.println("Button Pressed");
           client.write(signal_byte);
           digitalWrite(23, HIGH);
           delay(1000);
           digitalWrite(23, LOW);
        }
      }
    }   
}
