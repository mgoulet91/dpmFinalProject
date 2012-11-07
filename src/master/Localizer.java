package master;

import lejos.nxt.LightSensor;
import lejos.robotics.navigation.Navigator;

/**
 * This class implements the ultrasonic sensor and light sensor localization routines.
 * US localization runs first and roughly orients the robot "North."
 * LS localization then runs, and accurately positions the robot at the origin,
 * pointing North, and resets the odometer to this position.
 * 
 * 
 * @see USPoller
 * @see LineDetector
 * @see GridSnapper
 * @see Odometer
 * @see LCDInfo
 */
public class Localizer {
   
   /** Defines the default localization forward speed, in cm/s. */
   public static final int FSPEED = 3;
   /** Defines the default localization rotation speed, in degrees/s. */
   public static final int RSPEED = 30;
   /** Defines the distance between each light sensor, in cm. */
   public static final double LSDIST = 23.6;
   /** Defines the offset between the lightsensor axis and wheel axis, in cm. */
   public static final double LSOFFSET = 1.5;
   /** Defines the wall distance threshold for US localization, in cm. */
   public static final int WALLDIST = 35;
   /** Defines the maximum light value threshold for a black line. */
   public static final int THRESHOLD = 460;
   
   private Odometer odo;
   private TwoWheeledRobot robot;
   private LightSensor lsL;
   private LightSensor lsR;
   private USPoller usp;

   
   /**
    * This constructor is to be passed the odometer, and all the sensors 
    * required to perform localization.
    * 
    * @param odo_ The odometer tracking the robot's position.
    * @param lsL_ The light sensor on the left of the robot.
    * @param lsR_ The light sensor on the right of the robot.
    * @param usp_ The ultrasonic sensor mounted on the top of the robot.
    */
   public Localizer(Odometer odo_, LightSensor lsL_, LightSensor lsR_, USPoller usp_) {
      
      odo = odo_;
      robot = odo.getTwoWheeledRobot();
      lsL = lsL_;
      lsR = lsR_;
      usp = usp_;
      
      // Turn off the lights
      lsL.setFloodlight(false);
      lsR.setFloodlight(false);
   }
   
   /**
    * Performs light sensor based localization to accurately
    * position the robot over the origin.
    */
   private void doLSLocalization(double x, double y, double h, boolean xDirection) {
      
      double angleC, distPastLine;
      double line1, line2;
      
      int lightL = 1000, lightR = 1000;
      boolean leftFirst;
      
      // Turn on the floodlights
      lsL.setFloodlight(true);
      lsR.setFloodlight(true);
      
      // Back up a little bit
      Navigation.goForward(odo, -10);
      
      // Drive up to the line
      robot.setSpeeds(FSPEED, 0);
      
      while(lightL > THRESHOLD && lightR > THRESHOLD){
         lightL = lsL.getNormalizedLightValue();
         lightR = lsR.getNormalizedLightValue();
      }
      
      // Latch y-position of first LS line hit
      robot.setSpeeds(0, 0);
      line1 = odo.getY();
      

      
      // Back up a bit
      Navigation.goForward(odo, -3);
      
      // If the left LS hit first, turn its light off and poll the right one
      // until it hits a line, or else vice versa.
      if (lightL <= THRESHOLD){
         leftFirst = true;
         lsL.setFloodlight(false);
         
         lightR = 1000;
         
         robot.setSpeeds(FSPEED, 0);
         
         while(lightR > THRESHOLD){
            lightR = lsR.getNormalizedLightValue();
         }
         
      } else {
         leftFirst = false;
         lsR.setFloodlight(false);
         
         lightL = 1000;
         
         robot.setSpeeds(FSPEED, 0);
         
         while(lightL > THRESHOLD){
            lightL = lsL.getNormalizedLightValue();
         }
         
      }
      
      // latch y-position of second LS line hit
      robot.setSpeeds(0, 0);
      line2 = odo.getY();
      

      
      // Compute the angle error of the robot.
      angleC = Trig.atan((line2 - line1)/LSDIST);
      distPastLine = (LSDIST/2)*Trig.sin(angleC);
      
      // Turn to correct angle.
      if (leftFirst)
         Navigation.turnTo(odo, (xDirection ? 90 - angleC : -angleC));
      else
         Navigation.turnTo(odo, (xDirection ? 90 + angleC : angleC));
      
      // Back up, to end up exactly over the line.
      Navigation.goForward(odo, -(distPastLine + LSOFFSET));
      
      // Set new, correct position
      odo.setPosition(new double [] {x, y, h}, new boolean [] {xDirection, !xDirection, true});
      
      // Turn off the lights
      lsL.setFloodlight(false);
      lsR.setFloodlight(false);
      

   }
   
   /**
    * Localizes the robot using the black gridlines on the floor.
    * The robot will end up centered on the given point, facing North.
    * This method assumes the robot is facing roughly North initially.
    * 
    * @param x The robot's new x coordinate, in cm.
    * @param y The robot's new y coordinate, in cm.
    * @param h The robot's new orientation, in degrees.
    */
   public void gridSnapTo(double x, double y, double h){
      
      // Localize y
      doLSLocalization(x, y, h, false);
      Navigation.turnTo(odo, 90);
      // Localize x
      doLSLocalization(x, y, h, true);
      Navigation.turnTo(odo, -90);
      // Set new position
      odo.resetMotorTachos();
      odo.setPosition(new double [] {x, y, h}, new boolean [] {true, true, true});
      
   }
   
   /**
    * Performs ultrasonic sensor based localization (falling edge) to have the 
    * robot oriented roughly North for the LS localization.
    */
   public void doUSLocalization() {
      
      double angleA, angleB, angleC;
      
      try { Thread.sleep(250); } catch (Exception e) {}
      
      // If the robot start in front of a wall, rotate 135 degrees away from it
      // to get away from the corner, which often causes trouble for the us sensors.
      if (usp.getDistLo() < WALLDIST + 5)
         Navigation.turnRelative(odo, 135);
      
      // rotate the robot until it sees no wall
      robot.setSpeeds(0, RSPEED);
      while (usp.getDistLo() < WALLDIST + 5 || usp.getDistHi() < WALLDIST + 5);
      
      // keep rotating until the robot sees a wall, then latch the angle
      while (usp.getDistLo() > WALLDIST || usp.getDistHi() > WALLDIST);
      angleA = odo.getTheta();
      
 
      
      // Turn away from the current wall
      Navigation.turnRelative(odo, -90);
      
      // switch direction and wait until it sees no wall
      robot.setSpeeds(0, -RSPEED);
      while (usp.getDistLo() < WALLDIST + 5 || usp.getDistHi() < WALLDIST + 5);
      
      // keep rotating until the robot sees a wall, then latch the angle
      while (usp.getDistLo() > WALLDIST || usp.getDistHi() > WALLDIST);
      angleB = odo.getTheta();
      
   
      
      // angleA is clockwise from angleB, so assume the average of the
      // angles to the right of angleB is 45 degrees past 'north'
      if (angleA < angleB)
         Navigation.turnTo(odo, (angleA + angleB + 270) / 2);
      else
         Navigation.turnTo(odo, (angleA + angleB - 90) / 2);
      
      // update the odometer position
      odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
      

   }
   
}