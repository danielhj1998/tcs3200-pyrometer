//
// TCS3200 wiring to Arduino
//
#define S0 8
#define S1 9
#define S2 12
#define S3 11
#define sensorOutput 10

int photodiodeFrecuency = 0;
 
void setup() {
  pinMode(S0, OUTPUT);
  pinMode(S1, OUTPUT);
  pinMode(S2, OUTPUT);
  pinMode(S3, OUTPUT);
  pinMode(sensorOutput, INPUT);
  
  // set sensor frecuency scale to 100%
  digitalWrite(S0,HIGH);
  digitalWrite(S1,HIGH);

  // read photdiodes without filter (value instead of RGB)
  digitalWrite(S2,HIGH);
  digitalWrite(S3,LOW);
  
  Serial.begin(9600);
}

void loop() {
  if(Serial.available())
    {
      char op=Serial.read();
      if(op == 's'){
       while(true){
         sendTemperature();
       }
      }
    }

}

void sendTemperature(){
  
  photodiodeFrecuency = pulseIn(sensorOutput, LOW);
  
  Serial.print(frecuency2Temperature(photodiodeFrecuency));

  delay(1000);
}

float frecuency2Temperature(int f){
  const int a = 1592.94236882059;
  const int b = 1.00094211544372;
  const int c = -0.295805520537105;
  return a*pow(b,f)*pow(f,c) - 273.15 ;
}
