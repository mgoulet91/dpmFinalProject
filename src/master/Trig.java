package master;

/**
 * Static class that contains all trig operations in DEGREES,
 * rather than the usual radians.
 * 
 */
public class Trig{
   
   /**
    * Static method that computes the sine of a given angle.
    * 
    * @param angle The desired angle in DEGREES.
    * 
    * @return The sine of the given angle.
    */
   public static double sin(double angle){
      return Math.sin((Math.PI/180)*angle);
   }
   
   /**
    * Static method that computes the cosine of a given angle.
    * 
    * @param angle The desired angle in DEGREES.
    * 
    * @return The cosine of the given angle.
    */
   public static double cos(double angle){
      return Math.cos((Math.PI/180)*angle);
   }
   
   /**
    * Static method that computes the arctangent of a given ratio
    * 
    * @param ratio The ratio of edges.
    * 
    * @return The angle in DEGREES of the given ratio.
    */
   public static double atan(double ratio){
      return (180/Math.PI)*Math.atan(ratio);
   }
   
}