import lejos.nxt.comm.NXTConnection;


public class MasterCommunication {
 
 /** 
  * ************************************************************************
  * ************************************************************************
  *       INTER-BRICK COMMUNICATION
  * ************************************************************************
  * ************************************************************************
  */
  

  /**
  * *******************************************************************
  *         open()
  *  ******************************************************************
  */
 public static void open(){
   while(true){
             try {
                     MainManager.output.writeInt(1);
                     MainManager.output.flush();
                     break;
             } 
             catch (Exception e){
                     int [] modes = {NXTConnection.PACKET, NXTConnection.RAW};
                     
                     MainManager.connection = MainManager.connector.connect("Alpha", modes [0]);
                     
                     try{ // Reconnection
                             MainManager.input = MainManager.connection.openDataInputStream();
                             MainManager.output = MainManager.connection.openDataOutputStream();
                     } 
                     catch(Exception err){
                     }
             }
   }
     
      //Get confirmation that command was executed
      while (true){
              try {
                      if (MainManager.input.readBoolean()) break;
              } 
              catch (Exception e) {
                      open(); //Recursive... 
                      break;
              }
      }
 }
  
 
 /**
  * *******************************************************************
  *         close()
  *  ******************************************************************
  */
 public static void close(){
   while(true){
            try {
                    MainManager.output.writeInt(2);
                    MainManager.output.flush();
                    break;
            } 
            catch (Exception e){
                    int [] modes = {NXTConnection.PACKET, NXTConnection.RAW};
                    
                    MainManager.connection = MainManager.connector.connect("Alpha", modes [0]);
                    
                    try{ // Reconnection
                            MainManager.input = MainManager.connection.openDataInputStream();
                            MainManager.output = MainManager.connection.openDataOutputStream();
                    } 
                    catch(Exception err){
                    }
            }
   }
    
      //Get confirmation that command was executed
      while (true){
              try {
                      if (MainManager.input.readBoolean()) break;
              } 
              catch (Exception e) {
                      close(); //Recursive... 
                      break;
              }
      }
 }
 
 /**
  * *******************************************************************
  *         lower()
  *  ******************************************************************
  */
 public static void lower(){
   while(true){
           try {
                   MainManager.output.writeInt(3);
                   MainManager.output.flush();
                   break;
           } 
           catch (Exception e){
                   int [] modes = {NXTConnection.PACKET, NXTConnection.RAW};
                   
                   MainManager.connection = MainManager.connector.connect("Alpha", modes [0]);
                   
                   try{ // Reconnection
                           MainManager.input = MainManager.connection.openDataInputStream();
                           MainManager.output = MainManager.connection.openDataOutputStream();
                   } 
                   catch(Exception err){
                   }
           }
   }
   
      //Get confirmation that command was executed
      while (true){
              try {
                      if (MainManager.input.readBoolean()) break;
              } 
              catch (Exception e) {
                      lower(); //Recursive... 
                      break;
              }
      }
 }
 
 
 /**
  * *******************************************************************
  *         raise()
  *  ******************************************************************
  */
 public static void raise(){
   while(true){
           try {
                   MainManager.output.writeInt(4);
                   MainManager.output.flush();
                   break;
           } 
           catch (Exception e){
                   int [] modes = {NXTConnection.PACKET, NXTConnection.RAW};
                   
                   MainManager.connection = MainManager.connector.connect("Alpha", modes [0]);
                   
                   try{ // Reconnection
                           MainManager.input = MainManager.connection.openDataInputStream();
                           MainManager.output = MainManager.connection.openDataOutputStream();
                   } 
                   catch(Exception err){
                   }
           }
   }
   
      //Get confirmation that command was executed
      while (true){
              try {
                      if (MainManager.input.readBoolean()) break;
              } 
              catch (Exception e) {
                      raise(); //Recursive... 
                      break;
              }
      }
 }
 
 
 /**
  * *******************************************************************
  *         getTopValue()
  *  ******************************************************************
  */
 public static int getTopValue(){
        int reading;
        
        //if(MainManager.obstacles){
                while(true){
                        try {
                                MainManager.output.writeInt(6);
                                MainManager.output.flush();
                                break;
                        } 
                        catch (Exception e){
                                int [] modes = {NXTConnection.PACKET, NXTConnection.RAW};

                                MainManager.connection = MainManager.connector.connect("Alpha", modes [0]);

                                try{
                                        MainManager.input = MainManager.connection.openDataInputStream();
                                        MainManager.output = MainManager.connection.openDataOutputStream();
                                } 
                                catch(Exception err){}
                        }
                }
                while (true){
                        try {
                                reading = MainManager.input.readInt();
                                break;
                        } catch (Exception e) {
                                int [] modes = {NXTConnection.PACKET, NXTConnection.RAW};

                                MainManager.connection = MainManager.connector.connect("Alpha", modes [0]);

                                try{
                                        MainManager.input = MainManager.connection.openDataInputStream();
                                        MainManager.output = MainManager.connection.openDataOutputStream();
                                } 
                                catch(Exception err){}
                                return getTopValue();
                        }
                }
        //}
        
        return reading + (int)MainManager.topUSDist;
 }

}
