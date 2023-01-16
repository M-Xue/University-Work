package unsw.devices;
import unsw.utils.Angle;

public class HandheldDevice extends Device {
    
    private static final int RANGE = 50000;

    public HandheldDevice(String deviceId, Angle position) {
        super(deviceId, position);
    }

    public int getRange() {
        return RANGE;
    }
}