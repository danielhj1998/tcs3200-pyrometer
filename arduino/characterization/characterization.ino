#include "max6675.h"

//
// Cableado de TCS3200 a Arduino
//
#define S0 8
#define S1 9
#define S2 12
#define S3 11
#define salidaSensor 10

//
//Cableado del max6675 al arduino
//
#define thermoSO 4
#define thermoCS 5
#define thermoSCK 6

MAX6675 thermocouple(thermoSCK, thermoCS, thermoSO);

// Para guardar las frecuencias de los fotodiodos
int frecuenciaRojo = 0;
int frecuenciaVerde = 0;
int frecuenciaAzul = 0;
int frecuenciaBlanca = 0;
 
void setup() {
  // Definiendo las Salidas
  pinMode(S0, OUTPUT);
  pinMode(S1, OUTPUT);
  pinMode(S2, OUTPUT);
  pinMode(S3, OUTPUT);
  
  // Definiendo salidaSensor como entrada
  pinMode(salidaSensor, INPUT);
  
  // configurando la escala de frecuencia del sensor a 100%
  digitalWrite(S0,HIGH);
  digitalWrite(S1,HIGH);
  
   // Iniciar la comunicacion serial 
  Serial.begin(9600);
  
  // wait for MAX chip to stabilize
  delay(500);
}

void loop() {
  if(Serial.available())//si se está recibiendo una instrucción
    {
      //Se lee el caracter de entrada
      char op=Serial.read();
      if(op == 's'){
       while(true){
         principal();
       }
      }
    }
}

void principal(){
  lecturaRGBW();
  Serial.println(frecuenciaRojo);
  Serial.println(frecuenciaVerde);
  Serial.println(frecuenciaAzul);
  Serial.println(frecuenciaBlanca);
  
  Serial.println(thermocouple.readCelsius());
  
  delay(2000);
}

void lecturaRGBW(){
  // Lectura de los fotodiodos con filtro rojo
  digitalWrite(S2,LOW);
  digitalWrite(S3,LOW);
  frecuenciaRojo = pulseIn(salidaSensor, LOW);
  
  // Lectura de los fotodiodos con filtro verde
  digitalWrite(S2,HIGH);
  digitalWrite(S3,HIGH);
  frecuenciaVerde = pulseIn(salidaSensor, LOW);
  
  // Lectura de los fotodiodos con filtro azul
  digitalWrite(S2,LOW);
  digitalWrite(S3,HIGH);
  frecuenciaAzul = pulseIn(salidaSensor, LOW);

  // Lectura de los fotodiodos sin filtro
  digitalWrite(S2,HIGH);
  digitalWrite(S3,LOW);
  frecuenciaBlanca = pulseIn(salidaSensor, LOW);
}
