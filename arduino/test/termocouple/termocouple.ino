#include "max6675.h"

//Cableado del max6675 al arduino
#define thermoDO 4
#define thermoCS 5
#define thermoCLK 6

MAX6675 thermocouple(thermoCLK, thermoCS, thermoDO);
int vccPin = 3;
int gndPin = 2;
  
void setup() {
  Serial.begin(9600);
  
  // wait for MAX chip to stabilize
  delay(500);
}

void loop() {
  // basic readout test, just print the current temp
   
   //Serial.println(thermocouple.readCelsius());
   Serial.println(1);
   Serial.println(2);
   delay(1000);
}
