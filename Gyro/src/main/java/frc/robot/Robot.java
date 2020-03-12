/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

/**
 * This is a sample program to demonstrate how to use a gyro sensor to make a
 * robot drive straight. This program uses a joystick to drive forwards and
 * backwards while the gyro is used for direction keeping.
 */
public class Robot extends TimedRobot {
  private static final double kAngleSetpoint = 0.0;
  private static final double kP = 0.1; // propotional turning constant

  // gyro calibration constant, may need to be adjusted;
  // gyro value of 360 is set to correspond to one full revolution
  private static final double kVoltsPerDegreePerSecond = 0.0128;

  private static final int kLeftMotorPort = 0;
  private static final int kRightMotorPort = 1;
  private static final int kGyroPort = 0;
  private static final int kJoystickPort = 0;

//  private final DifferentialDrive m_myRobot = new DifferentialDrive(new PWMVictorSPX(kLeftMotorPort),
//      new PWMVictorSPX(kRightMotorPort));
  private final WPI_VictorSPX _leftMotor = new WPI_VictorSPX(0);
  private final WPI_VictorSPX _rightMotor = new WPI_VictorSPX(1);
  private final DifferentialDrive m_myRobot = new DifferentialDrive(_leftMotor, _rightMotor);
  // private final AnalogGyro m_gyro = new AnalogGyro(kGyroPort);
  private final ADXRS450_Gyro m_gyro = new ADXRS450_Gyro(Port.kOnboardCS0);
  private final Joystick m_joystick = new Joystick(kJoystickPort);
  private Double m_maxPower;

  @Override
  public void robotInit() {
    m_gyro.calibrate();
    _leftMotor.setInverted(true);
    _rightMotor.setInverted(true);
    //m_gyro.
    //m_gyro.calibrate();
    //m_gyro.setSensitivity(kVoltsPerDegreePerSecond);
    
  }

  @Override
  public void teleopInit() {
    m_gyro.reset();
    SmartDashboard.putNumber("Max Turn Power", .5);
    m_maxPower = SmartDashboard.getNumber("Max Turn Power", 0 );
  }

  /**
   * The motor speed is set from the joystick while the RobotDrive turning
   * value is assigned from the error between the setpoint and the gyro angle.
   */
  @Override
  public void teleopPeriodic() {
    /*double angle = Math.floor(m_gyro.getAngle())%354;
    double turningValue = (kAngleSetpoint - angle) * kP;
    double joystickY = m_joystick.getY();
    m_maxPower = SmartDashboard.getNumber("Max Turn Power", 0 );

    

    if(turningValue>m_maxPower)
      turningValue=m_maxPower;
    if(turningValue<-1*m_maxPower)
      turningValue=-1*m_maxPower;
    //if(Math.floor(m_joystick.getY())==0.0)
    //  turningValue=0.0;
    // Invert the direction of the turn if we are going backwards
    turningValue = -1 * Math.copySign(turningValue, joystickY );
    m_myRobot.arcadeDrive(joystickY, turningValue);*/

    double angle = m_gyro.getAngle();
    double turningValue = 0.0;
    double joystickY = m_joystick.getY();
    m_maxPower = SmartDashboard.getNumber("Max Turn Power", 0 );

    if(angle<0.0)
    {
      angle = Math.ceil(angle)%354;
    }
    else
    {
      angle = Math.floor(angle)%354;
    }

    //turned left turning right
    if(angle < -1.0)
      turningValue = m_maxPower;

    //turned right turning left
    if(angle > 1.0)
      turningValue = -1 * m_maxPower;

    m_myRobot.arcadeDrive(joystickY, turningValue);



    SmartDashboard.putNumber("Angle", angle);
    SmartDashboard.putNumber("turningValue", turningValue);
    SmartDashboard.putNumber("Joy", joystickY);
    SmartDashboard.putNumber("m_maxPower", m_maxPower);
  }
}
