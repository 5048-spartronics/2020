package frc.robot.teamcode.mechanisms;

import edu.wpi.first.wpilibj.AnalogGyro;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.robot.teamcode.enums.AutonStartingPosition;
import frc.robot.teamcode.enums.CanMotors;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class DriveController
{
    private final Double _driveIncrement = 0.25;
    XboxController _driveController;
    private CANSparkMax _leftFrontDrive ;
    private CANSparkMax _rightFrontDrive;
    private CANSparkMax _leftRearDrive;
    private CANSparkMax _rightRearDrive;
    private DifferentialDrive _differentialDrive;
    private AnalogGyro _gyro;

    public DriveController(XboxController DriverXboxController)
    {
        _gyro = new AnalogGyro(0);
        _gyro.reset();
        _gyro.setSensitivity(0.0128);//Adjust this if not reporting 360 degrees

        _driveController = DriverXboxController;
        _leftFrontDrive = new CANSparkMax(CanMotors.LeftFrontDriveMotor.getCanId(), MotorType.kBrushless);
        _rightFrontDrive = new CANSparkMax(CanMotors.RightFrontDriveMotor.getCanId(), MotorType.kBrushless);
        _leftRearDrive = new CANSparkMax(CanMotors.LeftRearDriveMotor.getCanId(), MotorType.kBrushless);
        _rightRearDrive = new CANSparkMax(CanMotors.RightRearDriveMotor.getCanId(), MotorType.kBrushless);

        _leftFrontDrive.restoreFactoryDefaults();
        _rightFrontDrive.restoreFactoryDefaults();
        _leftRearDrive.restoreFactoryDefaults();
        _rightRearDrive.restoreFactoryDefaults();

        _leftRearDrive.follow(_leftFrontDrive);
        _rightRearDrive.follow(_rightFrontDrive);

        _differentialDrive = new DifferentialDrive(_leftFrontDrive, _rightFrontDrive);
        
    }
    public void Drive()
    {
        _driveController.getX(Hand.kLeft);
        _differentialDrive.arcadeDrive(_driveController.getX(), _driveController.getY());
    }
    public void DriveForward()
    {
        _differentialDrive.arcadeDrive(0,_driveIncrement);
    }
    public void DriveBackward()
    {
        _differentialDrive.arcadeDrive(0,-_driveIncrement);
    }
    public void TurnLeft(int degrees)
    {
        while(_gyro.getAngle()>(-1*degrees))
        {
            _differentialDrive.arcadeDrive(0, -1*_driveIncrement);
        }
        _gyro.reset();
    }
    public void TurnRight(int degrees)
    {
        while(_gyro.getAngle()<degrees)
        {
            _differentialDrive.arcadeDrive(0, _driveIncrement);
        }
        _gyro.reset();
    }
    /*
    public void AutonDrive(AutonStartingPosition type)
    {
        switch(type)
        {
            case Drive://Center of field
                CenterAutonDrive();
                break;
            case MidPosition: // Halfway left or right
                MidAutonDrive();
                break;
            case SidePosition: //All the way left or right
                SideAutonDrive();
                break;
        }
    }
    private void MidAutonDrive()
    {

    }
    private void CenterAutonDrive()
    {

    }
    private void SideAutonDrive()
    {

    }*/
}