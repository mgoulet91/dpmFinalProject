package master;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

import lejos.util.Timer;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.nxt.*;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RS485;


public class MainManager {
	
	 // ------ Inter-Brick Communication --------\\
	 public static DataInputStream input;
	 public static DataOutputStream output;
	 static NXTCommConnector connector;
	 public static NXTConnection connection;
	 static int [] modes = {NXTConnection.PACKET, NXTConnection.RAW}; 
	 
	 public static double topUSDist = 13; 
	 public static boolean pallet = false;
	 public static final double FOOT = 30.48;

	public static void main(String[] args) {
		

		 connector = Bluetooth.getConnector();
		 connection = connector.connect("Storm", modes[0]);
		  
		  try {
		   input = connection.openDataInputStream();
		   output = connection.openDataOutputStream();
		  }catch (Exception e){}

		
		
		
		// object declaration
	
			
		UltrasonicDataCollector ultrasonicDataCollector;
		UltrasonicLocalizer ultrasonicLocalizer;
		MotorController motorController;
		Timer odometerTimer, controllerTimer;
		LightSensor lightSensor;
		Controller controller;
	
		//instantiate odometer and its timer
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odometer = new Odometer(patBot, true);	
		odometerTimer = new Timer(50, odometer);
		GridSnapper gs = new GridSnapper(odometer, true);
		LightSensor lsL = new LightSensor(SensorPort.S3);
	    LightSensor lsR = new LightSensor(SensorPort.S4);
		
		
		// instantiate ultrasonicDataCollecter object
		ultrasonicDataCollector = new UltrasonicDataCollector(new UltrasonicSensor(SensorPort.S4));
		
		// instantiate lightDataCollector object
		
		lightSensor = new LightSensor(SensorPort.S3);
		// instantiate MotorController object
		motorController = new MotorController(Motor.A, Motor.B, lightSensor, odometer);
		
		// instantiate UltrasonicLocalizer object
		ultrasonicLocalizer = new UltrasonicLocalizer(motorController, odometer, ultrasonicDataCollector);
		
		//instantiate Controller object
		controller = new Controller(ultrasonicLocalizer, odometer, ultrasonicDataCollector, motorController, lightSensor);
		
		//instantiate controller timer
		controllerTimer = new Timer(50, controller);
		
		//start timers
		controllerTimer.start();
		odometerTimer.start();
		
		// exit if escape is pressed
		while(Button.readButtons() != Button.ID_ESCAPE);
		System.exit(0);
		
	}
	
}
