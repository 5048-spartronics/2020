/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import frc.robot.grip.GripPipeline;

import frc.robot.teamcode.components.*;
import frc.robot.teamcode.mechanisms.DriveController;
import frc.robot.teamcode.mechanisms.HookController;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
  private final Timer m_timer = new Timer();
  private final Double _triggerThreashold = 0.5;
  private XboxController _driverController = new XboxController(0);
  private XboxController _operatorController = new XboxController(1);
  private DriveController _drive = new DriveController(_driverController);
  private HookController _hook = new HookController();
  private GripPipeline _gp = new GripPipeline();
 
  

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
  }

  /**
   * This function is run once each time the robot enters autonomous mode.
   */
  @Override
  public void autonomousInit() {
    m_timer.reset();
    m_timer.start();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    // Drive for 2 seconds
    if (m_timer.get() < 2.0) {
      //m_robotDrive.arcadeDrive(0.5, 0.0); // drive forwards half speed
      
    } else {
      //m_robotDrive.stopMotor(); // stop robot
    }
  }

  /**
   * This function is called once each time the robot enters teleoperated mode.
   */
  @Override
  public void teleopInit() {

    //_gp.process(findContoursOutput)

    DriverFunctions();
    OperatorFunctions();
  }

  private void DriverFunctions()
  {
    _drive.Drive();

    //Driver
    if(_driverController.getStartButtonPressed())
    {
      _hook.DeployHook();

    }
    if(_driverController.getBackButtonPressed())
    {
      //Not Needed This Year
    }
    if(_driverController.getAButtonPressed())
    {
      if(_hook.isDeployed())
      {
        _hook.ClimbDown();
      }
    }
    if(_driverController.getBButtonPressed())
    {
      //Not Needed This Year
    }
    if(_driverController.getXButtonPressed())
    {
      //Not Needed This Year
    }
    if(_driverController.getYButtonPressed())
    {
      if(_hook.isDeployed())
      {
        _hook.ClimbUp();
      }

    }
    if(_driverController.getBumperPressed(Hand.kRight))
    {
      //Not Needed This Year
    }
    if(_driverController.getBumperPressed(Hand.kLeft))
    {
      //Not Needed This Year
    }
    if(_driverController.getTriggerAxis(Hand.kRight)>_triggerThreashold)//If trigger Pulled more than half
    {
      //Not Needed This Year
    }
    if(_driverController.getTriggerAxis(Hand.kLeft)>_triggerThreashold)//If trigger pulled more than half
    {
      //Not Needed This Year
    }

  }
  private void OperatorFunctions()
  {
    //Operator
    if(_operatorController.getStartButtonPressed())
    {
    }
    if(_operatorController.getBackButtonPressed())
    {

    }
    if(_operatorController.getAButtonPressed())
    {

    }
    if(_operatorController.getBButtonPressed())
    {

    }
    if(_operatorController.getXButtonPressed())
    {

    }
    if(_operatorController.getYButtonPressed())
    {

    }
    if(_operatorController.getBumperPressed(Hand.kRight))
    {

    }
    if(_operatorController.getBumperPressed(Hand.kLeft))
    {

    }
    if(_operatorController.getTriggerAxis(Hand.kRight)>_triggerThreashold)//If trigger Pulled more than half
    {
      
    }
    if(_operatorController.getTriggerAxis(Hand.kLeft)>_triggerThreashold)//If trigger pulled more than half
    {
      
    }

  }

  /**
   * This function is called periodically during teleoperated mode.
   */
  @Override
  public void teleopPeriodic() {
    //m_robotDrive.arcadeDrive(m_stick.getY(), m_stick.getX());
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
