package master;

import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

/**
 * This class defines all relevant properties of the robot
 * currently being used. Also defines various methods to find
 * the robot's displacement and heading, and setting the robot's
 * forward and rotation speeds.
 * 
 * @author Patrick Diez
 * @author Stephane Beniak - 260324952
 * 
 * @see Odometer
 * @see Navigation
 */
public class TwoWheeledRobot {
   
   /** Defines the left wheel radius used in Odometer calculations, in cm. */
   public static final double ODO_LEFT_RADIUS = 2.665;
   /** Defines the right wheel radius used in Odometer calculations, in cm. */
   public static final double ODO_RIGHT_RADIUS = 2.68;
   /** Defines the robot wheelbase used in Odometer calculations, in cm. */
   public static final double ODO_WIDTH = 17.61;
   /** Defines the left wheel radius used in Navigation calculations, in cm. */
   public static final double NAV_LEFT_RADIUS = 2.64;
   /** Defines the right wheel radius used in Navigation calculations, in cm. */
   public static final double NAV_RIGHT_RADIUS = 2.65;
   /** Defines the robot wheelbase used in Navigation calculations, in cm. */
   public static final double NAV_WIDTH = 17.42;
   
   private NXTRegulatedMotor leftMotor, rightMotor;
   private double forwardSpeed, rotationSpeed;
   
   /**
    * Constructor which defines the robot's basic properties.
    * 
    * @param a The left motor of the robot.
    * @param b The right motor of the robot.
    */
   public TwoWheeledRobot(NXTRegulatedMotor a, NXTRegulatedMotor b) {
      leftMotor = leftMotor;
      rightMotor = rightMotor;
   }
   
   /**
    * Resets the tachocounts of both motors.
    */
   public void resetTachos() {
      leftMotor.resetTachoCount();
      rightMotor.resetTachoCount();
   }
   
   /**
    * Calculates the displacement of the robot relative to its original position.
    * 
    * @return The robot's displacement, relative to the origin, in cm.
    */
   public double getDisplacement() {
      return (leftMotor.getTachoCount() * ODO_LEFT_RADIUS + rightMotor.getTachoCount() * ODO_RIGHT_RADIUS) * Math.PI / 360.0;
   }
   
   /**
    * Finds the robot's heading relative to its original orientation.
    * 
    * @return The angle in degrees between the robot's current orientation and its original one.
    */
   public double getHeading() {
      return (leftMotor.getTachoCount() * ODO_LEFT_RADIUS - rightMotor.getTachoCount() * ODO_RIGHT_RADIUS) / ODO_WIDTH;
   }
   
   /**
    * Finds the robot's displacement and heading relative to its original position.
    * 
    * @param data The array in which the position and heading are returned.
    */
   public void getDisplacementAndHeading(double [] data) {
      int leftTacho, rightTacho;
      leftTacho = leftMotor.getTachoCount();
      rightTacho = rightMotor.getTachoCount();
      
      data[0] = (leftTacho * ODO_LEFT_RADIUS + rightTacho * ODO_RIGHT_RADIUS) * Math.PI / 360.0;
      data[1] = (leftTacho * ODO_LEFT_RADIUS - rightTacho * ODO_RIGHT_RADIUS) / ODO_WIDTH;
   }
   
   /**
    * Sets both the forward and rotation speeds of the robot.
    * 
    * @param forwardSpeed The desired forward speed of the robot, in cm/s.
    * @param rotationalSpeed The desired rotation speed of the robot, in degrees/s.
    */
   public void setSpeeds(double forwardSpeed, double rotationalSpeed) {
      double leftSpeed, rightSpeed;
      
      this.forwardSpeed = forwardSpeed;
      this.rotationSpeed = rotationalSpeed; 
      
      leftSpeed = (forwardSpeed + rotationalSpeed * NAV_WIDTH * Math.PI / 360.0) * 180.0 / (NAV_LEFT_RADIUS * Math.PI);
      rightSpeed = (forwardSpeed - rotationalSpeed * NAV_WIDTH * Math.PI / 360.0) * 180.0 / (NAV_RIGHT_RADIUS * Math.PI);
      
      // set motor directions
      if (leftSpeed > 0.0) {
         leftMotor.forward();
      } else {
         leftMotor.backward();
         leftSpeed = -leftSpeed;
      }
      
      if (rightSpeed > 0.0) {
         rightMotor.forward();
      } else {
         rightMotor.backward();
         rightSpeed = -rightSpeed;
      }
      
      // set motor speeds
      if (leftSpeed > 900.0) {
         leftMotor.setSpeed(900);
      } else {
         leftMotor.setSpeed((int)leftSpeed);
      }
      
      if (rightSpeed > 900.0) {
         rightMotor.setSpeed(900);
      } else {
         rightMotor.setSpeed((int)rightSpeed);
      }
      
   }
   
}