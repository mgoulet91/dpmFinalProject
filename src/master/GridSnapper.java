package master;
/**
 * Implementation of a grid snapping system, which listens to a line
 * detector, and arbitrates coordinate correction of the robot's odometer.
 * 
 * 
 * @see LineDetector
 * @see Odometer
 */
public class GridSnapper {
   
   /** Defines the maximum angle error to be considered by the GridSnapper, in degrees. */
   public static final int MAX_ERROR = 10;
   
   /** Defines the direction of the robot, as estimated by the Odometer. */
   public static int h = 0;
   
   double lightDistance = 20.5; //distance from each light sensor.
   
   private static boolean enabled;
   
   private Odometer odo;
   
   private double leftHit = 0;
   private double rightHit = 0;
   
   /**
    * Creates a new grid snapper, which controls the given robot's
    * coordinate correction through its odometer. Also registers this
    * GridSnapper to listen for line crosses.
    * 
    * @param odo_ The odometer tracking the robot's movement.
    * @param start If true, enable the grid snapper immediately.
    */
   public GridSnapper (Odometer odo_, boolean start) {
      odo = odo_;
      
      LineDetector.left.setGridSnapper(this);
      LineDetector.right.setGridSnapper(this);
      
      enabled = start;
   }
   
   /**
    * Controls what occurs when a line is detected by either line detector.
    * Based on many factors such as the robot's current direction (N, E, S, W),
    * which line detector went off, and the distance between each line cross
    * detection, this method arbitrates the odometer's angle correction.
    * 
    * @param ld The line detector which detected a line cross.
    */
   public synchronized void lineDetected(LineDetector ld) {
      
      if(enabled) {
         
         double angle;
         boolean left = ld.isLeft();
         
         // Get the robot's direction
         h = odo.getDirection();
         
         // Based on this direction, decide which value (x or y) to latch.
         if(h == 0) {
            
            if (left)
               leftHit = odo.getY();
            else
               rightHit = odo.getY();
            
         } else if(h == 1) {
            
            if (left)
               leftHit = odo.getX();
            else
               rightHit = odo.getX();
            
         } else if(h == 2) {
            
            if (left)
               leftHit = odo.getY();
            else
               rightHit = odo.getY();
            
         } else if(h == 3) {
            
            if (left)
               leftHit = odo.getX();
            else
               rightHit = odo.getX();
            
         }
         
         // If both light sensors have detected a line crossing
         if(leftHit != 0 && rightHit != 0) {
            
            // Compute the angle error of the robot.
            angle = Math.atan((rightHit - leftHit)/lightDistance);
            
            // If the error is more than allowed, consider it a bad line detection.
            if (angle < MAX_ERROR && angle > -MAX_ERROR) {
               
               if (h == 0) {
                  correctAngleTo(angle);
               } else if (h == 1) {
                  correctAngleTo(90 + angle);
               } else if (h == 2) {
                  correctAngleTo(180 - angle);
               } else if (h == 3){
                  correctAngleTo(270 - angle);
               }
               
            }
            
         }
         
      }
      
   }
   
   /**
    * Corrects the odometer's orientation, and resets the lineHit variables.
    * 
    * @param angle The new corrected angle, in degrees.
    */
   public void correctAngleTo(double angle) {
      odo.setTheta(angle);
      leftHit = 0;
      rightHit = 0;
   }
   
   /**
    * Enable the GridSnapper
    */
   public void enable() {
      enabled = true;
   }
   
   /**
    * Disable the GridSnapper
    */
   public void disable() {
      enabled = false;
   }
   
}