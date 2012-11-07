
import lejos.nxt.LCD;

import lejos.util.Timer;
import lejos.util.TimerListener;
import lejos.nxt.LightSensor;

// this class provides main control of the three different modes and makes decisions on what to do when a mode is selected
public class Controller implements TimerListener {
	
	// object delcaration
	private final UltrasonicLocalizer ultrasonicLocalizer;
	private final LightSensor lightSensor;
	private final Odometer odometer;
	private final MotorController mc;
	private final Timer ultrasonicLocalizerTimer;
	
	// fields
	
	private final int US_LOCALIZER_PERIOD = 500;
	private boolean usLocalizeMode;
	private int programCounter;
	private Object modeLock;
	
	
	// constructor
	public Controller( UltrasonicLocalizer ultrasonicLocalizer, 
			 Odometer odometer, UltrasonicDataCollector ultrasonicDataCollector, MotorController m, LightSensor lightSensor) {
	
		this.ultrasonicLocalizer = ultrasonicLocalizer;
	

		this.lightSensor = lightSensor;
		this.odometer = odometer;
		this.ultrasonicLocalizerTimer = new Timer(US_LOCALIZER_PERIOD, ultrasonicLocalizer);
		this.mc = m;
		usLocalizeMode = false;	
		programCounter = 0;		
		modeLock = new Object();
		
	}
	
	// this method runs on a timeout
	public void timedOut() {
		
		// display relevant information
		updateLCD();
		
		// all modes are disabled, so make a decision
		if(!getUsLocalizerMode()) {
			// do ultrasonic localization first, then do light localization
				programCounter++;
		}
		
		// set modes according to program counter
		switch(programCounter) {
		case 1: 
			setUsLocalizerMode(true);
			break;
		default:
			setUsLocalizerMode(false);
			return;
		}
		

		// code for ultrasonic localization mode
		if(getUsLocalizerMode()) {
			
			// if the ultrasonic localizer is incomplete and already running, leave it alone
			if(!ultrasonicLocalizer.getUsLocalizerComplete() && ultrasonicLocalizer.getUsLocalizerRunning()) {
			
				return;
			}
			// if the ultrasonic localizer is complete but not running, it is resting, so switch it on
			else if(ultrasonicLocalizer.getUsLocalizerComplete() && !ultrasonicLocalizer.getUsLocalizerRunning()) {
				ultrasonicLocalizer.setUsLocalizerComplete(false);
				
				ultrasonicLocalizerTimer.start();
			}	
			// the process has been completed, switch out of localizer mode, stop the timer and put localizer into rest
			else if(ultrasonicLocalizer.getUsLocalizerComplete() && ultrasonicLocalizer.getUsLocalizerRunning()) {
				ultrasonicLocalizer.setUsLocalizerRunning(false);
				
				setUsLocalizerMode(false);
				ultrasonicLocalizerTimer.stop();
			}
			// procedure waiting to begin
			else {
				
			}	
			
		}	
			

				
	
	}

		
	// this method updates the LCD
	private void updateLCD() {
		
		LCD.clearDisplay();
		
		LCD.drawString(" Heading: " + String.valueOf((double)odometer.getTheta()), 0, 0);
		LCD.drawString("X coord: " + String.valueOf((double)odometer.getX()), 0, 1);
		LCD.drawString("Y coord:" + String.valueOf((double)odometer.getY()), 0, 2);
		LCD.drawString("Light: " + String.valueOf((double)lightSensor.getLightValue()), 0 , 6);
		
		if(getUsLocalizerMode()) {
			LCD.drawString(" ULTRASONIC ", 0, 3);	
	} else LCD.drawString("Doing nothing", 0, 3);
	
	}
	
	// this method returns the value of usLocalizeMode
	public boolean getUsLocalizerMode() {
		
		boolean result;
		
		synchronized (modeLock) {
			result = usLocalizeMode;
		}
		
		return result;
		
	}
	
	// this method sets the value of usLocalizeMode
	public void setUsLocalizerMode(boolean set) {

		synchronized (modeLock) {
			usLocalizeMode = set;
		}
		
	}
	


		
	}


