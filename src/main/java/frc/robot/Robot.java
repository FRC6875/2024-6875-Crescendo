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


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */

public class Robot extends TimedRobot {
  CANSparkMax frontLeftDriveMotor = new CANSparkMax(1, MotorType.kBrushless);
  CANSparkMax backLeftDriveMotor = new CANSparkMax(3, MotorType.kBrushless);
  CANSparkMax frontRightDriveMotor = new CANSparkMax(2, MotorType.kBrushless);
  CANSparkMax backRightDriveMotor = new CANSparkMax(4, MotorType.kBrushless);
   
  CANSparkMax leftShoot = new CANSparkMax(5, MotorType.kBrushless);
  CANSparkMax rightShoot = new CANSparkMax(6, MotorType.kBrushless);

  CANSparkMax leftIntakeMotor1 = new CANSparkMax(7, MotorType.kBrushed);
  CANSparkMax rightIntakeMotor2 = new CANSparkMax(8, MotorType.kBrushed);



  DifferentialDrive frontRobotDrive;
  DifferentialDrive backRobotDrive;

  DifferentialDrive shootDrive;
    DifferentialDrive intakeDrive;



  XboxController Controller1 = new XboxController(0);
    XboxController Controller2 = new XboxController(1);

  
RelativeEncoder frontLeftEncoder;
RelativeEncoder frontRightEncoder;
RelativeEncoder backLeftEncoder;
RelativeEncoder backRightEncoder;

RelativeEncoder leftShootEncoder;
RelativeEncoder rightShootEncoder;



  private static final String kShootAndDrive = "Shoot And Drive";
  private static final String kLeave = "Leave";
  private static final String kShootLeavePickup = "Shoot and drive pick up and stay";
  private static final String kSitAndDoNothing = "Do nothing";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    
    frontLeftDriveMotor.restoreFactoryDefaults();
    frontRightDriveMotor.restoreFactoryDefaults();
    backRightDriveMotor.restoreFactoryDefaults();
    backLeftDriveMotor.restoreFactoryDefaults();
    
    leftShoot.restoreFactoryDefaults();
    rightShoot.restoreFactoryDefaults();


    backLeftEncoder = backLeftDriveMotor.getEncoder(Type.kHallSensor, 42);
    backRightEncoder = backRightDriveMotor.getEncoder(Type.kHallSensor, 42);
    frontLeftEncoder = frontLeftDriveMotor.getEncoder(Type.kHallSensor, 42);
    frontRightEncoder = frontRightDriveMotor.getEncoder(Type.kHallSensor, 42);

   rightShootEncoder = rightShoot.getEncoder(Type.kHallSensor, 42);
   leftShootEncoder = leftShoot.getEncoder(Type.kHallSensor, 42);


    frontLeftEncoder.setPositionConversionFactor(Math.PI*6/8.45);
    frontRightEncoder.setPositionConversionFactor(Math.PI*6/8.45);
    backLeftEncoder.setPositionConversionFactor(Math.PI*6/8.45);
    backRightEncoder.setPositionConversionFactor(Math.PI*6/8.45);

    m_chooser.setDefaultOption("Leave Auto", kLeave);
    m_chooser.addOption("Shoot and Leave", kShootAndDrive);
    m_chooser.addOption("Shoot and drive pick up and stay", kShootLeavePickup);
    m_chooser.addOption("Do nothing", kSitAndDoNothing);
    SmartDashboard.putData("Auto choices", m_chooser);
   
    CameraServer.startAutomaticCapture();
    CameraServer.startAutomaticCapture();

    frontLeftDriveMotor.setInverted(false);
    backLeftDriveMotor.setInverted(false);
    frontRightDriveMotor.setInverted(true);
    backRightDriveMotor.setInverted(true);

    leftShoot.setInverted(false);
    rightShoot.setInverted(true);

    leftIntakeMotor1.setInverted(false);
    rightIntakeMotor2.setInverted(true);


     frontRobotDrive = new DifferentialDrive(frontLeftDriveMotor::set,frontRightDriveMotor::set);
     backRobotDrive = new DifferentialDrive(backLeftDriveMotor::set,backRightDriveMotor::set);
     shootDrive = new DifferentialDrive(leftShoot::set,rightShoot::set);
     intakeDrive = new DifferentialDrive(leftIntakeMotor1::set,rightIntakeMotor2::set);





  }

  private double getSpeed() {
    if (Controller1.getLeftY()<0){
      return  -Controller1.getLeftY()*Controller1.getLeftY();
      
    }
   else {
    return   Controller1.getLeftY()*Controller1.getLeftY();
   }
    
  }



  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    
    SmartDashboard.putNumber("Front Left Distance", frontLeftEncoder.getPosition());
    SmartDashboard.putNumber("Front Right Distance", frontRightEncoder.getPosition());
    SmartDashboard.putNumber("Back Left Distance", backLeftEncoder.getPosition());
    SmartDashboard.putNumber("Back Right Distance", backRightEncoder.getPosition());
    

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
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kShootAndDrive:
        // Put custom auto code here
        break;
      case kLeave:
        if ((frontRightEncoder.getPosition()<80)&&(frontLeftEncoder.getPosition()<80)&&(backRightEncoder.getPosition()<80)&&(backLeftEncoder.getPosition()<80)) {
       frontRobotDrive.arcadeDrive (0.5,0);
       backRobotDrive.arcadeDrive (0.5,0);
        
       }
       else {
         
       frontRobotDrive.arcadeDrive (0,0);
       backRobotDrive.arcadeDrive (0,0);
       }
      break;

      case kShootLeavePickup:
      break;

      case kSitAndDoNothing:
      break;

      default:
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
  
  frontRobotDrive.arcadeDrive(getSpeed(),Controller1.getLeftX());
  backRobotDrive.arcadeDrive(getSpeed(),Controller1.getLeftX());

  if (Controller2.getAButton()) {
    shootDrive.tankDrive(0.1, 0.1,false);
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
