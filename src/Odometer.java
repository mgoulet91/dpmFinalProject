

import lejos.nxt.Motor;
import lejos.util.TimerListener;

// this class is the odometer carried over from lab 2
public class Odometer implements TimerListener {
	
	// fields
	private final double RADIUS = 2.75;
	private final double WIDTH = 15.6;
	private final double CONST1 = (Math.PI)*RADIUS/360;
	private final double CONST2 = ((Math.PI)*RADIUS)/(WIDTH*180);
	private final double RADTODEG = 180/(Math.PI);
	private final double DEGTORAD = (Math.PI)/180;
	
	private int oldLeftTachoCount = 0;
	private int oldRightTachoCount = 0;
	private int currentLeftTachoCount, currentRightTachoCount;
	private int deltaLeftTachoCount, deltaRightTachoCount;
	private double x, y, theta, dispMagnitude, dispAngle;
	private Object lock;

	
	// constructor
	public Odometer() {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		lock = new Object();
	}

	// this method runs on a timeout
	public void timedOut() {
		
		// update the current tacho count for each motor
		currentLeftTachoCount = Motor.A.getTachoCount();
		currentRightTachoCount = Motor.B.getTachoCount();	
		
		// calculate the change in tachos for each motor since last odometry reading, this is in degrees
		deltaLeftTachoCount = currentLeftTachoCount - oldLeftTachoCount;
		deltaRightTachoCount = currentRightTachoCount - oldRightTachoCount;
			
		// update the old tacho counts	
		oldLeftTachoCount = currentLeftTachoCount;
		oldRightTachoCount = currentRightTachoCount;
			
		// find the displacement magnitude and displacement angle
		dispMagnitude = CONST1*(deltaLeftTachoCount + deltaRightTachoCount);
		dispAngle = CONST2*(deltaLeftTachoCount - deltaRightTachoCount);

		synchronized (lock) {
			
			// update x, y, and theta, keep in mind that theta is expressed in degrees so make proper conversions
			x = x + dispMagnitude*(Math.sin(theta*DEGTORAD + (dispAngle/2))); 
			y = y + dispMagnitude*(Math.cos(theta*DEGTORAD + (dispAngle/2)));
			
			// make sure theta is non negative, representing a true compass bearing
			if((theta = (theta + (dispAngle*RADTODEG))%360) < 0) {
				theta = (theta + 360);
			}
			
		}

	}

	// this group of methods are used to access the fields, code taken from lab 2
	public void getPosition(double[] position, boolean[] update) {

		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
		
	}

	public double getX() {
		
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
		
	}

	public double getY() {
		
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
		
	}

	public double getTheta() {
		
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
		
	}

	// this group of methods are used to set the fields, code taken from lab 2
	public void setPosition(double[] position, boolean[] update) {

		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
		
	}

	public void setX(double x) {
		
		synchronized (lock) {
			this.x = x;
		}
		
	}

	public void setY(double y) {
		
		synchronized (lock) {
			this.y = y;
		}
		
	}

	public void setTheta(double theta) {
		
		synchronized (lock) {
			this.theta = theta;
		}
		
	}
	
}