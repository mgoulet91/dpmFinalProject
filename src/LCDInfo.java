import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;
import lejos.util.Timer;
import lejos.util.TimerListener;

public class LCDInfo implements TimerListener{
	public static final int LCD_REFRESH = 100;
	 static NXTRegulatedMotor claw = Motor.A;
	 static NXTRegulatedMotor lift = Motor.B;
	private Timer lcdTimer;
	
	// arrays for displaying data
	private double [] pos;
	
	public LCDInfo() {
		this.lcdTimer = new Timer(LCD_REFRESH, this);
		
		// initialise the arrays for displaying data
		
		// start the timer
		lcdTimer.start();
	}
	
	public void timedOut() { 
		
		LCD.clear();
		LCD.drawString("count: ", 0, 0);
		LCD.drawInt(claw.getTachoCount(), 3, 0);//need to change

	}
}
