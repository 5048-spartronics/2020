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
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
//import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.revrobotics.SparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Timer;

//Color Imports
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.I2C;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorMatch;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.DriverStation;

public class Robot extends TimedRobot {
    private final Timer m_timer = new Timer();
    /*
     * --- [1] Update CAN Device IDs -----
     */     
    private CANSparkMax _leftFrontDrive ;
    private CANSparkMax _rightFrontDrive;
    private CANSparkMax _leftRearDrive;
    private CANSparkMax _rightRearDrive;
    private WPI_VictorSPX _leftShooter = new WPI_VictorSPX(5); 
    private WPI_VictorSPX _rightShooter = new WPI_VictorSPX(6);
    

    private XboxController _driveController = new XboxController(0);
    private XboxController _operatorController = new XboxController(1);
    
    private final I2C.Port i2cPort = I2C.Port.kOnboard;
    private DifferentialDrive _differentialDrive;

    private Faults _faults_L = new Faults();
    private Faults _faults_R = new Faults();
    
    private final double minShooterPower = 0.0;
    private final double maxShooterPower = 1.0;
    private final double shooterIncrement = 0.01;

    private double shooterPower = 0.0;
    private boolean shooterEnabled = false;

    private DriverStation _driverStation = null;

    private String currentColor = null;

    @Override
    public void teleopPeriodic() {
        ManageShooter();
        ManageDrive();
        ManageColorSensor();
    }

    @Override
    public void robotInit() {
        InitShooter();
        InitDrive();

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
      /*if (m_timer.get() < 2.0) {
        m_robotDrive.arcadeDrive(0.5, 0.0); // drive forwards half speed
      } else {
        m_robotDrive.stopMotor(); // stop robot
      }*/
  
      
    }

    private void InitShooter()
    {
        /* factory default values */
        _rightShooter.configFactoryDefault();
        _leftShooter.configFactoryDefault();

        /* [3] flip values so robot moves forward when stick-forward/LEDs-green */
        _rightShooter.setInverted(true); // !< Update this
        _leftShooter.setInverted(false); // !< Update this

        /*
         * [4] adjust sensor phase so sensor moves positive when Talon LEDs are green
         */
        _rightShooter.setSensorPhase(true);
        _leftShooter.setSensorPhase(true);

    }
    private void InitDrive(){
        _leftFrontDrive = new CANSparkMax(1, MotorType.kBrushless);
        _rightFrontDrive = new CANSparkMax(2, MotorType.kBrushless);
        _leftRearDrive = new CANSparkMax(3, MotorType.kBrushless);
        _rightRearDrive = new CANSparkMax(4, MotorType.kBrushless);

        _leftFrontDrive.restoreFactoryDefaults();
        _rightFrontDrive.restoreFactoryDefaults();
        _leftRearDrive.restoreFactoryDefaults();
        _rightRearDrive.restoreFactoryDefaults();

        _leftRearDrive.follow(_leftFrontDrive);
        _rightRearDrive.follow(_rightFrontDrive);

        _differentialDrive = new DifferentialDrive(_leftFrontDrive, _rightFrontDrive);
    }
    private void ManageDrive()
    {
        _differentialDrive.arcadeDrive(_driveController.getX(), _driveController.getY());

    }
    private void ManageColorSensor()
    {
        //#Buttons
        if(_driveController.getYButtonPressed())
        {
            DeployColorSensor();
        }
        if(_driveController.getXButtonPressed())
        {
            
        }
        if(_driveController.getBButtonPressed())
        {

        }
        
    }

    private void ManageShooter()
    {

        String work = "";


        //boolean btn1 = _joystick.getRawButton(1); /* is button is down, print joystick values */
        if(_operatorController.getStartButtonPressed())
        {
            ToggleShooterEnabled();
        }


        if(_operatorController.getAButtonPressed() && shooterPower > minShooterPower && shooterEnabled)
        {
            shooterPower -= shooterIncrement;
            SetShooterPower(shooterPower);
        }

        if(_operatorController.getYButtonPressed() && shooterPower < maxShooterPower && shooterEnabled)
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
        }

        SmartDashboard.putNumber("power", shooterPower);
        SmartDashboard.putBoolean("Shooter Enabled", shooterEnabled);
       
        _leftShooter.getFaults(_faults_L);
        _rightShooter.getFaults(_faults_R);

        if (_faults_L.SensorOutOfPhase) {
            work += " L sensor is out of phase";
        }
        if (_faults_R.SensorOutOfPhase) {
            work += " R sensor is out of phase";
        }

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
    private void DeployColorSensor()
    {
        SmartDashboard.putString("Status","Deploying Control Operation Arm");
        //Deploy Color Sensor

        //Read initial color

        SmartDashboard.putString("Status","Control Operation Arm deployed. Driver has the folowing options. \n\nPress X for Rotation Control \nPress B for Poisition Control (Color), \nPress A to retract arm and cancel.");

    }
    private void Rotate(int rotationCount)
    {

    }
    private void RotateToColor()
    {
        ControlPannelColor color = null;

        SmartDashboard.putString("Status", "Waiting for Color.  Driver press A to cancel");
        while(color==null && !_driveController.getAButtonPressed())
        {
            color = GetColorFromFms();
        }
        
        //Get Color from Color sensor. 

    }
    private ControlPannelColor GetColorFromFms()
    {
        ControlPannelColor output = null;
        String gameData;
        if(_driverStation==null)
            _driverStation = DriverStation.getInstance();

        gameData = _driverStation.getGameSpecificMessage();
        if(gameData.length()>0)
        {
            switch (gameData.charAt(0))
            {
                case 'B':
                    output = ControlPannelColor.Blue;
                    break;
                case 'R':
                    output = ControlPannelColor.Red;
                case 'Y':
                    output =  ControlPannelColor.Yellow;
                case 'G':
                    output = ControlPannelColor.Green;
                    break;
                
            }

        }
        return output;
    }

    public enum ControlPannelColor{
        Blue, Red, Green, Yellow
    }
}
