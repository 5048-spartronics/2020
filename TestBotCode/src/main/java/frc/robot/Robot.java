/**
 * Phoenix Software License Agreement
 *
 * Copyright (C) Cross The Road Electronics.  All rights
 * reserved.
 * 
 * Cross The Road Electronics (CTRE) licenses to you the right to 
 * use, publish, and distribute copies of CRF (Cross The Road) firmware files (*.crf) and 
 * Phoenix Software API Libraries ONLY when in use with CTR Electronics hardware products
 * as well as the FRC roboRIO when in use in FRC Competition.
 * 
 * THE SOFTWARE AND DOCUMENTATION ARE PROVIDED "AS IS" WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT
 * LIMITATION, ANY WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * CROSS THE ROAD ELECTRONICS BE LIABLE FOR ANY INCIDENTAL, SPECIAL, 
 * INDIRECT OR CONSEQUENTIAL DAMAGES, LOST PROFITS OR LOST DATA, COST OF
 * PROCUREMENT OF SUBSTITUTE GOODS, TECHNOLOGY OR SERVICES, ANY CLAIMS
 * BY THIRD PARTIES (INCLUDING BUT NOT LIMITED TO ANY DEFENSE
 * THEREOF), ANY CLAIMS FOR INDEMNITY OR CONTRIBUTION, OR OTHER
 * SIMILAR COSTS, WHETHER ASSERTED ON THE BASIS OF CONTRACT, TORT
 * (INCLUDING NEGLIGENCE), BREACH OF WARRANTY, OR OTHERWISE
 */

/**
 * Enable robot and slowly drive forward.
 * [1] If DS reports errors, adjust CAN IDs and firmware update.
 * [2] If motors are spinning incorrectly, first check gamepad (hold down btn1)
 * [3] If motors are still spinning incorrectly, correct motor inverts.
 * [4] Now that motors are driving correctly, check sensor phase.  If sensor is out of phase, adjust sensor phase.
 * [4] Is only necessary if you have sensors.
 */
package frc.robot;

import com.ctre.phoenix.motorcontrol.Faults;
//import com.ctre.phoenix.motorcontrol.InvertType;
//import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
//import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Servo;

public class Robot extends TimedRobot {
    /*
     * --- [1] Update CAN Device IDs ------
     */

    private CANSparkMax _leftMotorControllerFront = new CANSparkMax(1, MotorType.kBrushless);
    private CANSparkMax _rightMotorControllerFront = new CANSparkMax(2, MotorType.kBrushless);
    private CANSparkMax _leftMotorControllerBack = new CANSparkMax(3, MotorType.kBrushless);
    private CANSparkMax _rightMotorControllerBack = new CANSparkMax(4, MotorType.kBrushless);
    private WPI_VictorSPX _leftShooter = new WPI_VictorSPX(5);
    private WPI_VictorSPX _rightShooter = new WPI_VictorSPX(6);
    private WPI_VictorSPX _intake = new WPI_VictorSPX(7);
    private Servo _indexer = new Servo(0);
    private DifferentialDrive m_myRobot;
    private Boolean intakeEnabled;
    private Double indexposition = 90.0; // this stops servo

    private XboxController m_driverStick = new XboxController(0);
    private XboxController m_operatorStick = new XboxController(1);

    Faults _faults_L = new Faults();
    Faults _faults_R = new Faults();

    private final double minShooterPower = 0.0;
    private final double maxShooterPower = 1.0;
    private final double shooterIncrement = 0.01;

    private double shooterPower = 0.5;
    private boolean shooterEnabled = false;
    private Timer m_timer = new Timer();
    private final SendableChooser<String> m_chooser = new SendableChooser<>();
    private final static String defaultAuto = "Default Auto";
    private final static String customAuto = "Custom Auto";
    private String m_autoSelected;
    private Double ramprate = 0.5;

    @Override
    public void robotInit() {
        InitShooter();
        InitDrive();
        InitIntake();
        shooterPower = 0.75;
        m_chooser.setDefaultOption("Default Auto", defaultAuto);
        m_chooser.addOption("My Auto", customAuto);
        m_chooser.setDefaultOption("Default Auto", defaultAuto);
        SmartDashboard.putData("Auto choices", m_chooser);
        _leftMotorControllerFront.setOpenLoopRampRate(ramprate);
        _rightMotorControllerFront.setOpenLoopRampRate(ramprate);
        _leftMotorControllerBack.setOpenLoopRampRate(ramprate);
        _rightMotorControllerBack.setOpenLoopRampRate(ramprate);
    }

    // auton
    public void autonomousInit() {
        m_timer.reset();
        m_timer.start();
        m_autoSelected = m_chooser.getSelected();
        m_autoSelected = SmartDashboard.getString("Auto Choices", m_autoSelected);
        System.out.println("Auto selected: " + m_autoSelected);
        _indexer.setAngle(indexposition);
    }

    public void autonomousPeriodic() {
        switch (m_autoSelected) {
        case customAuto:
            while (m_timer.get() > 14.0) {
                m_myRobot.arcadeDrive(0.5, 0.0);
            }
            break;

        case defaultAuto:
        default:
            while (m_timer.get() < 1.75) {
                m_myRobot.arcadeDrive(0.5, 0.0);
            }
            SetShooterPower(0.75);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
                while (m_timer.get() < 8.0){
                    _indexer.setAngle(30.0);
                }
                _indexer.setAngle(indexposition);
                break;
                
            }
        } 

    @Override
    public void teleopPeriodic() {
        ManageShooter();
        ManageDrive();
        ManageIntake();        
    }
        
        //added in auto stuff into robotinit

    private void InitDrive(){
        m_myRobot = new DifferentialDrive(_leftMotorControllerFront, _rightMotorControllerFront );
        _leftMotorControllerFront.restoreFactoryDefaults();
        _rightMotorControllerFront.restoreFactoryDefaults();
        _leftMotorControllerBack.restoreFactoryDefaults();
        _rightMotorControllerBack.restoreFactoryDefaults();
        _leftMotorControllerBack.follow(_leftMotorControllerFront);
        _rightMotorControllerBack.follow(_rightMotorControllerFront);
        

    }

    private void InitShooter()
    {
        _indexer.setAngle(indexposition);
        /* factory default values */


        /* [3] flip values so robot moves forward when stick-forward/LEDs-green */
        _rightShooter.setInverted(true); // !< Update this
        _leftShooter.setInverted(true); // !< Update this

        /*
         * [4] adjust sensor phase so sensor moves positive when Talon LEDs are green
         */
        _rightShooter.setSensorPhase(true);
        _leftShooter.setSensorPhase(true);

    }

    private void InitIntake(){
        _intake.setSensorPhase(true);
        _intake.setInverted(true);
        intakeEnabled=false;
    }

    private void ManageDrive(){
        double x = m_driverStick.getX(Hand.kLeft);
        
        if (x>.3 || x<-.3)
        {
            m_myRobot.arcadeDrive(m_driverStick.getY(Hand.kLeft), -1* m_driverStick.getX(Hand.kLeft));

        }
        else
        {
            m_myRobot.arcadeDrive(m_driverStick.getY(Hand.kLeft), 0);
        }


    }

    private void ManageShooter()
    {

        //String work = "";


        //boolean btn1 = _joystick.getRawButton(1); /* is button is down, print joystick values */
        if(m_operatorStick.getStartButtonPressed())
        {
            ToggleShooterEnabled();
            m_timer.reset();
            m_timer.start();
            if (m_timer.get() > 0.25){
                _indexer.setAngle(30.0);
            }
            
            
        }


        if(m_operatorStick.getAButtonPressed() && shooterPower > minShooterPower && shooterEnabled)
        {
            shooterPower -= shooterIncrement;
            SetShooterPower(shooterPower);
            
        }

        if(m_operatorStick.getYButtonPressed() && shooterPower < maxShooterPower && shooterEnabled)
        {
            shooterPower += shooterIncrement;
            SetShooterPower(shooterPower);
        }

        if(shooterEnabled)
        {
            SetShooterPower(shooterPower);
        }
        else{
            SetShooterPower(0.0);
            _indexer.setAngle(90.0);

        }

        SmartDashboard.putNumber("power", shooterPower);
        SmartDashboard.putBoolean("Shooter Enabled", shooterEnabled);
       
        _leftShooter.getFaults(_faults_L);
        _rightShooter.getFaults(_faults_R);

        if (_faults_L.SensorOutOfPhase) {
           // work += " L sensor is out of phase";
        }
        if (_faults_R.SensorOutOfPhase) {
            //work += " R sensor is out of phase";
        }

    }

    private void ManageIntake()
    {
      if (m_operatorStick.getBButtonPressed())  {
          if (intakeEnabled){
            _intake.set(0.0);
            intakeEnabled = !intakeEnabled; 
          } else {
            _intake.set(0.95);  
            intakeEnabled = !intakeEnabled;
            SmartDashboard.putBoolean("Intake Enabled", intakeEnabled); 
          }
      }
      SmartDashboard.putBoolean("Intake Enabled", intakeEnabled);
      SmartDashboard.putNumber("Intake Power", 0.5);
    }

    private void SetShooterPower(double power)
    {
        _rightShooter.set(power);
        _leftShooter.set(power);
        
    }
    private void ToggleShooterEnabled()
    {
        shooterEnabled = !shooterEnabled;
    }
    
}
