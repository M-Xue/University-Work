package unsw.satellites;

import java.util.HashMap;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;
import unsw.file.File;


public class TeleportingSatellite extends FileHandlingSatellite{

    private static final int LINEAR_VELOCITY = 1000;
    private static final int RANGE = 200000;

    // Teleporting Satellites have infinite file storage size, i.e., can have as many number of files as possible as long as the sum of the size of the files is under 200 bytes.
    private static final int BYTE_STORAGE_SIZE = 200;
    private static final int FILE_STORAGE_SIZE = Integer.MAX_VALUE;

    private static final int BYTE_UPLOAD_BANDWIDTH = 10;
    private static final int BYTE_DOWNLOAD_BANDWIDTH = 15;

    private final HashMap<String, File> FILES = new HashMap<String, File>();
    private final HashMap<String, File> SENDING_FILES = new HashMap<String, File>();
    private final HashMap<String, File> COMPLETED_SENDING_FILES = new HashMap<String, File>();
    private final HashMap<String, File> COMPLETED_DOWNLOADING_FILES = new HashMap<String, File>();

    private int currDirection = MathsHelper.ANTI_CLOCKWISE;
    private boolean teleported = false;

    public TeleportingSatellite(String satelliteId, double height, Angle position) {
        super(satelliteId, height, position);
    }
    
    // * GETTERS AND SETTERS FOR FILE SUPPORT *************************************/
    public HashMap<String, File> getFiles() {
        return this.FILES;
    }
    public HashMap<String, File> getSendingFiles() {
        return this.SENDING_FILES;
    }
    public HashMap<String, File> getCompletedSendingFiles() {
        return this.COMPLETED_SENDING_FILES;
    }
    public HashMap<String, File> getCompletedDownloadingFiles() {
        return this.COMPLETED_DOWNLOADING_FILES;
    }

    public void addFile(File file) {
        this.FILES.put(file.getTitle(), file);
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

    public int calculateUploadSatelliteBandwidth() {
        return BYTE_UPLOAD_BANDWIDTH / this.getSendingFiles().size();
    }
    public int calculateDownloadSatelliteBandwidth() {
        int numReceivingFiles = 0;
        for (File file : this.getFiles().values()) {
            if (!file.isComplete()) {
                numReceivingFiles++;
            }
        }
        return BYTE_DOWNLOAD_BANDWIDTH / (numReceivingFiles + this.getCompletedDownloadingFiles().size());
    }

    public int getRange() {
        return RANGE;
    }

    // * MOVEMENT *****************************************************************/
    private double getAngularVelocity() {
        return LINEAR_VELOCITY / this.getHeight();
    }
    public void move() {
        this.setTeleported(false);
        Angle initialPosition = this.getPosition();
        Angle positionChange = Angle.fromRadians(this.getAngularVelocity());

        if (this.currDirection == MathsHelper.ANTI_CLOCKWISE) {
            Angle newPosition = Angle.fromDegrees(Satellite.normaliseDegrees(initialPosition.add(positionChange).toDegrees()));
            this.setPosition(newPosition);
        } else if (this.currDirection == MathsHelper.CLOCKWISE) {
            Angle newPosition = Angle.fromDegrees(Satellite.normaliseDegrees(initialPosition.subtract(positionChange).toDegrees()));
            this.setPosition(newPosition);
        }
        // * TELEPORTING **********************************************************/
        // This means the satellite has definitely passed the 180 degree mark and will be teleporting an changing direction.
        if (positionChange.toDegrees() >= 359) {
            this.setPosition(Angle.fromDegrees(0));
            this.currDirection *= -1;
            this.setTeleported(true);
            
        // This means the satellite started at the bottom of the angle circle and is going CLOCKWISE and that it has passed the 180 degrees mark.
        } else if (currDirection == MathsHelper.CLOCKWISE && this.getPosition().toDegrees() <= 180) {
            this.setPosition(Angle.fromDegrees(0));
            this.currDirection = MathsHelper.ANTI_CLOCKWISE;
            this.setTeleported(true);

        // This means the satellite started at the top of the angle circle and is going ANTI_CLOCKWISE and that it has passed the 180 degrees mark.    
        } else if (currDirection == MathsHelper.ANTI_CLOCKWISE && ((initialPosition.toDegrees() >= 0 && initialPosition.toDegrees() <= 180 && this.getPosition().toDegrees() >= 180) || (initialPosition.toDegrees() >= 180 && positionChange.toDegrees() >= (360 - (initialPosition.toDegrees() - 180))))) {
            this.setPosition(Angle.fromDegrees(0));
            this.currDirection = MathsHelper.CLOCKWISE;
            this.setTeleported(true);

        }
    }
    public boolean getTeleported() {
        return this.teleported;
    }
    public void setTeleported(boolean teleported) {
        this.teleported = teleported;
    }
}
