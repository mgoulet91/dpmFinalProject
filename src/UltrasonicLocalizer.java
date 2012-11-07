
//import lejos.nxt.comm.RConsole;
import lejos.nxt.LCD;
import lejos.util.TimerListener;

// this class carries out localization using the ultrasonic sensor
public class UltrasonicLocalizer implements TimerListener{

	// object declaration
	private final MotorController motorController;
	private final Odometer odometer;
	private final UltrasonicDataCollector ultrasonicDataCollector;
	
	// fields
	public enum LocalizationType {FALLING_EDGE, RISING_EDGE};
	private final double THETA_CORRECTION_CONSTANT = 18;
	private boolean usLocalizerComplete;
	private boolean usLocalizerRunning;
	private double angleA, angleB, offsetTheta;
	private Object modeLock;
	
	// constructor
	public UltrasonicLocalizer(MotorController motorController, Odometer odometer, UltrasonicDataCollector ultrasonicDataCollector) {
		
		this.motorController = motorController;
		this.odometer = odometer;
		this.ultrasonicDataCollector = ultrasonicDataCollector;
		
		usLocalizerComplete = true;
		usLocalizerRunning = false;
		modeLock = new Object();
		
	}
	
	
	// this method runs on a timeout
	public void timedOut() {
		
		// if localization is incomplete and is not running, begin a new localization procedure
		if(!getUsLocalizerComplete() && !getUsLocalizerRunning()) {
			setUsLocalizerRunning(true);
			doLocalization(UltrasonicLocalizer.LocalizationType.FALLING_EDGE);
			setUsLocalizerComplete(true);			
		}
		 
	}
	
	// this method carries out the localization of type given as argument
	public void doLocalization(LocalizationType localizationType) {
		 LCD.drawString("Finding Heading", 0, 3);
		if(localizationType == LocalizationType.FALLING_EDGE) {
			
			// bring robot to complete stop to begin falling edge localization
			while(!motorController.stop());
			
			// first stage is to rotate the robot until no wall is seen	
			while(ultrasonicDataCollector.proximityDetector()) {
				motorController.searchRotateCW();
			}
		
			// second stage is to keep rotating until a wall is seen	
			while(!ultrasonicDataCollector.proximityDetector()) {
				motorController.searchRotateCW();
			}
			angleA =odometer.getTheta();
			
			// third stage is to rotate the other way until no wall is seen
			while(ultrasonicDataCollector.proximityDetector()) {
				motorController.searchRotateACW();
			}			
			
			
			// fourth stage is to keep rotating until a wall is seen
			while(!ultrasonicDataCollector.proximityDetector()) {
				motorController.searchRotateACW();
			}
			angleB = odometer.getTheta();
			
			// calculate the offset angle to be added to the odometry bearing
			if(angleA < angleB) {
				offsetTheta = 225 - (angleA + angleB )/2;
			}
			else {
				offsetTheta = 405 - (angleA + angleB )/2;
			}
			
			// update odometer and add a correction constant to take into account the non-ideal sensor
			odometer.setTheta(odometer.getTheta()+offsetTheta+THETA_CORRECTION_CONSTANT);
			
			// rotate to a true bearing of zero
		
			turnTo(0);
			
			
			// check to see if motors have come to a stop, if so, switch off localizerRunning so that controller knows that the process is complete 
			while(motorController.isMoving());
			LCD.clear();
			 LCD.drawString("theta" + odometer.getTheta(), 0, 3);
			return;
			
			
		} else {
			
			// bring robot to complete stop to begin rising edge localization
			while(!motorController.stop());
			
			// first stage is to rotate the robot until a wall is seen	
			while(!ultrasonicDataCollector.proximityDetector()) {
				motorController.searchRotateACW();
			}
		

		
			// second stage is to keep rotating until no wall is seen	
			while(ultrasonicDataCollector.proximityDetector()) {
				motorController.searchRotateACW();
			}
			angleA =odometer.getTheta();
			
			// third stage is to rotate the other way until a wall is seen
			while(!ultrasonicDataCollector.proximityDetector()) {
				motorController.searchRotateCW();
			}			
			
			// fourth stage is to keep rotating until no wall is seen
			while(ultrasonicDataCollector.proximityDetector()) {
				motorController.searchRotateCW();
			}
			angleB = odometer.getTheta();
			
			// calculate the offset angle to be added to the odometry bearing
			if(angleA < angleB) {
				offsetTheta = 225 - (angleA + angleB )/2;
			}
			else {
				offsetTheta = 405 - (angleA + angleB )/2;
			}

			// update odometer and add a correction constant to take into account the non-ideal sensor
			odometer.setTheta(odometer.getTheta()+offsetTheta+THETA_CORRECTION_CONSTANT);
			
			// rotate to a true bearing of zero
	
			turnTo(0);
			
			// check to see if motors have come to a stop, if so, switch off localizerRunning so that controller knows that the process is complete 
			while(motorController.isMoving());
			return;		
			
		}

	}

	// this method returns the value of uslocalizerComplete
	public boolean getUsLocalizerComplete() {
		
		boolean result;
		
		synchronized (modeLock) {
			result = usLocalizerComplete;
		}
		
		return result;
		
	}

	// this method sets the value of uslocalizerComplete
	public void setUsLocalizerComplete(boolean set) {
		
		synchronized (modeLock) {
			usLocalizerComplete = set;
			
		}
	}
	
	// this method returns the value of uslocalizerRunning
	public boolean getUsLocalizerRunning() {
		
		boolean result;
		
		synchronized (modeLock) {
			result = usLocalizerRunning;
		}
		
		return result;
		
	}

	// this method sets the value of uslocalizerRunning
	public void setUsLocalizerRunning(boolean set) {
		
		synchronized (modeLock) {
			usLocalizerRunning = set;
			
		}
	}
	
	// this method rotates the robot to the desired true bearing
	public void turnTo(double theta) {
		
		double rotation = theta - odometer.getTheta();
		
		// make a minimal rotation
		if (rotation > 180) {
			while(!motorController.rotate(rotation - 360));
		}
		
		else if (rotation < -180) {
			while(!motorController.rotate(rotation + 360));
		}
		
		else while(!motorController.rotate(rotation));
		
	}

}
