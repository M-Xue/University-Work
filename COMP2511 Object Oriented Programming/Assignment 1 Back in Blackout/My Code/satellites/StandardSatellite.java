package unsw.satellites;

import java.util.HashMap;

import unsw.file.File;
import unsw.utils.Angle;

public class StandardSatellite extends FileHandlingSatellite {

    private static final int LINEAR_VELOCITY = 2500;
    private static final int RANGE = 150000;

    private static final int FILE_STORAGE_SIZE = 3;
    private static final int BYTE_STORAGE_SIZE = 80;

    private static final int BYTE_UPLOAD_BANDWIDTH = 1;
    private static final int BYTE_DOWNLOAD_BANDWIDTH = 1;

    private final HashMap<String, File> FILES = new HashMap<String, File>();
    private final HashMap<String, File> SENDING_FILES = new HashMap<String, File>();

    public StandardSatellite(String satelliteId, double height, Angle position) {
        super(satelliteId, height, position);
    }

    // Getters and setters for file support
    public HashMap<String, File> getFiles() {
        return this.FILES;
    }
    public HashMap<String, File> getSendingFiles() {
        return this.SENDING_FILES;
    }

    public void addFile(File file) {
        this.FILES.put(file.getTitle(), file);
    }

    public int calculateUploadSatelliteBandwidth() {
        return 1;
    }
    public int calculateDownloadSatelliteBandwidth() {
        return 1;
    }

    public int getRange() {
        return RANGE;
    }
    public int getByteStorageSize() {
        return BYTE_STORAGE_SIZE;
    }
    public int getFileStorageSize() {
        return FILE_STORAGE_SIZE;
    }
    public int getByteDownloadBandwidth() {
        return BYTE_DOWNLOAD_BANDWIDTH;
    }
    public int getByteUploadBandwidth() {
        return BYTE_UPLOAD_BANDWIDTH;
    }

    // Movement
    public double getAngularVelocity() {
        return LINEAR_VELOCITY / this.getHeight();
    }

    public void move() {
        Angle currPosition = this.getPosition();
        Angle positionChange = Angle.fromRadians(this.getAngularVelocity());
        Angle newPosition = currPosition.subtract(positionChange);
        if (newPosition.toDegrees() < 0 || newPosition.toDegrees() >= 360) {
            double newDegrees = Satellite.normaliseDegrees(newPosition.toDegrees());
            newPosition = Angle.fromDegrees(newDegrees);
        } 
        this.setPosition(newPosition);
    }
}
