package unsw.devices;
import unsw.utils.Angle;

public class LaptopDevice extends Device {
    
    private static final int RANGE = 100000;

    public LaptopDevice(String deviceId, Angle position) {
        super(deviceId, position);

    }

    public int getRange() {
        return RANGE;
    }
}