package frc.robot.teamcode.interfaces;

public interface GenericMotor
{
    public void setPower(Double power);
    public void setDeviceId(int deviceId);
    public void getBusType();
    public void setBusType();
}