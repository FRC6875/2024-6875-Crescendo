// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkRelativeEncoder.Type;


import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Servo;
import com.kauailabs.navx.frc.AHRS;
import java.lang.Math;



/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */

public class Robot extends TimedRobot {
  // declare wheel motors
  CANSparkMax frontLeftDriveMotor = new CANSparkMax(1, MotorType.kBrushless);
  CANSparkMax backLeftDriveMotor = new CANSparkMax(3, MotorType.kBrushless);
  CANSparkMax frontRightDriveMotor = new CANSparkMax(2, MotorType.kBrushless);
  CANSparkMax backRightDriveMotor = new CANSparkMax(4, MotorType.kBrushless);
   
  // declare shooter motors
  CANSparkMax leftShoot = new CANSparkMax(5, MotorType.kBrushless);
  CANSparkMax rightShoot = new CANSparkMax(6, MotorType.kBrushless);

  CANSparkMax leftIntake = new CANSparkMax(7, MotorType.kBrushless);//Shoud be brushed
  CANSparkMax rightIntake = new CANSparkMax(8, MotorType.kBrushless);//Shoud be brushed

  //declare actuators
  Servo actuator1 = new Servo(0);
  Servo actuator2 = new Servo(1);

  //declare input sensor
  DigitalInput shootSensor = new DigitalInput(0);

  // declare DifferentialDrive object
  DifferentialDrive frontRobotDrive;
  DifferentialDrive backRobotDrive;
  DifferentialDrive shootDrive;
  DifferentialDrive intakeDrive;

  // delcare gyro
  AHRS gyro = new AHRS(SPI.Port.kMXP);



  // declare XboxControllers
  XboxController Controller1 = new XboxController(0); //drive controller
  XboxController Controller2 = new XboxController(1); //shoot/intake/actuator controller


  //declare Encorders
  RelativeEncoder frontLeftEncoder;
  RelativeEncoder frontRightEncoder;
  RelativeEncoder backLeftEncoder;
  RelativeEncoder backRightEncoder;
  RelativeEncoder leftShootEncoder;
  RelativeEncoder rightShootEncoder;


  //declare autonomous modes
  private static final String kShootAndDrive = "Shoot And Drive";
  private static final String kLeave = "Leave";
  private static final String kShootLeavePickup = "Shoot and drive pick up and stay";
  private static final String kShootLeaveTurnRed= "Shoot Leave Red Alliance Closest to the amp";
  private static final String kSitAndDoNothing = "Do nothing";
  private String m_autoSelected; // selection options
  private final SendableChooser<String> m_chooser = new SendableChooser<>();


  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {

    //initialize actuators
    actuator1.setBoundsMicroseconds(2000, 0, 1500, 0, 0);
    actuator2.setBoundsMicroseconds(2000, 0, 1500, 0, 0);


    // initialize motors
    frontLeftDriveMotor.restoreFactoryDefaults();
    frontRightDriveMotor.restoreFactoryDefaults();
    backRightDriveMotor.restoreFactoryDefaults();
    backLeftDriveMotor.restoreFactoryDefaults();
    leftShoot.restoreFactoryDefaults();
    rightShoot.restoreFactoryDefaults();

    // initialize encoders
    backLeftEncoder = backLeftDriveMotor.getEncoder(Type.kHallSensor, 42);
    backRightEncoder = backRightDriveMotor.getEncoder(Type.kHallSensor, 42);
    frontLeftEncoder = frontLeftDriveMotor.getEncoder(Type.kHallSensor, 42);
    frontRightEncoder = frontRightDriveMotor.getEncoder(Type.kHallSensor, 42);
    rightShootEncoder = rightShoot.getEncoder(Type.kHallSensor, 42);
    leftShootEncoder = leftShoot.getEncoder(Type.kHallSensor, 42);

    // set for the wheel motors as we want to know the positions
    //  PI * WheelDiameter / GearRatio --WheelDiameter is in inches
    frontLeftEncoder.setPositionConversionFactor(Math.PI*6/8.45);
    frontRightEncoder.setPositionConversionFactor(Math.PI*6/8.45);
    backLeftEncoder.setPositionConversionFactor(Math.PI*6/8.45);
    backRightEncoder.setPositionConversionFactor(Math.PI*6/8.45);

    // set autonomous mode names
    m_chooser.setDefaultOption("Leave Auto", kLeave);
    m_chooser.addOption("Shoot and Leave", kShootAndDrive);
    m_chooser.addOption("Shoot and drive pick up and stay", kShootLeavePickup);
    m_chooser.addOption("Do nothing", kSitAndDoNothing);
    m_chooser.addOption("Shoot Leave Red Alliance closest to the amp",kShootLeaveTurnRed);
    SmartDashboard.putData("Auto choices", m_chooser);

    // set leader/followers - this connects the front and back motors to drive together
    backLeftDriveMotor.follow(frontLeftDriveMotor);
    backRightDriveMotor.follow(frontRightDriveMotor);


  // not doinmg cameras
    // CameraServer.startAutomaticCapture();
    // CameraServer.startAutomaticCapture();

    // set motor inversion (may not have to do this - test without it later)
    // frontLeftDriveMotor.setInverted(false);
    backLeftDriveMotor.setInverted(false);
    // frontRightDriveMotor.setInverted(true);
    // backRightDriveMotor.setInverted(true);
    backRightDriveMotor.setInverted(true); // have to do seperate for each motor
    leftShoot.setInverted(false);
    rightShoot.setInverted(true);
    leftIntake.setInverted(false);
    rightIntake.setInverted(true);


    // create differential drive objects 
    // we removed '::set' from parameters

    //  frontRobotDrive = new DifferentialDrive(frontLeftDriveMotor::set,frontRightDriveMotor::set);
    //  backRobotDrive = new DifferentialDrive(backLeftDriveMotor::set,backRightDriveMotor::set);
    robotDrive = new DifferentialDrive(backLeftDriveMotor,backRightDriveMotor); //all motors connected

    shootDrive = new DifferentialDrive(leftShoot,rightShoot);
    intakeDrive = new DifferentialDrive(leftIntake,rightIntake);
    // instead just make a method to just set the speed
    

  }
  // get speed for drive motors -- not necessary, dependso m how you're moving your joystick
  private double getSpeed() {
    if (Controller1.getLeftY()<0){
      return  -Controller1.getLeftY()*Controller1.getLeftY();
      
    }
   else {
    return   Controller1.getLeftY()*Controller1.getLeftY();
   }

  //   if (Controller1.getLeftY()<=-0.8){
  //     return  -1;
      
  //   }
  //   else if (Controller1.getLeftY()>=0.8) {
  //     return 1;
  //   }
  //  else {
  //   return Controller1.getLeftY();
  //  }
    
  }

  private void turnInPlace(double targetAngle, double rotation) {
    double direction; // in NavX, clockwise is positive??
    // dont mount NavX backwards!!!
    if ( (gyro.getAngle()) < targetAngle ) {
      direction = 1;
    }
    else if ( (gyro.getAngle()) > targetAngle ) {
      direction = -1;
    }
    // gives room for error, tolerance range
    while (Math.abs((gyro.getAngle() - targetAngle )) >= 5) { // as your angles get closer, the difference gets smaller. 5 is a tolerance, 5 degrees of error
      robotDrive.arcadeDrive(0,rotation*direction);
    }
    robotDrive.arcadeDrive(0,0); // if you don't change the value, it will keep running on the last value given -- test to see if this is true though
  }

  private void driveDistance(double speed, double targetDistance){
    frontRightEncoder.setPosition(0); // always initialize encoder position to 0
    while (Math.abs(frontRightEncoder.getPosition()) < Math.abs(targetDistance) ) { //while the encoder (starting at 0 distance) is less than the target distance
      robotDrive.arcadeDrive(speed,0); // drive forward at given speed
    }
    robotDrive.arcadeDrive(0,0); // when the encoder distance is the same or greater than the target distance, stop
  } // may need to add room for error as in turnInPlace



  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // display positions on SmartDashboard
    SmartDashboard.putNumber("Front Left Distance", frontLeftEncoder.getPosition());
    SmartDashboard.putNumber("Front Right Distance", frontRightEncoder.getPosition());
    SmartDashboard.putNumber("Back Left Distance", backLeftEncoder.getPosition());
    SmartDashboard.putNumber("Back Right Distance", backRightEncoder.getPosition());
    SmartDashboard.putBoolean("Shoot Sensor",shootSensor.get());
 
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
       
    backLeftEncoder.setPosition(0);
    backRightEncoder.setPosition(0);
    frontLeftEncoder.setPosition(0);
    frontRightEncoder.setPosition(0);

   
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

    switch (m_autoSelected) {

      case kShootAndDrive: 
      // may need to modify depending where note is loaded.
      //if loaded in intake - will need to push note up using actuators, then shoot, then drive.
      if (shootSensor.get()){ //note is in shooter
        shootDrive.arcadeDrive(0.9, 0); //shoot at 0.9 speed (change speed accordingly)
      }
      else { // note has been shot (sensor not sensing note anymore)
        driveDistance(0.5,24); // input is speed, target distance (in)
      }
      break; // end kShootAndDrive


      case kLeave: // robot only drives forward

      //   if ((frontRightEncoder.getPosition()<80)&&(frontLeftEncoder.getPosition()<80)&&(backRightEncoder.getPosition()<80)&&(backLeftEncoder.getPosition()<80)) {
      // //  frontRobotDrive.arcadeDrive (0.5,0);
      // //  backRobotDrive.arcadeDrive (0.5,0);
      // the above 'if' only really applies to auto periodic

       driveDistance(0.5,12); // input is speed, target distance (in)
        
      break; // end kLeave

      case kShootLeavePickup:
        //shoot
        //drivedistance
        //turnInPlace if needed
        //pickup
        
      break;

      case kSitAndDoNothing:
        //done!
      break;

      case kShootLeaveTurnRed:
        //turn red?
      break;

      default:
        break;
    }
  }
//d
  /** This function is called periodically during autonomous. */
  // @Override
  // public void autonomousPeriodic() {
    
  // } // we are no longer using this, and instead putting our cases in Auto Init above

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
  
  // frontRobotDrive.arcadeDrive(getSpeed(),Controller1.getLeftX());
  // backRobotDrive.arcadeDrive(getSpeed(),Controller1.getLeftX());
  robotDrive.arcadeDrive(getSpeed(),Controller1.getLeftX()); // getSpeed()-getleftY instead of
  // multiple either one by a decimal to slow down
  
  // Shoot - A button
  if (Controller2.getAButton()) {
    shootDrive.arcadeDrive(0.9, 0); //shoot at 0.9 speed (change speed accoridngly)
  }
  // Intake - B button
  if (Controller2.getBButton()) {
    intakeDrive.arcadeDrive(0.1, 0); //intake at 0.1 speed (change speed accoridngly)
  }
  // actuators - X button
  if (Controller2.getXButton()) {
    actuator1.setSpeed(0.95);
    actuator2.setSpeed(0.95);
  }
  // actuators - Y button
  if (Controller2.getYButton()) {
    actuator1.setSpeed(0.05);
    actuator2.setSpeed(0.05);

  }
  }
  


  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
