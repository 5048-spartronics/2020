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
//import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
    /*
     * --- [1] Update CAN Device IDs ------
     */
    WPI_VictorSPX _rightShooter = new WPI_VictorSPX(0);
    WPI_VictorSPX _leftShooter = new WPI_VictorSPX(1);  

    XboxController _controller = new XboxController(0);

    Faults _faults_L = new Faults();
    Faults _faults_R = new Faults();
    
    double shooterPower = 0.5;
    boolean shooterEnabled = false;

    @Override
    public void teleopPeriodic() {

        String work = "";


        //boolean btn1 = _joystick.getRawButton(1); /* is button is down, print joystick values */
        if(_controller.getStartButtonPressed())
        {
            if(shooterEnabled)
            {
                shooterEnabled = false;
            }
            else{
                shooterEnabled = true;
            }
        }


        if(_controller.getAButtonPressed() && shooterPower > 0.0 && shooterEnabled)
        {
            shooterPower -= 0.01;
            _rightShooter.set(shooterPower);
            _leftShooter.set(shooterPower);
        }

        if(_controller.getYButtonPressed() && shooterPower < 1.0 && shooterEnabled)
        {
            shooterPower += 0.01;
            _rightShooter.set(shooterPower);
            _leftShooter.set(shooterPower);
        }

        if(shooterEnabled)
        {
            _rightShooter.set(shooterPower);
            _leftShooter.set(shooterPower);
        }
        else{
            _rightShooter.set(0);
            _leftShooter.set(0);
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

    @Override
    public void robotInit() {
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
}
