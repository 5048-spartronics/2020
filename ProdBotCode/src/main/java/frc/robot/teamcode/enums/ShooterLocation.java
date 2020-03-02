package frc.robot.teamcode.enums;

public enum ShooterLocation
{
    TrenchRun(.75),
    ControlPanel(.85),
    StartLineCenter(.65);

    private Double _locationPower;

    public Double getLocationPowerValue()
    {
        return this._locationPower;
    }
    private ShooterLocation(Double LocationPowerValue)
    {
        this._locationPower = LocationPowerValue;
    }
}