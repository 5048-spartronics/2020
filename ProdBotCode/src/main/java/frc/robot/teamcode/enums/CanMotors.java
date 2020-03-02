package frc.robot.teamcode.enums;

public enum CanMotors
{
    //SET ALL CAN IDs here
    LeftFrontDriveMotor(1),
        RightFrontDriveMotor(2),
        LeftRearDriveMotor(3), 
        RightRearDriveMotor(4), 
        ShooterLeftMotor(5), 
        ShooterRightMotor(6), 
        ShooterIndexer(7), 
        IntakeMotor(8), 
        IntakeDeploy(9),
        ControlPanelMotor(10); 

    private int canId;

    public int getCanId()
    {
        return this.canId;
    }
    private CanMotors(int CanId)
    {
        this.canId = CanId;
    }

}