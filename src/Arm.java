
public class Arm {
	final static int ARMSPEED=50;
    final static int RAISETIME=5400;
    
    /**
     * lower:
     * 
     * Parameters: none
     * Output: none
     * Purpose: lowers claw in order to collect the ball
     * 
     */
    public static void lower(){
    	
    	NXTSlave.clawArm.setSpeed(ARMSPEED);
    	NXTSlave.clawArm.forward();
	    
	    try{Thread.sleep(RAISETIME);}catch(Exception e){}
	    NXTSlave.clawArm.setSpeed(0);
    }
    
    
    /**
     * raise:
     * 
     * Parameters: none
     * Output: none
     * Purpose: raises claw and puts balls in basket; default position
     * 
     */
    public static void raise(){
    	
    	NXTSlave.clawArm.setSpeed(ARMSPEED);
    	NXTSlave.clawArm.backward();
	    
	    try{Thread.sleep(RAISETIME);}catch(Exception e){}
	    NXTSlave.clawArm.setSpeed(0);
    }
}
