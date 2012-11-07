
import lejos.nxt.Button;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
//import lejos.nxt.comm.RConsole;
import lejos.util.Timer;

public class MainManager {

	public static void main(String[] args) {
		
		// object declaration
	
		Odometer odometer;
		UltrasonicDataCollector ultrasonicDataCollector;
		UltrasonicLocalizer ultrasonicLocalizer;
		MotorController motorController;
		Timer odometerTimer, controllerTimer;
		LightSensor lightSensor;
		Controller controller;
	
		//instantiate odometer and its timer
		odometer = new Odometer();	
		odometerTimer = new Timer(50, odometer);
		
		
		// instantiate ultrasonicDataCollecter object
		ultrasonicDataCollector = new UltrasonicDataCollector(new UltrasonicSensor(SensorPort.S1));
		
		// instantiate lightDataCollector object
		
		lightSensor = new LightSensor(SensorPort.S2);
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
