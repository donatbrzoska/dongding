#include <WiFi.h>
const char* ssid = ""; //WIFI SSID
const char* password = ""; //WIFI PASSWORD
byte ip[] = { 192, 168, 178, 74}; //Server IP in local network
byte signal_byte_ring = 1;
byte signal_byte_other = 2;

WiFiClient client;

void setup() {
    Serial.begin(9600);
    delay(1000);
    
    pinMode(23, OUTPUT); //LAMP
    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
      delay(1000);
      Serial.println("Connecting to WiFi..");
    }
    Serial.println("Connected to the WiFi network");
    Serial.println("Trying to connect to server...");
    int connection_state = client.connect(ip, 22223);
    while (connection_state != 1){
      connection_state = client.connect(ip, 22223);
      Serial.println("Trying to connect to server...");
    }
    Serial.println("Connected to server");
    Serial.println(WiFi.localIP());
}

void loop() {
    if (client) {
      while (client.connected()) {
        while (client.available()>0) {
          byte b = client.read();
          Serial.print(b);
          if(b == signal_byte_ring){
            blink();
            Serial.println(" Ding Dong received");
          } else if(b = signal_byte_other){
            Serial.println("Essen ist fertig");
            blink_short();
          }
          
        }
      }
    } 
}

void blink() {
    digitalWrite(23, HIGH);
    delay(1000);
    digitalWrite(23, LOW);
    delay(200);
    digitalWrite(23, HIGH);
    delay(1000);
    digitalWrite(23, LOW);
}

void blink_short(){
    digitalWrite(23, HIGH);
    delay(200);
    digitalWrite(23, LOW);
    delay(100);
    digitalWrite(23, HIGH);
    delay(200);
    digitalWrite(23, LOW);
}
