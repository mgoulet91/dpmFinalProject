

public class Claw {
	final static int CLAWSPEED=60; //if motor claw is set to forward, it closes the claw
    final static int CLOSETIME=9167;
	
	/**
	 * open:
	 * 
	 * Parameters: none
	 * Output: none
	 * Purpose: opens claw
	 * 
	 */
	public static void open(){
		
	    // open the claw to release the ball
	    NXTSlave.claw.setSpeed(CLAWSPEED);
	    NXTSlave.claw.forward();
	    
	    try{Thread.sleep(CLOSETIME);}catch(Exception e){}
	    
	    NXTSlave.claw.setSpeed(0);
		
	}
	
	
	/**
	 * close:
	 * 
	 * Parameters: none
	 * Output: none
	 * Purpose: closes claw and collects ball
	 */
	public static void close()
	{
		// close the claw
		NXTSlave.claw.setSpeed(CLAWSPEED);
		NXTSlave.claw.backward();
		
	    try{Thread.sleep(CLOSETIME);}catch(Exception e){}
	    
	    NXTSlave.claw.setSpeed(0);
	}
	
}
