package unsw.devices;
import unsw.utils.Angle;

public class DesktopDevice extends Device {
    
    private static final int RANGE = 200000;

    public DesktopDevice(String deviceId, Angle position) {
        super(deviceId, position);

    }

    public int getRange() {
        return RANGE;
    }
}
