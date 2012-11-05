import lejos.nxt.*;
import lejos.util.*;

/*	0: initial
 * 	1: open
 * 	2: close
 * 
 */
public class claw implements TimerListener{
	 static NXTRegulatedMotor claw = Motor.A;
	 static NXTRegulatedMotor lift = Motor.B;
	 
	 private int choice;
	 private int current;
	 
	 public claw(int current, int choice) {
		 this.choice = choice;
		 this.current = current;
		 
	 }
	
	 public void timedOut(){
		 clawControl(current,choice);
	 }

	 public static void clawControl(int current,int choice){
		
		 LCD.drawString("Running Claw Test", 0, 3);
		 
		 if (current == 0 && choice == 1){//from initial to open
				claw.setSpeed(50);
				claw.rotate(-224,false);
				claw.stop();
				current = 1;
				choice = 2;
				
			}

			if(current == 1 && choice == 2){//from open to close
				claw.setSpeed(50);
				claw.rotate(275);
				claw.stop();
				Motor.B.setSpeed(60);
				 Motor.B.rotate(-500);
				 System.exit(0);
			}

//			if(current == 1 && choice == 0){//from open to initial
//				claw.setSpeed(50);
//				claw.rotate(224);
//				claw.stop();
//			}

			if(current == 1 && choice == 0){//from close to open
				claw.setSpeed(50);
				claw.rotate(-275);
				claw.stop();
			}
	 } 
}
			 
			
				/*
				 claw.setSpeed(50);
				 claw.rotate(-224,false);
				 claw.rotate(275,false);
				 claw.stop();
				 Motor.B.setSpeed(60);
				 Motor.B.rotate(-720);
		 		*/
