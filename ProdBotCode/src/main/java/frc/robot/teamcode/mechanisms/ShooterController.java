package frc.robot.teamcode.mechanisms;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.teamcode.enums.ShooterLocation;

import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

public class ShooterController
{
    private final int _defaultShooterLocation = 0;
    private final Double _shooterDefaultPower = 0.75;
    private final Double _shooterDisabledPower = 0.0;
    private final Double _shooterPowerIncrement = 0.01;
    private Double _shooterCurrentPower;
    private Boolean _shooterEnabled;
    private WPI_VictorSPX _leftShooter = new WPI_VictorSPX(5); 
    private WPI_VictorSPX _rightShooter = new WPI_VictorSPX(6);
    private ArrayList<ShooterLocation> _shooterLocations;
    private int _currentShooterLocation;


    public ShooterController(XboxController OperatorXboxController)
    {
        InitShooterLocations();
        InitShooterMotors();
    }

    public Boolean getShooterEnabled()
    {
        return _shooterEnabled;
    }
    public void ResetShooterPower()
    {
        _shooterCurrentPower = _shooterDefaultPower;
    }
    public void ResetShooterLocation()
    {
        _currentShooterLocation = _defaultShooterLocation;
    }
    public void Enable()
    {
        if(!_shooterEnabled)
        {
            setShooterPower(_shooterCurrentPower);
            _shooterEnabled = true;
        }
    }
    public void Disable()
    {
        if(_shooterEnabled)
        {
            setShooterPower(_shooterDisabledPower);
            _shooterEnabled=false;
        }
    }
    public void ShooterIncreasePower()
    {
        setShooterPower(_shooterCurrentPower+_shooterPowerIncrement);
    }
    public void ShooterDecreasePower()
    {
        setShooterPower(_shooterCurrentPower-_shooterPowerIncrement);
    }
    public void setShooterLocation(ShooterLocation location)
    {
        int loc = _shooterLocations.indexOf(location);

        if(loc==-1)
            return;

        _currentShooterLocation = loc;
        setShooterPower(location.getLocationPowerValue());
    }
    public void IncrementShooterLocation()
    {
        _currentShooterLocation++;
        setShooterPower(_shooterLocations.get(_currentShooterLocation).getLocationPowerValue());
    }
    public Double getCurrentShooterPower()
    {
        return _shooterCurrentPower;
    }
    
    private void setShooterPower(double power)
    {
        _rightShooter.set(power);
        _leftShooter.set(power);
        _shooterCurrentPower = power;
        
    }

    private void InitShooterMotors()
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
    private void InitShooterLocations()
    {
        _shooterLocations = new ArrayList<ShooterLocation>();

        _shooterLocations.add(0,ShooterLocation.TrenchRun);
        _shooterLocations.add(1,ShooterLocation.ControlPanel);
        _shooterLocations.add(2,ShooterLocation.StartLineCenter);
    }
}