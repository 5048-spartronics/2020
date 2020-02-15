package frc.robot.teamcode.mechanisms;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class DriveController
{
    XboxController _driveController;
    private CANSparkMax _leftFrontDrive ;
    private CANSparkMax _rightFrontDrive;
    private CANSparkMax _leftRearDrive;
    private CANSparkMax _rightRearDrive;
    private DifferentialDrive _differentialDrive;

    public DriveController(XboxController DriverXboxController)
    {
        _driveController = DriverXboxController;
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
    public void Drive()
    {
        _differentialDrive.arcadeDrive(_driveController.getX(), _driveController.getY());
    }
}