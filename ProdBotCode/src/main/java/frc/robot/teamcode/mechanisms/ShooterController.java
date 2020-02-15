package frc.robot.teamcode.mechanisms;

import edu.wpi.first.wpilibj.XboxController;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

public class ShooterController
{
    private final Double _shooterEnabledPower = 0.75;
    private final Double _shooterDisabledPower = 0.0;
    private Double _shooterCurrentPower;
    private final Double _shooterPowerIncrement = 0.01;
    private Boolean _shooterEnabled;
    private WPI_VictorSPX _leftShooter = new WPI_VictorSPX(5); 
    private WPI_VictorSPX _rightShooter = new WPI_VictorSPX(6);

    public ShooterController(XboxController OperatorXboxController)
    {
        _shooterEnabled = false;
        _shooterCurrentPower = _shooterDisabledPower;
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

    public Boolean getShooterEnabled()
    {
        return _shooterEnabled;
    }
    public void Enable()
    {
        if(!_shooterEnabled)
        {
            SetShooterPower(_shooterEnabledPower);
            _shooterEnabled = true;
        }
    }
    public void Disable()
    {
        if(_shooterEnabled)
        {
            SetShooterPower(_shooterDisabledPower);
            _shooterEnabled=false;
        }
    }
    public void ShooterIncreasePower()
    {
        SetShooterPower(_shooterCurrentPower+_shooterPowerIncrement);
    }
    public void ShooterDecreasePower()
    {
        SetShooterPower(_shooterCurrentPower-_shooterPowerIncrement);

    }
    
    private void SetShooterPower(double power)
    {
        _rightShooter.set(power);
        _leftShooter.set(power);
        _shooterCurrentPower = power;
        
    }
}