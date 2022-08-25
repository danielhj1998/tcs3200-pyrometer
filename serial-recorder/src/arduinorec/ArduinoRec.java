package arduinorec;

import commserial.SerialReceiver;
import commserial.SerialSender;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import commserial.InputBufferAction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author danielhj1998
 */
public class ArduinoRec {
    private static final int CONNECTION_TIME_OUT = 2000; 
    private static final int DEFAULT_BAUD_RATE = 9600;
    private static final int DEFAULT_DATABITS = SerialPort.DATABITS_8;
    private static final int DEFAULT_STOP_BITS = SerialPort.STOPBITS_1;
    private static final int DEFAULT_PARITY = SerialPort.PARITY_NONE;
    
    private static SerialSender sSender;
    private static SerialReceiver sReceiver;
    private static SerialPort sPort;
    
    public void ejecutar(){
        System.out.println("************************************************************************");
        System.out.println("Serial Recorder by danielhj1998");
        System.out.println("************************************************************************");
        
        do{
            String portName = getCOM();
            System.out.println("Connecting to "+portName+"...\n");

            sPort = serialConnect(portName);
        } while(sPort == null);

        System.out.println("\nSucessful connection");
        printMenu();
        
        String op;
        do{
            Scanner sc = new Scanner(System.in);
            op = sc.nextLine();
            
            switch(op){
                case "menu":
                    printMenu();
                    break;
                case "excel":
                    saveXLSX();
                    break;
                case "print":
                    printToStdout();
                    break;
                case "quit":
                    if(sPort != null){
                        sPort.removeEventListener();
			sPort.close();
                    }
                    break;
                default:
                    System.out.println("Esa opción no es válida");
                    break;
            }
            
        }while(!op.equals("quit"));
    
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        
        ArduinoRec app = new ArduinoRec();
        app.ejecutar();
    }
    
    /**Este método obtiene el puerto COM donde está el ARDUINO conectado
     * 
     * @return nombre del puerto COM donde está conectado el ARDUINO
     */
    public static String getCOM(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Input the COM port number");
        
        String portName = "COM"; 
        int n=sc.nextInt();
        return portName + n;
    }
    
    public static SerialPort serialConnect(String portName){
        try {
            //Se realiza la conexión con arduino
            CommPortIdentifier cpIdentifier= CommPortIdentifier.getPortIdentifier(portName);
            sPort = (SerialPort) cpIdentifier.open("SerialRecorder",CONNECTION_TIME_OUT);
            
            int[] params = getParams();
            sPort.setSerialPortParams(params[0], params[1], params[2], params[3]);
            
            sSender= new SerialSender(sPort.getOutputStream());
            sReceiver= new SerialReceiver(sPort.getInputStream());
            sPort.addEventListener(sReceiver);
            
            return sPort;
        } 
        catch (NoSuchPortException ex) {
            System.err.println("Port does not exist");
        }
        catch (PortInUseException ex) {System.out.println("Port is already in use");}
        catch (UnsupportedCommOperationException ex) {System.out.println("Port does not admit configuration parameters");} catch (TooManyListenersException ex) {
            Logger.getLogger(ArduinoRec.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ArduinoRec.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static int[] getParams(){
        System.out.println("Port params configuration:");
        Scanner sc = new Scanner(System.in);
        System.out.println("Do you want to use the default configuration (Baud rate = " + DEFAULT_BAUD_RATE + ", Data bits = " + DEFAULT_DATABITS + ", Stop bits= " + DEFAULT_STOP_BITS + ", Parity bit = No)? [y/n]");
        String op = "";
        do{
            op = sc.next().toLowerCase();
        }while(!op.equals("y") && !op.equals("n"));

        int[] params = {DEFAULT_BAUD_RATE, DEFAULT_DATABITS, DEFAULT_STOP_BITS, DEFAULT_PARITY};
        if(op.equals("y"))
            return params;
        else{
            System.out.println("Lamentablemente aun no está disponible esa opción :x");
            return params;
        }
    }
    
    public void saveXLSX(){
        //Read data
        Scanner sc = new Scanner(System.in);
        System.out.println("\nInput the number of columns: ");
        int nCol = sc.nextInt();
        String[] nombresCol = new String[nCol];
        int[] columnTypes = new int[nCol];
        for(int i=0; i<nCol; i++){
            System.out.println("\nColumn name "+(i+1)+": ");
            sc.nextLine();
            nombresCol[i] = sc.nextLine();
            System.out.println("Data type "+nombresCol[i]+" (numeric[1], string[2], boolean[3]): ");
            columnTypes[i] = sc.nextInt();
        }
        
        List<List> rowsData = new ArrayList();
        
        InputBufferAction buffer= new InputBufferAction(){
            private int currentColumn = 0;
            @Override
            public void receivingActionPerformed(String buffer){
 
                if(currentColumn == 0){
                    List<Object> row = new ArrayList();
                    rowsData.add(row);
                    if(nCol == 1)
                        System.out.println(buffer);
                    else
                        System.out.print(buffer);
                }
                else
                    System.out.println("\t"+buffer);
                
                //Cast data type
                switch(columnTypes[currentColumn]){
                    case 1://Double
                        double numBuffer = Double.parseDouble(buffer);
                        rowsData.get(rowsData.size()-1).add(numBuffer);
                        break;
                    case 3://Boolean
                        boolean boolBuffer = Boolean.parseBoolean(buffer);
                        rowsData.get(rowsData.size()-1).add(boolBuffer);
                    default://String
                        rowsData.get(rowsData.size()-1).add(buffer);
                        break;
                }
                    
                if(currentColumn + 1 >= nCol)
                    currentColumn = 0;
                else
                    currentColumn++;
            }

            @Override
            public void notReceivingActionPerformed(){

            }
        };
        
        System.out.println("\nTo start press ENTER (A string \"S\" is sent to serial) to start reading from serial and press ENTER again to stop");
        String header = "";
        for(int i=0; i<nCol; i++){
            header += nombresCol[i]+"\t"; 
        }
        System.out.println(header);
        sc.nextLine();
        sc.nextLine();
        sSender.send("S\n".getBytes());
        sReceiver.setInputBufferAction(buffer);
        sPort.notifyOnDataAvailable(true);
        sc.nextLine();
        sPort.notifyOnDataAvailable(false);
        System.out.println();
        System.out.println(rowsData.size() + ", " + rowsData.get(0).size());
        boolean proceed = true;
        do{
            SelectorRutaXLS selector = new SelectorRutaXLS();
            if (selector.abrirSelector()) {
                String path = selector.getRuta();
                if(!path.endsWith(".xls"))
                    path = path + ".xls";
                try {
                        File xlsFile = new File(path);
                        boolean writeFile = true;
                        if (xlsFile.exists()) {
                            writeFile = false;
                            System.out.println("File already exists, do yo want to overwrite it? [y/n]");
                            String option = sc.nextLine().toLowerCase();
                            if(option.equals("y")){
                                writeFile = true;
                                xlsFile.delete();
                            }                        

                        }
                        if(writeFile){
                            xlsFile.createNewFile();
                            Workbook book = new HSSFWorkbook();
                            FileOutputStream file = new FileOutputStream(xlsFile);
                            Sheet sheet = book.createSheet("sheet 1");
                            sheet.setDisplayGridlines(true);

                            //Create cells
                            int nRow = rowsData.size();
                            for(int i=0; i<nRow; i++){
                                Row row = sheet.createRow(i);
                                for(int j=0; j<rowsData.get(i).size(); j++){
                                    Cell celda = row.createCell(j);
                                    switch(columnTypes[j]){
                                        case 1://Double
                                            celda.setCellValue((Double)rowsData.get(i).get(j));
                                            break;
                                        case 3://Boolean
                                            celda.setCellValue((Boolean)rowsData.get(i).get(j));
                                            break;
                                        default://String
                                            celda.setCellValue((String)rowsData.get(i).get(j));
                                            break;
                                    }                      
                                }
                            }

                            //Se escribe el file
                            book.write(file);
                            file.close();
                            System.out.println("Opening excel...");
                            Desktop.getDesktop().open(xlsFile);
                            System.out.println("\nSucessfully recorded");
                            System.out.println();
                        }
                        else{
                            System.out.println("Do you want to choose another path? [s/n]");
                            String option = sc.nextLine().toLowerCase();
                            if(!option.equals("n")){
                                proceed = false;
                            }
                        }

                    }catch (IOException ex) {
                            Logger.getLogger(ArduinoRec.class.getName()).log(Level.SEVERE, null, ex);
                    }

            }
        } while(!proceed);
             
    }
    
    public void printToStdout(){
        InputBufferAction buffer= new InputBufferAction(){
                @Override
                public void receivingActionPerformed(String buffer){
                    System.out.println(buffer);
                }
                
                @Override
                public void notReceivingActionPerformed(){
                    
                }

        };
        
        Scanner sc = new Scanner(System.in);
       
        System.out.println("\nTo start press ENTER (A string \"S\" is sent to serial) to start reading from serial and press ENTER again to stop");
        sc.nextLine();
        sSender.send("S\n".getBytes());
        sReceiver.setInputBufferAction(buffer);
        sPort.notifyOnDataAvailable(true);
        sc.nextLine();
        sPort.notifyOnDataAvailable(false);
    }

    public void printMenu() {
        System.out.println("\n\nOptions\n"
                + "excel: \tSave data in .xlsx file\n"
                + "print: \tPrint data to stdout\n\n"
                + "menu: \tMenu\n"
                + "quit: \tExit\n"
                + "\n");
    }
}
