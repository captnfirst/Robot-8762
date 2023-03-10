// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.PWMVictorSPX;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  VictorSP m_leftMotor = new VictorSP(1);
  VictorSP m_rightMotor = new VictorSP(3);
  VictorSP m_kule = new VictorSP(0);

  PWMVictorSPX m_PWMSPX = new PWMVictorSPX(2);

  CANSparkMax m_SPMX_1 = new CANSparkMax(1, MotorType.kBrushless);
  
  //Compressor pcmCompressor = new Compressor(0, PneumaticsModuleType.CTREPCM);
  DoubleSolenoid m_doubleSolenoid = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 3, 2);  

  DifferentialDrive m_robotDrive = new DifferentialDrive(m_leftMotor, m_rightMotor);

  Joystick m_stick = new Joystick(0);

  DigitalInput toplimitSwitch = new DigitalInput(0);
  DigitalInput bottomlimitSwitch = new DigitalInput(1);

  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_rightMotor.setInverted(true);

    //m_SPMX_1.restoreFactoryDefaults();
    //m_SPMX_4.restoreFactoryDefaults(); 

    //pcmCompressor.enableDigital();

    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

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
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    m_robotDrive.arcadeDrive(-0.8*m_stick.getZ(), -0.8*m_stick.getY());
    
    //switch ile ??al????acak motor 
    if (m_stick.getRawButton(2)){
      setMotorSpeed(0.7);
    }
    else if (m_stick.getRawButton(4)){
      setMotorSpeed(-0.7);
    }
    else {
      setMotorSpeed(0);
    }

    //Can Spark Max NEO
    if (m_stick.getRawButton(1)){ 
      m_SPMX_1.set(0.3);  
    }
    else if (m_stick.getRawButton(3)){
      m_SPMX_1.set(-0.3);
    }
    else {
      m_SPMX_1.stopMotor(); 
    } 

    //Double Solenoid
    if (m_stick.getRawButton(5)) {
      m_doubleSolenoid.set(DoubleSolenoid.Value.kForward);
    } else if (m_stick.getRawButton(6)) {
      m_doubleSolenoid.set(DoubleSolenoid.Value.kReverse);
    }else{
      m_doubleSolenoid.set(DoubleSolenoid.Value.kOff);
    }

    /** if(m_stick.getRawButton(2)){
      if(!toplimitSwitch.get()){
        m_kule.set(0.7);
      }else{
        m_kule.set(0);
      }
    }else if(m_stick.getRawButton(4)){
      if(bottomlimitSwitch.get()){
        m_kule.set(-0.7);
      }else{
        m_kule.set(0);
      }
    }else{
      m_kule.set(0);
    } */
  }

  public void setMotorSpeed(double speed) {
    if (speed > 0) {
        if (toplimitSwitch.get()) {
            // We are going up and top limit is tripped so stop
            m_kule.set(0);
        } else {
            // We are going up but top limit is not tripped so go at commanded speed
            m_kule.set(speed);
        }
    } else {
        if (bottomlimitSwitch.get()) {
            // We are going down and bottom limit is tripped so stop
            m_kule.set(0);
        } else {
            // We are going down but bottom limit is not tripped so go at commanded speed
            m_kule.set(speed);
        }
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
