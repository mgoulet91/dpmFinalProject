import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;
import lejos.util.*;

public class NXTMaster {

	public static void main(String[] args) throws Exception {
		String name = "Alpha";
		NXTCommConnector connector = RS485.getConnector() ;
		int mode = NXTConnection.PACKET; 
		
		LCD.clear();
		
		NXTConnection con = connector.connect(name,
				mode);
		
		int cases;

		if (con == null) {
			LCD.drawString("Connect fail", 0, 5);
			// sounds if no connection
			Sound.playTone(440, 80);
			try{Thread.sleep(80);}catch(Exception e){}
			Thread.sleep(2000);
			System.exit(1);
		}
		
		// LCD.drawString("Connected	", 0, 3);
		// LCD.refresh();

		// ------------------ Define data streams ------------------ //
		DataInputStream dis = con.openDataInputStream();
		DataOutputStream dos = con.openDataOutputStream();

		for (int i = 0; i < 100; i++) {
			try {
				
				dos.writeInt(i * 30000);
				dos.flush();
			} catch (IOException ioe) {
				LCD.drawString("Write Exception", 0, 5);
			}
			try {
				LCD.drawString("Read: ", 0, 7);
				LCD.drawInt(dis.readInt(), 8, 6, 7);
			} catch (IOException ioe) {
				LCD.drawString("Read Exception ", 0, 5);
			}
		}

		try {
			LCD.drawString("Closing...	", 0, 3);
			dis.close();
			dos.close();
			con.close();
		} catch (IOException ioe) {
			LCD.drawString("Close Exception", 0, 5);
			LCD.refresh();
		}
		LCD.drawString("Finished	", 0, 3);
		Thread.sleep(2000);
	}
}