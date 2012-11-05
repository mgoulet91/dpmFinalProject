import lejos.nxt.*;
import lejos.util.*;


public class run {
	
	public static void main(String [] args) {
		
					claw claw;
					Timer clawTimer;	
					
					claw = new claw(0 , 1);
					
					clawTimer = new Timer(500, claw);
					
					clawTimer.start();
					
					while(Button.readButtons() != Button.ID_ESCAPE);
					System.exit(0);
			
		}
	}

