int cont = 0;
  
void setup() {
  Serial.begin(9600);
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
  Serial.print(cont++);
  delay(1000);
}
