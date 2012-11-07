package master;


import lejos.nxt.UltrasonicSensor;


// this class takes raw ultrasonic sensor readings and filters them
public class UltrasonicDataCollector {

	// object declaration
	private final UltrasonicSensor ultrasonicSensor;
	
	// fields
	private final int WALL_DISTANCE_THRESHOLD = 50;
	private int rawDistance;

	// constructor
	public UltrasonicDataCollector(UltrasonicSensor ultrasonicSensor) {
		
		this.ultrasonicSensor = ultrasonicSensor;
		ultrasonicSensor.capture();
		
	}
	// this method sets proximityWarning to true if an object has been detected
	public boolean proximityDetector() {
		
		// get current sensor value
		ultrasonicSensor.ping();
		rawDistance =  ultrasonicSensor.getDistance();
		
		// if the raw value is below wall threshold an object is detected
		if(rawDistance < WALL_DISTANCE_THRESHOLD) {
			return true;
		}
		
		return false;
		
		
	}
	
	
	
}