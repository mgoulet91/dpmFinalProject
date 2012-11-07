package master;

import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * This class tracks the movement of the robot around the course,
 * by first tracking the tachometer of each motor, and then through
 * various mathematical techniques, constantly updating the robot's
 * x, y coordinates, and orientation h.
 *
 * 
 * @see Navigation
 * @see GridSnapper
 * @see TwoWheeledRobot
 */
public class Odometer implements TimerListener {
   
   /** Defines the default time period between Odometer position updates, in ms. */
   public static final int DEFAULT_PERIOD = 25;
   
   private TwoWheeledRobot robot;
   private Timer odometerTimer;
   
   // Position data
   private Object lock;
   private double x, y, theta;
   private double [] oldDH, dDH;
   
   // Node and direction data
   private int nodeX = 0, nodeY = 0, direction = 0;
   private int i = 0;
   
   /**
    * Contructor that defines a new Odometer assigned to a specific robot,
    * and polls the tachometers at a set rate.
    * 
    * @param prawnBot The robot whose motion will be tracked.
    * @param period The polling rate of the odometer, in ms.
    * @param start If true, the odometer starts polling immediately.
    */
   public Odometer(TwoWheeledRobot prawnBot, int period, boolean start) {
      
      robot = prawnBot;
      odometerTimer = new Timer(period, this);
      x = 0.0;
      y = 0.0;
      theta = 0.0;
      oldDH = new double [2];
      dDH = new double [2];
      lock = new Object();
      
      // start the odometer immediately, if necessary
      if (start)
         odometerTimer.start();
   }
   
   /**
    * Constructor which is only passed a robot; the default refresh
    * rate will be used, and it will not start automatically.
    */
   public Odometer(TwoWheeledRobot robot) {
      this(robot, DEFAULT_PERIOD, false);
   }
   
   /**
    * Constructor which is passed a robot and start boolean;
    * the default refresh rate will be used.
    */
   public Odometer(TwoWheeledRobot robot, boolean start) {
      this(robot, DEFAULT_PERIOD, start);
   }
   
   /**
    * Constructor which is passed a robot and polling rate;
    * it will not start automatically
    */
   public Odometer(TwoWheeledRobot robot, int period) {
      this(robot, period, false);
   }
   
   /**
    * The method which does the actual math behind the odometer.
    * Using each motor's tachometer, calculates the change in position
    * and orientation of the robot, and adds it to the previous values,
    * updating it accordingly.
    */
   public void timedOut() {
      robot.getDisplacementAndHeading(dDH);
      dDH[0] -= oldDH[0];
      dDH[1] -= oldDH[1];
      
      // update the position in a critical region
      synchronized (lock) {
         x += dDH[0] * Math.sin((oldDH[1] + dDH[1] / 2.0) * Math.PI / 180.0);
         y += dDH[0] * Math.cos((oldDH[1] + dDH[1] / 2.0) * Math.PI / 180.0);
         theta += dDH[1];
         
         // keep theta between 0 and 360
         if (theta < 0.0)
            theta += 360.0;
         else if (theta >= 360.0)
            theta -= 360.0;
         
         // Only updates node and direction data every 10 odometer cycles
         if (i > 9) {
            
            nodeX = (int)((x + 12.0) / 30.48);
            nodeY = (int)((y + 12.0) / 30.48);
            direction = (int)(((theta + 44.0) / 90.0) % 4);
         }
         
         i++;
         i %= 11;
         
      }
      
      oldDH[0] += dDH[0];
      oldDH[1] += dDH[1];
      
   }
   
   /**
    * Method which makes the current position values of
    * the robot accessible.
    * 
    * @param pos The array of double in which the robot's position is returned.
    */
   public void getPosition(double [] pos) {
      synchronized (lock) {
         pos[0] = x;
         pos[1] = y;
         pos[2] = theta;
      }
   }
   
   /**
    * Get the x position of the robot.
    * 
    * @return The robot's x coordinate, in cm.
    */
   public double getX() {
      synchronized (lock) {
         return x;
      }
   }
   
   /**
    * Get the y position of the robot.
    * 
    * @return The robot's y coordinate, in cm.
    */
   public double getY() {
      synchronized (lock) {
         return y;
      }
   }
   
   /**
    * Get the orientation of the robot, relative to North.
    * 
    * @return The angle the robot is facing, in degrees.
    */
   public double getTheta() {
      synchronized (lock) {
         return theta;
      }
   }
   
   /**
    * Get the nodal x position of the robot.
    * 
    * @return The robot's x node coordinate, in "feet".
    */
   public int getNodeX() {
      synchronized (lock) {
         return nodeX;
      }
   }
   
   /**
    * Get the nodal y position of the robot.
    * 
    * @return The robot's y node coordinate, in "feet".
    */
   public int getNodeY() {
      synchronized (lock) {
         return nodeY;
      }
   }
   
   /**
    * Get the orientation of the robot, as a direction.
    * 
    * @return The robot direction (0 = N, 1 = E, 2 = S, 3 = W).
    */
   public int getDirection() {
      synchronized (lock) {
         return direction;
      }
   }
   
   /**
    * Allows access to the robot for which this odometer is working.
    * 
    * @return The robot whose odometry is being calculated.
    */
   public TwoWheeledRobot getTwoWheeledRobot() {
      return robot;
   }
   
   /**
    * Reset the motors' tacho counts, and odometer data, to zero
    */
   public void resetMotorTachos() {
      robot.resetTachos();
      oldDH[0] = 0;
      oldDH[1] = 0;
      dDH[0] = 0;
      dDH[1] = 0;
   }
   
   /**
    * Allows the robot's position to be reset by the user.
    * 
    * @param pos The position values to be set.
    * @param update If true, updates the corresponding coordinate.
    */
   public void setPosition(double [] pos, boolean [] update) {
      synchronized (lock) {
         if (update[0]) x = pos[0];
         if (update[1]) y = pos[1];
         if (update[2]) theta = pos[2];
      }
   }
   
   /**
    * Allows the robot's orientation to be reset by the user.
    * 
    * @param angle The new orientation of the robot, in degrees.
    */
   public void setTheta(double angle) {
      synchronized (lock) {
         theta = angle;
      }
   }
   
   /**
    * Sets a given angle to be between 0 and 360 degrees
    * 
    * @param angle The angle to be corrected.
    */
   public static double fixAngle(double angle) {  
      if (angle < 0.0)
         angle = 360.0 + (angle % 360.0);
      
      return angle % 360.0;
   }
   
   /**
    * Finds the most efficient way for the robot to turn from
    * one angle to another; never more than 180 degrees.
    * 
    * @param a The initial angle in degrees.
    * @param b The final angle in degrees.
    * 
    * @return The shortest rotation required to go from a to b.
    */
   public static double minimumAngleFromTo(double a, double b) {
      double d = fixAngle(b - a);
      
      if (d < 180.0)
         return d;
      else
         return d - 360.0;
   }
   
}