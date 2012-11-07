package master;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.Button;

/**
 * Set of static methods to control the movement of the robot around the course.
 * Implements measured forward motion and rotation, as well as vectored movement
 * which circumnavigates any obstacles encountered along the way.
 * 
 * @author Patrick Diez
 * @author Stephane Beniak - 260324952
 * @author Farah Hanani Shamsuddin, Azraf Hipni
 * 
 * @see Odometer
 * @see TwoWheeledRobot
 */
public class Navigation {
   
   /** Defines the x coordinate of the lower left corner of the dropoff zone. */
   public static final int DROPOFF_X = 9;
   /** Defines the y coordinate of the lower left corner of the dropoff zone. */
   public static final int DROPOFF_Y = 9;
   
   /** Defines the default forward speed, in cm/s. */
   public static final double FSPEED = 7.0;
   /** Defines the default rotation speed, in degrees/s. */
   public static final double RSPEED = 50.0;
   /** Defines the maximum tolerable error when turning, in degrees. */
   public static final double ROTATION_TOLERANCE = 0.5;
   /** Defines the minimum obstacle clearance needed for the robot, in cm. */
   public static final int CLEARANCE_NEAR = 25;
   /** Defines the minimum distance required for the robot to move forward, in cm. */
   public static final int CLEARANCE_FAR = 55;
   /** Defines the minimum distance difference required to recognize a pallet from a wall, in cm. */
   public static final int BLOCK_WALL_DIFF = 15;
   /** Defines the arc to be swept, in degrees. */
   public static final int SWEEP_ARC = 90;
   
   private static double angleP;
   
   /**
    * Goes forward for a specified distance. This is blocking, hence it will 
    * prevent the following line of code from executing until completion.
    * 
    * @param odo The odometer tracking the robot's movement.
    * @param distance The desired distance in cm to be travelled forward.
    */
   public static void goForward(Odometer odo, double distance) {
      
      TwoWheeledRobot robot = odo.getTwoWheeledRobot();
      double [] initPos = new double [3], currPos = new double [3];
      
      // stop any rotational motion
      robot.setSpeeds(0, 0);
      
      // latch the initial position
      odo.getPosition(initPos);
      odo.getPosition(currPos);
      
      // start the motors and wait to reach the appropriate distance from
      // the initial position
      if (distance < 0.0)
         robot.setSpeeds(-FSPEED, 0);
      else
         robot.setSpeeds(FSPEED, 0);
      
      while ((currPos[0] - initPos[0]) * (currPos[0] - initPos[0]) +
             (currPos[1] - initPos[1]) * (currPos[1] - initPos[1]) <
             distance * distance) {
         odo.getPosition(currPos);
      }
      
      // stop the motors
      robot.setSpeeds(0, 0);
      
   }
   
   /**
    * Rotates the robot around itself for a specified angle. It will 
    * always turn efficiently, meaning never more than 180 degrees.
    * As with goForward(), this method is blocking.
    * 
    * @param odo The odometer tracking the robot's movement.
    * @param angle The desired angle in degrees to which the robot should rotate.
    */
   public static void turnTo(Odometer odo, double angle) {
      
      TwoWheeledRobot robot = odo.getTwoWheeledRobot();
      double [] currPos = new double [3];
      double currSpeed = RSPEED;
      double angDiff;
      
      // stop any forward motion
      robot.setSpeeds(0, 0);
      
      // latch the initial position
      odo.getPosition(currPos);
      angDiff = Odometer.minimumAngleFromTo(currPos[2], angle);
      if (angDiff > 0.0)
         robot.setSpeeds(0, currSpeed);
      else
         robot.setSpeeds(0, currSpeed *= -1);
      
      // turn to the appropriate angle
      while (Math.abs(angDiff) > ROTATION_TOLERANCE) {
         if (currSpeed > 0.0 && angDiff < 0.0)
            robot.setSpeeds(0, currSpeed *= -0.5);
         else if (currSpeed < 0.0 && angDiff > 0.0)
            robot.setSpeeds(0, currSpeed *= -0.5);
         
         odo.getPosition(currPos);
         angDiff = Odometer.minimumAngleFromTo(currPos[2], angle);
      }
      
      // stop the motors
      robot.setSpeeds(0, 0);
   }
   
   /**
    * This method allows the robot to turn a certain angle
    * relative to its current position.
    * 
    * @param odo The odometer tracking the robot's movement.
    * @param angle The desired relative angle in degrees the robot should rotate.
    */
   public static void turnRelative(Odometer odo, double angle){
      
      // Latch current angle
      double h = odo.getTheta();
      
      // Turn set number of degrees relative to current angle.
      turnTo(odo, h + angle);
   }
   
   /**
    * This method allows the robot to turn an arbitrary number of degrees.
    * This method will not choose the "most efficient way," so it is possible
    * to turn a complete 720, for example. It is non-blocking, allowing subsequent
    * code to execute while the robot turns.
    * 
    * @param odo The odometer that tracks the robot's motion
    * @param angle The angle in degrees that the robot should rotate.
    */
   public static void turn(Odometer odo, double angle){
      
      TwoWheeledRobot robot = odo.getTwoWheeledRobot();
      
      int rotdegs = (int)((angle * (robot.NAV_WIDTH + 0.5)) / (robot.NAV_LEFT_RADIUS + robot.NAV_RIGHT_RADIUS));
      
      //Motor.A.setSpeed((int)RSPEED*3);
      //Motor.B.setSpeed((int)RSPEED*3);
      
      Motor.A.rotate(rotdegs, true);
      Motor.B.rotate(-rotdegs, true);
   }
   
   /**
    * This method allows the robot to rotate about one of its wheels
    * for a set angle.
    * 
    * @param odo The odometer that tracks the robot's motion.
    * @param angle The angle in degrees that the robot should rotate.
    * @param left If true, rotates about the left wheel.
    */
   public static void turnAboutWheel(Odometer odo, double angle, boolean left){
      
      TwoWheeledRobot robot = odo.getTwoWheeledRobot();
      
      // Calculates the require tachometer change
      int rotdegs = (int)((2 * angle * robot.NAV_WIDTH) / (robot.NAV_LEFT_RADIUS + robot.NAV_RIGHT_RADIUS));
      
      if (left) { 
         Motor.A.setSpeed(0);
         Motor.B.setSpeed((int)RSPEED*4);
         Motor.B.rotate(rotdegs);
      } else {
         Motor.A.setSpeed((int)RSPEED*4);
         Motor.B.setSpeed(0);
         Motor.A.rotate(rotdegs);
      }
      
   }
   
   /**
    * Makes the robot navigate to the specified point, while circumnavigating
    * obstacles along the way. Travels only in right angles (along x and y)
    * to preserve odometry as best as possible and simplify movement.
    * 
    * @param odo The odometer tracking the robot's movement.
    * @param usp The robot's ultrasonic polling system.
    * @param px The x coordinate of the desired destination, in cm.
    * @param py The y coordinate of the desired destination, in cm.
    */
   public static void goToPoint(Odometer odo, USPoller usp, double px, double py){
      double [] pos = new double [3];
      double x, y, error;
      
      // Calculate error between current location and desired point
      odo.getPosition(pos);
      x = pos[0];
      y = pos[1];
      error = Math.sqrt(((px - x)*(px - x) + (py - y)*(py - y)));
      
      turnTo(odo, (px > x ? 90 : 270));
      
      // Repeat goTowards() until error is less than 1cm
      while (error > 1.0) {
         
         goTowardsX(odo, usp, px, py);
         
         // Calculate error between current location and desired point
         odo.getPosition(pos);
         x = pos[0];
         y = pos[1];
         error = Math.sqrt(((px - x)*(px - x) + (py - y)*(py - y)));
         
         if (error > 1.0) {
         
            // Turn to face the correct direction.
            turnTo(odo, (py > y ? 0 : 180));
            
            goTowardsY(odo, usp, px, py);
            
            // Calculate error between current location and desired point
            odo.getPosition(pos);
            x = pos[0];
            y = pos[1];
            error = Math.sqrt(((px - x)*(px - x) + (py - y)*(py - y)));
            
            if (error > 1.0) {
               
               // Turn to face the correct direction.
               turnTo(odo, (px > x ? 90 : 270));
               
            }
            
         }
         
      }
      
   }
   
   /**
    * A submethod of goToPoint(), this method consists of a single
    * forward motion (along the x axis).
    * The forward motion ends when the x-coordinates match, or 
    * when an obstacle is encountered.
    * 
    * @param odo The odometer tracking the robot's movement.
    * @param usp The robot's ultrasonic poller
    * @param px The x coordinate of the desired destination, in cm.
    * @param py The y coordinate of the desired destination, in cm.
    */
   public static void goTowardsX(Odometer odo, USPoller usp, double px, double py){
      TwoWheeledRobot robot = odo.getTwoWheeledRobot();
      double x, y;
      int distL, distR;
      boolean cinder = false, pathBlocked = false;
      
      // get current positions
      x = odo.getX();
      y = odo.getY();
      
      
      // If we're moving left, else we're going right
      if (px > x) {
         
         // While the path is clear for 31 cm, and coordinates don't match...
         while (px - x > 31 && !cinder) {
            
            turnRelative(odo, -30);
            distL = usp.getDistHi();
            turnRelative(odo, 60);
            distR = usp.getDistHi();
            turnRelative(odo, -30);
            
            // If path is clear, go. Else, exit loop and change direction.
            if(distL > CLEARANCE_FAR && distR > CLEARANCE_FAR)
               go(odo);
            else
               cinder = true;
            
            // update position
            x = odo.getX();
         }
         
         robot.setSpeeds(FSPEED, 0);
         
         // For the reMainManagering distance ( > 31cm), drive straight.
         while (x < px && usp.getDistHi() > CLEARANCE_NEAR){
            x = odo.getX();
         }
         
         robot.setSpeeds(0, 0);
         
      } else {
         
         // Same logic as above, for moving right instead of left
         while (x - px > 31 && !cinder) {
            
            Navigation.turnRelative(odo, -30);
            distL = usp.getDistHi();
            Navigation.turnRelative(odo, 60);
            distR = usp.getDistHi();
            Navigation.turnRelative(odo, -30);
            
            if(distL > CLEARANCE_FAR && distR > CLEARANCE_FAR)
               go(odo);
            else
               cinder = true;
            
            x = odo.getX();
         }
         
         robot.setSpeeds(FSPEED, 0);
         
         while ((x > px && usp.getDistHi() > CLEARANCE_NEAR)){
            x = odo.getX();
         }
         
         robot.setSpeeds(0, 0);
         
      }
      
      // update position
      y = odo.getY();
      
      // If coordinates match within an error bound, and and obstacle is
      // in the way, go around the obstacle and continue.
      if ((y > (py - 10) && y < (py + 10)) && cinder) {
         Navigation.goAroundObstacle(odo, usp);
      }
      
   }
   
   /**
    * A submethod of goToPoint(), this method consists of a single
    * forward motion (along the y axis).
    * The forward motion ends when the y-coordinates match, or 
    * when an obstacle is encountered.
    * 
    * @param odo The odometer tracking the robot's movement.
    * @param usp The robot's ultrasonic poller.
    * @param px The x coordinate of the desired destination, in cm.
    * @param py The y coordinate of the desired destination, in cm.
    */
   public static void goTowardsY(Odometer odo, USPoller usp, double px, double py){
      TwoWheeledRobot robot = odo.getTwoWheeledRobot();
      double x, y;
      int distL, distR;
      boolean cinder = false, pathBlocked = false;
      
      // All the same logic as goTowardsX, see above...
      x = odo.getX();
      y = odo.getY();
      
      
      if (py > y) {
         
         while (py - y > 31 && !cinder) {
            
            Navigation.turnRelative(odo, -30);
            distL = usp.getDistHi();
            Navigation.turnRelative(odo, 60);
            distR = usp.getDistHi();
            Navigation.turnRelative(odo, -30);
            
            if(distL > CLEARANCE_FAR && distR > CLEARANCE_FAR)
               go(odo);
            else
               cinder = true;
            
            y = odo.getY();
         }
         
         robot.setSpeeds(FSPEED, 0);
         
         while (py > y && usp.getDistHi() > CLEARANCE_NEAR){
            y = odo.getY();
         }
         
         robot.setSpeeds(0, 0);
         
      } else {
         
         while (y - py > 31 && !cinder) {
            
            Navigation.turnRelative(odo, -30);
            distL = usp.getDistHi();
            Navigation.turnRelative(odo, 60);
            distR = usp.getDistHi();
            Navigation.turnRelative(odo, -30);
            
            if(distL > CLEARANCE_FAR && distR > CLEARANCE_FAR)
               go(odo);
            else
               cinder = true;
            
            y = odo.getY();
         }
         
         robot.setSpeeds(FSPEED, 0);
         
         while (y > py && usp.getDistHi() > CLEARANCE_NEAR){
            y = odo.getY();
         }
         
         robot.setSpeeds(0, 0);
         
      }
      
      x = odo.getX();
      
      if ((x > (px - 10) && x < (px + 10)) && cinder) {
         Navigation.goAroundObstacle(odo, usp);
      }
      
   }
   
   /**
    * If an obstacle is encountered, this method makes the robot go around it.
    * It then continues its path on the other side.
    * 
    * @param odo The odometer tracking the robot's movement.
    */
   public static void goAroundObstacle(Odometer odo, USPoller usp){
      
      int dir = odo.getDirection();
      
      boolean rightBusy = false;
      
      // Turn to the right
      Navigation.turnRelative(odo, 90);
      
      // If right is blocked, turn to the left
      if(usp.getDistHi() < CLEARANCE_NEAR + 5) {
         Navigation.turnRelative(odo, 180);
         rightBusy = true;
      }
      
      // Move forward 40 cm
      Navigation.goForward(odo, 40);
      
      // Face the obstacle again
      Navigation.turnTo(odo, dir * 90);
      
      // If still blocked, turn and keep going
      if(usp.getDistHi() < CLEARANCE_NEAR + 5 && rightBusy) {
         
         Navigation.turnRelative(odo, -90);
         Navigation.goForward(odo, 40);
         
      } else if (usp.getDistHi() < CLEARANCE_NEAR + 5 && !rightBusy) { 
         
         Navigation.turnRelative(odo, 90);
         Navigation.goForward(odo, 40);
         
      }
      
      // Face the obstacle again
      Navigation.turnTo(odo, dir * 90);
      
      // Move forward 40 cm
      Navigation.goForward(odo, 60);
      
      // Face original path
      if(rightBusy)
         Navigation.turnRelative(odo, 90);
      else 
         Navigation.turnRelative(odo, -90);
      
      // If blocked, turn and keep going
      if(usp.getDistHi() < CLEARANCE_NEAR + 5) {
         Navigation.turnTo(odo, dir * 90);
         Navigation.goForward(odo, 40);
         
         // Face original path
         if(rightBusy)
            Navigation.turnRelative(odo, 90);
         else 
            Navigation.turnRelative(odo, -90);
      }
      
      // Rejoin original path
      Navigation.goForward(odo, 40);
      Navigation.turnTo(odo, dir * 90);
      
      
   }
   
   /**
    * Sweeps the field directly in front of the robot for obstacles or pallets.
    * 
    * @param odo The odometer tracking the robot's movement.
    * @param usp The robot's ultrasonic sensor polling system.
    * @param arc The arc that the robot will check ahead for, in degrees.
    * 
    * @return Returns 0 for a clear field, -1 for a cinder block, 1 for a pallet.
    */
   public static int checkAhead(Odometer odo, USPoller usp, int arc) {
      
      TwoWheeledRobot robot = odo.getTwoWheeledRobot();
      
      // Set up sweeping data
      int [] distsHi = {100, 100, 100, 100, 100};
      int [] distsDiff = {0, 0, 0, 0, 0};
      int avgHi = 100, avgDiff = 0, angleI = 0;
      boolean leftWall = false, rightWall = false, clear = true, pallet = false;
      
      // For all nodes on the edge of the course, don't sweep into the walls.
      // Otherwise, regular sweep (half arc on each side).
      if ((odo.getNodeX() == 1 && odo.getDirection() == 0) ||
          (odo.getNodeX() == 11 && odo.getDirection() == 2) ||
          (odo.getNodeY() == 1 && odo.getDirection() == 3) ||
          (odo.getNodeY() == 11 && odo.getDirection() == 1)) {
         
         robot.setSpeeds(0, RSPEED);
         turn(odo, arc/2);
         leftWall = true;
         
      } else if ((odo.getNodeX() == 1 && odo.getDirection() == 2) ||
                 (odo.getNodeX() == 11 && odo.getDirection() == 0) ||
                 (odo.getNodeY() == 1 && odo.getDirection() == 1) ||
                 (odo.getNodeY() == 11 && odo.getDirection() == 3)) {
         
         turnRelative(odo, -arc/2);
         robot.setSpeeds(0, RSPEED);
         turn(odo, arc/2);
         rightWall = true;
         
      } else {
         
         // Turn to -(half arc) and do an (arc) degree sweep.
         turnRelative(odo, -arc/2);
         //angleI = (int)(odo.getTheta() + SWEEP_ARC) % 360;
         robot.setSpeeds(0, RSPEED);
         turn(odo, arc);
      }
      
      // Keep track of the 5 last distances read.
      for(int i = 0; Motor.A.isMoving() || Motor.B.isMoving(); i++) {
         
         distsHi[i] = usp.getDistHi();
         avgHi = (distsHi[0] + distsHi[1] + distsHi[2] + distsHi[3] + distsHi[4]) / 5;
         
         distsDiff[i] = usp.getDistDiff();
         avgDiff = (distsDiff[0] + distsDiff[1] + distsDiff[2] + distsDiff[3] + distsDiff[4]) / 5;
         
         // If their average falls under the clearance, the path is considered blocked.
         if (avgHi < CLEARANCE_FAR)
            clear = false;
         
         // If the average distance difference (between top and bottom) is above the
         // minimum clearance, a pallet has been found.
         if (avgDiff > BLOCK_WALL_DIFF) {
            pallet = true;
            angleP = odo.getTheta();
         }
         
         try { Thread.sleep(50); } catch (Exception e) {}
         
         i %= 4;
      }
      
      try { Thread.sleep(250); } catch (Exception e) {}
      
      if (!rightWall) {
         // Turn back to face ahead.
         Navigation.turnRelative(odo, -arc/2);
      }
      
      
      if (!clear)
         return -1;
      else if (clear && !pallet)
         return 0;
      else
         return 1;
      
   }
   
   /**
    * Turn on the line detectors and advance 30.48 cm, so that 
    * movement is corrected in 12 inch increments.
    * 
    * @param odo The odometer tracking the robot's movement.
    */
   public static void go(Odometer odo) {
      
      LineDetector.left.start();
      LineDetector.right.start();
      
      // advance one grid tile, and correct orientation.
      Navigation.goForward(odo, 30.48);
      Navigation.turnTo(odo, odo.getDirection() * 90);

      
      LineDetector.left.stop();
      LineDetector.right.stop();
   }
   
   /**
    * Basically a combination of checkAhead() and go() with some added logic
    * based on their results. This method will check ahead of the robot, determine
    * what is in front of it, and act accordingly. If the path is clear, the robot
    * will advance. If the path is blocked, the robot will change its path or go
    * around it. If a pallet is ahead, it will get picked up.
    * 
    * @param odo The odometer tracking the robot's movement.
    * @param usp The robot's ultrasonic sensor polling system.
    * @param bf The robot's block finding system.
    * 
    * @return 0 if path was clear and the robot advanced,
    * -1 if the path was blocked and the robot avoided the obstacle,
    * 1 if the robot found a pallet and picked it up,
    * 2 if the robot now has a pallet and should drop it off next.
    */
   /*public static int checkAndGo(Odometer odo, USPoller usp, BlockFinder bf) {
      
   }
   */
}