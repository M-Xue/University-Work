package unsw.satellites;

import unsw.file.File;

import java.util.HashMap;
import unsw.utils.Angle;
import unsw.blackout.FileTransferException;


public abstract class FileHandlingSatellite extends Satellite{
    public FileHandlingSatellite(String satelliteId, double height, Angle position) {
        super(satelliteId, height, position);
    }

    public abstract HashMap<String, File> getFiles();
    public abstract HashMap<String, File> getSendingFiles();
    public abstract void addFile(File file);
    
    public abstract int getByteStorageSize();
    public abstract int getFileStorageSize();
    public abstract int getByteDownloadBandwidth();
    public abstract int getByteUploadBandwidth();

    public abstract int calculateUploadSatelliteBandwidth();
    public abstract int calculateDownloadSatelliteBandwidth();

    public void checkFileDownloadErrors(File newFile) throws FileTransferException {
        int currSatelliteByteStorage = 0;
        int downloadingFiles = 0;
        for (File f : this.getFiles().values()) {
            currSatelliteByteStorage += f.getFullSize();
            if (!f.isComplete()) {
                downloadingFiles += 1;
            }
        }
        // Checking for bandwidth download limits
        if (downloadingFiles == this.getByteDownloadBandwidth()) {
            throw new FileTransferException.VirtualFileNoBandwidthException(this.getSatelliteId());
        }
        
        // Checking for bytes storage size capacity
        if (currSatelliteByteStorage + newFile.getFullSize() > this.getByteStorageSize()) {
            throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Storage Reached");
        }

        // Checking for file storage size capacity
        if (this.getFiles().size() == this.getFileStorageSize()) {
            throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Files Reached");
        }

        // Checking for if file already existing in target satellite
        if (this.getFiles().containsKey(newFile.getTitle())) {
            throw new FileTransferException.VirtualFileAlreadyExistsException(newFile.getTitle());
        }
    }

    public void checkFileUploadErrors(File newFile) throws FileTransferException {
        // Checking for bandwidth upload limits
        if (this.getSendingFiles().size() == this.getByteUploadBandwidth()) {
            throw new FileTransferException.VirtualFileNoBandwidthException(this.getSatelliteId());
        }

        // Checking if the file of interest does not exist in source or is a partial download in source, throw an error.
        if (!(this.getFiles().containsKey(newFile.getTitle())) || !(this.getFiles().get(newFile.getTitle()).isComplete())) {
            throw new FileTransferException.VirtualFileNotFoundException(newFile.getTitle());
        }
    }
}
