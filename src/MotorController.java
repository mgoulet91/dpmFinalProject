
import lejos.nxt.LightSensor;
import lejos.nxt.Sound;
import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

// this class handles all motor movement
public class MotorController {

	// object declaration
	private final NXTRegulatedMotor leftMotor;
	private final NXTRegulatedMotor rightMotor;
	private LightSensor lightSensor;
	private final Odometer odometer;
	
	// fields
	private final double RADIUS = 2.75;
	private final double WIDTH = 15.6;
	private final int FORWARD_SPEED = 250;
	private final int ROTATE_SPEED = 170;
	private final int SEARCH_SPEED = 170; 
	private final int ACCELERATION = 400; 
	
	// constructor
	public MotorController(NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, LightSensor lightSensor, Odometer odometer) {
		
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setAcceleration(ACCELERATION);
		rightMotor.setAcceleration(ACCELERATION);
		this.lightSensor = lightSensor;
		this.odometer = odometer;
		
	}
	

	// this method moves the robot forward until it reaches the distance given as argument
	public boolean forward(double distance) {
		
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);
			
		leftMotor.rotate(convertDistance(distance), true);
		rightMotor.rotate(convertDistance(distance), false);
		
		return true;
		
	}	
	
	// this method rotates the robot to the true bearing given as argument, returns true when complete
	public boolean rotate(double angle) {

		double test;
		
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		leftMotor.rotate(convertAngle(angle), true);
	
		rightMotor.rotate(-convertAngle(angle), false);
		
		
		return true;
		
	}
	
	
	// this method causes the robot to rotate clockwise indefinitely until motors are given other orders
	public void searchRotateCW() {
		
		leftMotor.setSpeed(SEARCH_SPEED);
		rightMotor.setSpeed(SEARCH_SPEED);
		
		leftMotor.forward();
		rightMotor.backward();
		
	}
	
	// this method causes the robot to rotate anticlockwise indefinitely until motors are given other orders
	public void searchRotateACW() {
		
		leftMotor.setSpeed(SEARCH_SPEED);
		rightMotor.setSpeed(SEARCH_SPEED);
		
		leftMotor.backward();
		rightMotor.forward();
		
	}
	
	// this method causes the robot to move forward indefinitely until motors are given other orders
	public void searchForward() {
		
		leftMotor.setSpeed(SEARCH_SPEED);
		rightMotor.setSpeed(SEARCH_SPEED);
		
		leftMotor.forward();
		rightMotor.forward();
		
	}
	
	// this method stops the robot, returns true when complete
	public boolean stop() {
		
		leftMotor.stop(true);
		rightMotor.stop(false);
		return true;
		
	}
	
	// this method returns true if the wheels are moving, false otherwise
	public boolean isMoving() {
		
		if(leftMotor.isMoving() || rightMotor.isMoving()) {
			return true;
		} else {
			return false;
		}
		
	}
	
	// this method causes the wheels to move forward with speed given as argument
	public void wheelSpeed(int leftSpeed, int rightSpeed) {
		
		leftMotor.setSpeed(leftSpeed);
		rightMotor.setSpeed(rightSpeed);
		
		leftMotor.forward();
		rightMotor.forward();
		
	}
	
	// this method does relevant conversions, taken from lab 2 code
	private int convertDistance(double distance) {
		return (int) ((180.0 * distance) / (Math.PI * RADIUS));		
	}

	// this method does relevant conversions, taken from lab 2 code
	private int convertAngle(double angle) {		
		return convertDistance(Math.PI * WIDTH * angle / 360.0);		
	}
	
}