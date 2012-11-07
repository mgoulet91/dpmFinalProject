import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;

import lejos.util.*;


public class NXTSlave
{
	public static NXTRegulatedMotor claw, clawArm;
	public static UltrasonicSensor topUS;
	
	public static void main(String [] args) throws IOException
	{
		// Initialises Motors and US
		claw = Motor.B;
		clawArm = Motor.C;
		topUS = new UltrasonicSensor(SensorPort.S1);
		
		// needed for connection
		String name = "FPFerrie";
		NXTCommConnector  connector =  Bluetooth.getConnector();
		int  mode[] = {NXTConnection.PACKET, NXTConnection.RAW} ;
		
		// command code sent my master brick
		int code;
		
		
		LCD.clear();
		
		while(true){ // establishes connection
		
			LCD.clear();
			LCD.drawString("Waiting...", 0, 0);
						
			NXTConnection connection =	connector.waitForConnection(0, mode[0]);
			
			LCD.clear();
			LCD.drawString("Connected...", 0, 0);
			
			
			// data input and output streams 
			DataInputStream input = connection.openDataInputStream();
			DataOutputStream output = connection.openDataOutputStream();
			
			while(true){ // wait for master brick
				
				try{
					code = input.readInt();
					
					// open claw
					if(code == 1){
						LCD.clear();
						LCD.drawString("Openinc claw...", 0, 0);
						
						Claw.open();
						
						// claw opened successful
						try {
							output.writeBoolean(true);
							output.flush();

							LCD.clear();
							LCD.drawString("Waiting...",0,0);
						}catch (Exception e){} // Exception Handling??
					}
					
					
					// close claw
					else if(code == 2){
						LCD.clear();
						LCD.drawString("close claw...", 0, 0);
						
						Claw.close();
						
						// claw closed successful
						try {
							output.writeBoolean(true);
							output.flush();

							LCD.clear();
							LCD.drawString("Waiting...",0,0);
						}catch (Exception e){} // Exception Handling??
						
					}
					
					
					// lower claw arm
					else if (code == 3){
						LCD.clear();
						LCD.drawString("close claw...", 0, 0);
						
						Arm.lower();
						
						// Claw Arm lowered successful
						try {
							output.writeBoolean(true);
							output.flush();

							LCD.clear();
							LCD.drawString("Waiting...",0,0);
						}catch (Exception e){}
					}
					
					// raise claw arm
					else if (code == 4){
						LCD.clear();
						LCD.drawString("raise arm...", 0, 0);
						
						Arm.raise();
						
						// Claw Arm raised successful
						try {
							output.writeBoolean(true);
							output.flush();

							LCD.clear();
							LCD.drawString("Waiting...",0,0);
						}catch (Exception e){}
					}
					
					
					
					// exit loop
					else if(code == 10){
						System.exit(0);
					}
				}catch (Exception e){}
				
				
				try{
					input.close();
					output.close();
					connection.close();
					
				}catch (Exception e){}
				
				
				break; // establishes connection again
			}
					
		}
	}
}


	

	
