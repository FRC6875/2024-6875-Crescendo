// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkRelativeEncoder.Type;
import com.revrobotics.REVLibError;


import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import java.lang.Math;
import edu.wpi.first.wpilibj.Timer;


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
  CANSparkMax leftShoot = new CANSparkMax(6, MotorType.kBrushless);
  CANSparkMax rightShoot = new CANSparkMax(10, MotorType.kBrushless);
  CANSparkMax rightShoot2 = new CANSparkMax(9, MotorType.kBrushed);
  CANSparkMax leftShoot2 = new CANSparkMax(5, MotorType.kBrushed);


  CANSparkMax leftIntake = new CANSparkMax(7, MotorType.kBrushed);//Shoud be brushed
  CANSparkMax rightIntake = new CANSparkMax(8, MotorType.kBrushed);//Shoud be brushed

  //declare lift motors
  //  CANSparkMax rightLift = new CANSparkMax(12, MotorType.kBrushed);//Shoud be brushed
  //  CANSparkMax leftLift = new CANSparkMax(11, MotorType.kBrushed);//Shoud be brushed

  //declare input sensor
  DigitalInput intakeSensor = new DigitalInput(0);

  // declare DifferentialDrive object
  DifferentialDrive frontRobotDrive;
  DifferentialDrive backRobotDrive;
  // DifferentialDrive shootDrive;
  // DifferentialDrive intakeDrive;
  DifferentialDrive robotDrive;

  // delcare gyro
  AHRS gyro = new AHRS(SPI.Port.kMXP);

  Timer waitTimer = new Timer();
  Timer autoTimer = new Timer();


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
  private static final String kShoot = "Shoot -- BLUE and RED";
  private static final String kLeave = "Drive forward -- BLUE and RED";
  private static final String kShootLeavePickup = "Shoot, drive forward from speaker center, intake -- BLUE and RED";
  private static final String kShootLeaveTurnRedAmp= "Shoot, drive forward, turn -- RED closest to amp";
  private static final String kShootLeaveTurnBlueAmp = "Shoot, drive forward, turn -- BLUE closest to amp";
  private static final String kShootLeaveTurnBlueFartherAmp = "Shoot, drive forward, turn -- BLUE farther from amp";
  private static final String kShootLeaveTurnRedFartherAmp = "Shoot, drive forward, turn -- RED Farther from amp";
  private static final String kShootLeavePickupDriveShoot = "Shoot, drive forward, intake, drive backwards, shoot -- (Blue and Red)";
  private static final String kSitAndDoNothing = "Do nothing -- BLUE and RED";



  
  private String m_autoSelected; // selection options
  private final SendableChooser<String> m_chooser = new SendableChooser<>();


  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {

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
    m_chooser.setDefaultOption("Drive forward -- BLUE and RED", kLeave);
    m_chooser.addOption("Shoot, -- BLUE and RED", kShoot);
    m_chooser.addOption("Shoot, drive forward from speaker center, intake -- BLUE and RED", kShootLeavePickup);
    m_chooser.addOption("Do nothing -- BLUE and RED", kSitAndDoNothing);
    m_chooser.addOption("Shoot, drive forward, turn -- RED closest to amp",kShootLeaveTurnRedAmp);
    m_chooser.addOption("Shoot, drive forward, turn -- BLUE closest to amp",kShootLeaveTurnBlueAmp);
    m_chooser.addOption("Shoot, drive forward, turn -- BLUE farther from amp",kShootLeaveTurnBlueFartherAmp);
    m_chooser.addOption("Shoot, drive forward, turn -- RED Farther from amp",kShootLeaveTurnRedFartherAmp);
    m_chooser.addOption("Shoot, drive forward, intake, drive backwards, shoot -- (Blue and Red)",kShootLeavePickupDriveShoot);

    SmartDashboard.putData("Auto choices", m_chooser);
 
    // set leader/followers - this connects the front and back motors to drive together
    backLeftDriveMotor.follow(frontLeftDriveMotor);
    backRightDriveMotor.follow(frontRightDriveMotor);


   
     CameraServer.startAutomaticCapture();
    // CameraServer.startAutomaticCapture();

    // set motor inversion (may not have to do this - test without it later)
    frontLeftDriveMotor.setInverted(true);
    backLeftDriveMotor.setInverted(true);
    frontRightDriveMotor.setInverted(false);
    backRightDriveMotor.setInverted(false); // have to do seperate for each motor
    leftShoot.setInverted(true);
    rightShoot.setInverted(false);
    leftIntake.setInverted(false);
    rightIntake.setInverted(true);   
    leftShoot2.setInverted(true);
    rightShoot2.setInverted(false);
    rightIntake.setInverted(false);
    leftIntake.setInverted(true);
    // rightLift.setInverted(true);
    // leftLift.setInverted(false);

    // burn settings into memory
    frontLeftDriveMotor.burnFlash();
    frontRightDriveMotor.burnFlash();
    backRightDriveMotor.burnFlash();
    backLeftDriveMotor.burnFlash();



    //  rightLift.setInverted(true);
    //  leftLift.setInverted(false);

    gyro.reset();

    // robot differential drive
    robotDrive = new DifferentialDrive(frontLeftDriveMotor,frontRightDriveMotor); //all motors connected
    
  }   //end robotInit



//////////////////// START METHODS ////////////////////////

  private double getSpeedY() {
    if (Controller1.getLeftY()<0){
      return  -Controller1.getLeftY()*Controller1.getLeftY()*Controller1.getLeftY();
      
    }
   else {
    return   Controller1.getLeftY()*Controller1.getLeftY()*Controller1.getLeftY();
   }
    
  }   // end getSpeed

  private double getSpeedX() {
    if (Controller1.getLeftX()<0){
      return  -Controller1.getLeftX()*Controller1.getLeftX()*Controller1.getLeftX();
      
    }
   else {
    return   Controller1.getLeftX()*Controller1.getLeftX()*Controller1.getLeftX();
   }
    
  }  

  private void turnInPlace(double targetAngle, double rotation) {
    double direction = 1; 
    // dont mount NavX backwards!!!
    if ( (gyro.getAngle()) < targetAngle ) {
      direction = 1;
    }
    else if ( (gyro.getAngle()) > targetAngle ) {
      direction = -1;
    }
    // gives room for error, tolerance range
    while (Math.abs((gyro.getAngle() - targetAngle )) >= 5 && autoTimer.get() > 15) { // as your angles get closer, the difference gets smaller. 5 is a tolerance, 5 degrees of error
      robotDrive.arcadeDrive(0,rotation*direction);
      SmartDashboard.putNumber("Turn in Place Angle", gyro.getAngle());
    }

    robotDrive.arcadeDrive(0,0); 
  }   // end turnInPlace


  private void driveDistance(double speed, double targetDistance, double initialPostion){
    targetDistance = targetDistance + initialPostion;
    while (Math.abs(frontRightEncoder.getPosition()) < Math.abs(targetDistance) && autoTimer.get() > 15) { //while the encoder (starting at 0 distance) is less than the target distance
      robotDrive.arcadeDrive(speed*-1,0); // drive forward at given speed
      SmartDashboard.putNumber("Front right Distance", Math.abs(frontRightEncoder.getPosition()));
    }
     
     robotDrive.arcadeDrive(0,0); 
  } // end driveDistance


  private void shoot(double speed){ 

    waitTimer.start();

    if ((waitTimer.get()) < 0.005) {  // ramp up top motors
    leftShoot.set(speed);
    rightShoot.set(speed);
    } 
    else {
    rightShoot2.set(speed);
    leftShoot2.set(speed);
    }
   
    waitTimer.stop();
    // waitTimer.reset();
   
  } // end shoot


  private void shootAuto(double speed){

    waitTimer.start();
    SmartDashboard.putNumber("seconds", waitTimer.get());

    while (waitTimer.get() < 1 && autoTimer.get() > 15) { // ramp up top motors
      leftShoot.set(speed);
      rightShoot.set(speed);
    } 
    while (waitTimer.get() < 1.5 && autoTimer.get() > 15) {
      rightShoot2.set(speed);
      leftShoot2.set(speed);
    }
      leftShoot.set(0);
      rightShoot.set(0);
      rightShoot2.set(0);
      leftShoot2.set(0);
   
    waitTimer.stop();
    // waitTimer.reset();
   
  }   // end shootAuto
  
  private void intake(double speed) {
    rightIntake.set(speed);
    leftIntake.set(speed);
  }   // end intake

  //  private void lift(double speed){
  //   rightLift.set(speed);
  //    leftLift.set(speed);
  //  }   // end lift

 



  //////////////////// END METHODS ////////////////////////

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
    SmartDashboard.putBoolean("Shoot Sensor",intakeSensor.get());
    SmartDashboard.putNumber("gyro", gyro.getAngle());
 
     

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
    gyro.reset(); 
     backLeftEncoder.setPosition(0);
     backRightEncoder.setPosition(0);
     frontLeftEncoder.setPosition(0);
     frontRightEncoder.setPosition(0);
    
     autoTimer.start();

    m_autoSelected = m_chooser.getSelected();
 
    System.out.println("Auto selected: " + m_autoSelected);

    switch (m_autoSelected) {

      case kShoot: // Shoot, drive forward from speaker center (BLUE and RED)

      //  if (waitTimer.get() < 0.1){
        shootAuto(1);
      //  }else {
         //driveDistance(0.7,-36, frontRightEncoder.getPosition());
        //driveDistance(0.3,-50, frontRightEncoder.getPosition());
      //  }
      break; // end kShootAndDrive


      case kLeave: // Drive forward (BLUE and RED)
      
       driveDistance(0.7,-150, frontRightEncoder.getPosition());
       SmartDashboard.putNumber("Front right Distance", frontRightEncoder.getPosition());

      break; // end kLeave
      
     
      case kShootLeavePickup: // Shoot, drive forward from speaker center, intake -- BLUE and RED

        shootAuto(0.5);
        driveDistance(0.5,-20, frontRightEncoder.getPosition());
        turnInPlace(45,0.3);
        driveDistance(0.5,-330, frontRightEncoder.getPosition());

        while (intakeSensor.get()) { // while intake senses reflection (note not in)
        intake(0.5); // intake
        } // will stop when it senses note

        
      break;

      case kShootLeavePickupDriveShoot: // Shoot, drive forward, intake, drive backwards, shoot -- (Blue and Red)

        shootAuto(0.5);
        driveDistance(0.7,-20, frontRightEncoder.getPosition());
        turnInPlace(45,0.5);
        driveDistance(0.7,-200, frontRightEncoder.getPosition());

        while (intakeSensor.get()) { // while intake senses reflection (note not in)
          intake(0.5);  // intake
        } // will stop when it senses note
          
        driveDistance(0.2,20, frontRightEncoder.getPosition());
        shootAuto(0.5);
        
      break;


      case kSitAndDoNothing: // Do nothing -- BLUE and RED
        //done!
      break;

      case kShootLeaveTurnRedAmp:
        //turn red?
        shootAuto(0.9);
        driveDistance(0.5,-20, frontRightEncoder.getPosition());
        turnInPlace(45,0.3);
        driveDistance(0.5, -330, frontRightEncoder.getPosition());
       
      break;

      case kShootLeaveTurnBlueAmp:  // Shoot, drive forward, turn -- BLUE closest to amp

        shootAuto(0.5);
        driveDistance(0.2,-20, frontRightEncoder.getPosition());
        turnInPlace(45,0.3);
        driveDistance(0.2, -336, frontRightEncoder.getPosition());

      break;
      
      case kShootLeaveTurnBlueFartherAmp: // Shoot, drive forward, turn -- BLUE farther from amp
        shootAuto(0.5);
        driveDistance(0.2,-20, frontRightEncoder.getPosition());
        turnInPlace(45,0.3);
        driveDistance(0.2, -336, frontRightEncoder.getPosition());
      break;

      case kShootLeaveTurnRedFartherAmp:  // Shoot, drive forward, turn -- RED Farther from amp
        shootAuto(0.5);
        driveDistance(0.2,-20, frontRightEncoder.getPosition());
        turnInPlace(45,0.3);
        driveDistance(0.2, -336, frontRightEncoder.getPosition());
      break;


      default:
      break;
      } // end cases
  } // end auto init



  /** This function is called periodically during autonomous. */
  // @Override
  // public void autonomousPeriodic() {
  // //   SmartDashboard.putNumber("gyro", gyro.getAngle());
  //  } // we are no longer using this, and instead putting our cases in Auto Init above

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    gyro.reset();
    // m_chooser.close(); // might work
  }


  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
     
    // CameraServer.startAutomaticCapture();
 
  
  // frontRobotDrive.arcadeDrive(getSpeed(),Controller1.getLeftX());
  // backRobotDrive.arcadeDrive(getSpeed(),Controller1.getLeftX());
  robotDrive.arcadeDrive(getSpeedY(),getSpeedX()); //Controller1.getLeftX()*Controller1.getLeftX())); // getSpeed()-getleftY instead of
  // robotDrive.arcadeDrive(Controller1.getLeftY(),Controller1.getLeftX());
  // multiple either one by a decimal to slow down
  
  // ShootSpeaker - X button
  if (Controller2.getXButton()) {
    shoot(0.9); //shoot at 0.9 speed (change speed accoridngly)
  // } else if (Controller2.getYButton()) {
  //   shoot(0.5); //shoot at 0.9 speed (change speed accoridngly)
  }else {
    shoot(0.0);
    waitTimer.stop();
    waitTimer.reset();
  }

  // ShootAmp - Y
//   if (Controller2.getYButton()) {
//     leftShoot.set(0.3);
//     rightShoot.set(0.3);
//     leftShoot2.set(0.3);
//     rightShoot2.set(0.3);//shoot at 0.9 speed (change speed accoridngly)
//    }
//  else {
//    leftShoot.set(0.0);
//     rightShoot.set(0.0);
//     leftShoot2.set(0.0);
//     rightShoot2.set(0.0);
//  }
  // shoot(0.0);
  // waitTimer.stop();
  // waitTimer.reset();
  // }
  
  // Intake - A button
  if (Controller2.getAButton()) {
    intake(0.5); //intake at 0.1 speed (change speed accoringly)
  }else intake(0.0);

  // if(Controller1.getBButton()){
  //   gyro.reset();
  // }
  //Lift - Dpad Up = up
  //     - Dpad down = down
  //  if(Controller2.getBButton()) {//UP
  //   lift(1);

  //   } else if(Controller2.getYButton()){//DOWN
  //     lift(-1);
  //   } else{
  //    lift(0);
  // }

   // lift(Controller2.getLeftY());
    
  // // actuators - X button
  // if (Controller2.getXButton()) {
  //   actuator1.setSpeed(0.95);
  //   actuator2.setSpeed(0.95);
  // }
  // // actuators - Y button
  // if (Controller2.getYButton()) {
  //   actuator1.setSpeed(0.05);
  //   actuator2.setSpeed(0.05);

  // }
  }   // end teleopPeriodic
  


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
