package master;


import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;

/**
 * Line detector, which constantly polls the light sensors to detect line crosses.
 * When a line has been crossed, the grid snapper is notified and behaves accordingly.
 * 
 * @author Severin "Sparky" Smith
 * @author Stephane Beniak - 260324952
 * 
 * @see GridSnapper
 * @see LCDInfo
 */
public class LineDetector implements Runnable {
   
   /** The left line detector, running on the left light sensor. */
   public static final LineDetector left = new LineDetector(new LightSensor(SensorPort.S3, true), true);
   /** The right line detector, running on the right light sensor. */
   public static final LineDetector right = new LineDetector(new LightSensor(SensorPort.S4, true), false);
   
   /** Defines the maximum light value threshold for a black line. */
   public static final int THRESHOLD = 485;
   
   /** Defines the status of this line detector. True if it's running. */
   public boolean running;
   /** This line detector's running thread. */
   Thread line_detector_thread;
   
   private LightSensor ls;
   private boolean leftSide;
   private GridSnapper gs;
   
   private int newValue;
   private int lastValue;
   
   /**
    * Creates a new line detector. It is private because no more than the above
    * two (left and right) should exist.
    * 
    * @param ls_ The light sensor to be polled.
    * @param leftSide_ If true, it's the left light sensor.
    */
   private LineDetector (LightSensor ls_, boolean leftSide_) {
      ls = ls_;
      leftSide = leftSide_;
      lastValue = 0;
      
      this.start();
   }
   
   /**
    * Starts the line detector thread. In case it was already running, it will be
    * stopped and restarted.
    */
   public void start() {
      stop();
      running = true;
      this.ls.setFloodlight(true);
      line_detector_thread = new Thread(this);
      line_detector_thread.setDaemon(true);
      line_detector_thread.start();
   }
   
   /**
    * Stop the current line detector, and let its thread yield to all others.
    */
   public void stop() {
      
      if(running) {
         running = false;
         this.ls.setFloodlight(false);
         while(line_detector_thread != null && line_detector_thread.isAlive()) Thread.yield();
      }
   }
   
   /**
    * Continuously checks the light sensor for a line, by comparing the light values
    * to a set threshold. In order for the listener to be notified only once per line,
    * the line is only latched for a falling edge, i.e. when the light value drops
    * from above the threshold to under it.
    */
   public void run() {
      
      while(running) {
         newValue = ls.getNormalizedLightValue();
         if(newValue < THRESHOLD && lastValue > THRESHOLD) notifyGridSnapper();
         lastValue = newValue;
      }
   }
   
   /**
    * Obtain the latest light sensor reading.
    * 
    * @return The last value read by the light sensor, from 0-1023.
    */
   public int getValue() {
      return lastValue;
   }
   
   /**
    * Set the grid snapper that will be listening to this line detector.
    * 
    * @param gs_ The grid snapper that will be notified of line crosses.
    */
   public void setGridSnapper(GridSnapper gs_){
      gs = gs_;
   }
   
   /**
    * Notify the grid snapper that a line has been crossed.
    */
   public void notifyGridSnapper() {
      if(gs != null) gs.lineDetected(this);
   }
   
   /**
    * Check if the line was detected by the left or right light sensor.
    * 
    * @return True for the left one, false for the right.
    */
   public boolean isLeft() {
      return leftSide;
   }
   
}