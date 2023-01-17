package unsw.devices;

import java.util.HashMap;
import unsw.utils.Angle;
import unsw.file.File;


public abstract class Device{

    private String deviceId;
    private Angle position;
    private final HashMap<String, File> files = new HashMap<String, File>();

    // Constructor
    public Device(String deviceId, Angle position) {
        this.deviceId = deviceId;
        this.position = position;
    }

    // Getters and setters for files
    public HashMap<String, File> getFiles() {
        return files;
    }
    public void addFile(File file) {
        this.files.put(file.getTitle(), file);
    }

    // Only getters. These attributes cannot be altered manually.
    public String getDeviceId() {
        return this.deviceId;
    }
    public Angle getPosition() {
        return this.position;
    }

    public abstract int getRange();


}
