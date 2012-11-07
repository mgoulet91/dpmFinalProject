package master;

import lejos.nxt.UltrasonicSensor;

/**
 * Wrapper class for the ultrasonic sensors, which adds a distance
 * filter, and improves the ping/wait routine.
 * 
 * 
 * @see BlockFinder
 * @see LCDInfo
 */
public class USPoller extends Thread {
   
   /** Defines the default maximum value for filtering */
   public static final int DEFAULT_FILTER = 120;
   
   /** The robot's high mounted ultrasonic sensor */
   public UltrasonicSensor usHi;
   /** The robot's low mounted ultrasonic sensor */
   public UltrasonicSensor usLo;
   
   /** The distance stored for the high US sensor */
   public int distHi;
   /** The distance stored for the low US sensor */
   public int distLo;
   /** The maximum distance considered before filtering */
   public int filter;
   
   /**
    * Constructor which defines the sensors to be used, and the distance
    * filter to be applied.
    * 
    * @param usLo_ The low US sensor to be used.
    * @param usHi_ The high US sensor to be used.
    * @param maxFilter The maximum distance that the sensor will consider, in cm.
    */
   public USPoller (UltrasonicSensor usLo_, UltrasonicSensor usHi_, int maxFilter){
      usLo = usLo_;
      usHi = usHi_;
      usLo.ping();
      usHi.ping();
      filter = maxFilter;
      
      // Start this US polling Thread.
      this.start();
   }
   
   /**
    * Constructor which defines the sensors to be used, and applies the
    * default filter distance.
    * 
    * @param usL The low US sensor to be used.
    * @param usH The high US sensor to be used.
    */
   public USPoller (UltrasonicSensor usL, UltrasonicSensor usH){
      this(usL, usH, DEFAULT_FILTER);
   }
   
    /**
    * Continuously polls both ultrasonic sensors, and stores the distance
    * they measure. First, each sensor pings, then waits for a reasonable 
    * period for the ping to return. It the reads the distance obtained, 
    * and filters out any spurious values above the filter by clamping 
    * all large values down to the filter value.
    */
   public void run(){
      
      while(true){
         // Ping each sensor
         usLo.ping();
         usHi.ping();
         
         // Sleep a moment while the pings completes.
         try { Thread.sleep(50); } catch (Exception e) {}
         distLo = usLo.getDistance();
         distHi = usHi.getDistance();
         
         // Clamp all data above the filter, down to the filter
         distLo = (distLo > filter ? filter : distLo);
         distHi = (distHi > filter ? filter : distHi);
      }
      
   }
   
   /**
    * Obtains the distance measured by the low ultrasonic sensor.
    * 
    * @return The filtered distance measured by the low sensor, in cm.
    */
   public int getDistLo(){
      return distLo;
   }
   
   /**
    * Obtains the distance measured by the high ultrasonic sensor.
    * 
    * @return The filtered distance measured by the high sensor, in cm.
    */
   public int getDistHi(){
      return distHi;
   }
   
   /**
    * Obtains the difference in distances measured by the high and low sensors.
    * 
    * @return The high sensor's distance minus the low one's, in cm.
    */
   public int getDistDiff(){
      return distHi - distLo;
   }
   
}