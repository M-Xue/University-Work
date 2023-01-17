package unsw.blackout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.satellites.FileHandlingSatellite;
import unsw.satellites.RelaySatellite;
import unsw.satellites.Satellite;
import unsw.satellites.StandardSatellite;
import unsw.satellites.TeleportingSatellite;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;
import unsw.devices.*;
import unsw.file.File;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

public class BlackoutController {

    private final HashMap<String, Satellite> ALL_SATELLITES = new HashMap<String, Satellite>();
    private final HashMap<String, Device> ALL_DEVICES = new HashMap<String, Device>();
    private final HashMap<String, File> PENDING_FILES = new HashMap<String, File>();

    public void createDevice(String deviceId, String type, Angle position) {
        // Task 1a)
        // Don't need default because we can assume we are never giving an invalid input
        switch (type) {
            case "HandheldDevice":
                this.ALL_DEVICES.put(deviceId, new HandheldDevice(deviceId, position));
            break;
            case "LaptopDevice":
                this.ALL_DEVICES.put(deviceId, new LaptopDevice(deviceId, position));
            break;
            case "DesktopDevice":
                this.ALL_DEVICES.put(deviceId, new DesktopDevice(deviceId, position));
            break;
        }
    }

    public void removeDevice(String deviceId) {
        // Task 1b)
        this.ALL_DEVICES.remove(deviceId);
    }

    public List<String> listDeviceIds() {
        // Task 1e)
        Set<String> deviceIdsSet = this.ALL_DEVICES.keySet();
        ArrayList<String> deviceIdsList = new ArrayList<String>();
        deviceIdsList.addAll(deviceIdsSet);
        return deviceIdsList;
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        // Task 1g)
        Device device = this.ALL_DEVICES.get(deviceId);
        File file = new File(filename, content, "", device.getDeviceId(), true);
        device.addFile(file);
    }

    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        // Task 1c)
        // Don't need default because we can assume we are never giving an invalid input
        switch (type) {
            case "StandardSatellite":
                this.ALL_SATELLITES.put(satelliteId, new StandardSatellite(satelliteId, height, position));
            break;
            case "TeleportingSatellite":
                this.ALL_SATELLITES.put(satelliteId, new TeleportingSatellite(satelliteId, height, position));
            break;
            case "RelaySatellite":
                this.ALL_SATELLITES.put(satelliteId, new RelaySatellite(satelliteId, height, position));
            break;
        }
    }

    public void removeSatellite(String satelliteId) {
        // Task 1d)
        this.ALL_SATELLITES.remove(satelliteId);
    }

    public List<String> listSatelliteIds() {
        // Task 1f)
        Set<String> satelliteIdsSet = this.ALL_SATELLITES.keySet();
        ArrayList<String> satelliteIdsList = new ArrayList<String>();
        satelliteIdsList.addAll(satelliteIdsSet);
        return satelliteIdsList;
    }

    public EntityInfoResponse getInfo(String id) {
        // Task 1h)
        if (this.ALL_SATELLITES.containsKey(id)) {
            Satellite satellite = this.ALL_SATELLITES.get(id);
            if (satellite instanceof FileHandlingSatellite) {
                FileHandlingSatellite FHS = (FileHandlingSatellite) satellite;
                HashMap<String, File> files = FHS.getFiles();
                HashMap<String, FileInfoResponse> fileInfoResponseMap = new HashMap<String, FileInfoResponse>();
                for (File file : files.values()) {
                    FileInfoResponse fileRes = new FileInfoResponse(file.getTitle(), file.getDownloadedContent(), file.getFullSize(), file.isComplete()); 
                    fileInfoResponseMap.put(file.getTitle(), fileRes); 
                }
                if (satellite instanceof StandardSatellite) {
                    return new EntityInfoResponse(satellite.getSatelliteId(), satellite.getPosition(), satellite.getHeight(), "StandardSatellite", fileInfoResponseMap);
                } else if (satellite instanceof TeleportingSatellite) {
                    return new EntityInfoResponse(satellite.getSatelliteId(), satellite.getPosition(), satellite.getHeight(), "TeleportingSatellite", fileInfoResponseMap);
                }
            } else if (satellite instanceof RelaySatellite) {
                return new EntityInfoResponse(satellite.getSatelliteId(), satellite.getPosition(), satellite.getHeight(), "RelaySatellite");
            }

        } else if (this.ALL_DEVICES.containsKey(id)) {
            Device device = ALL_DEVICES.get(id);

            HashMap<String, File> files = device.getFiles();
            HashMap<String, FileInfoResponse> fileInfoResponseMap = new HashMap<String, FileInfoResponse>();

            for (File file : files.values()) {
                FileInfoResponse fileRes = new FileInfoResponse(file.getTitle(), file.getDownloadedContent(), file.getFullSize(), file.isComplete()); 
                fileInfoResponseMap.put(file.getTitle(), fileRes); 
            }

            if (device instanceof HandheldDevice) {
                return new EntityInfoResponse(device.getDeviceId(), device.getPosition(), RADIUS_OF_JUPITER, "HandheldDevice", fileInfoResponseMap);
            } else if (device instanceof LaptopDevice) {
                return new EntityInfoResponse(device.getDeviceId(), device.getPosition(), RADIUS_OF_JUPITER, "LaptopDevice", fileInfoResponseMap);
            } else if (device instanceof DesktopDevice) {
                return new EntityInfoResponse(device.getDeviceId(), device.getPosition(), RADIUS_OF_JUPITER, "DesktopDevice", fileInfoResponseMap);
            }
        }
        return null; 
    }

    public void simulate() {
        // Task 2a)

        // * MOVING ALL SATELLITES
        for (Satellite s : ALL_SATELLITES.values()) {
            s.move();
            if (s instanceof TeleportingSatellite) {
                this.handleFileChangesForTeleport((TeleportingSatellite)s);
            }
        }

        // * INCREMENTING PENDING FILES
        // Need to copy the panding files hash map so that removing elements mid for loop won't break the iteration.
        HashMap<String, File> pendingFilesCopy = new HashMap<String, File>();
        pendingFilesCopy.putAll(this.PENDING_FILES);

        for (File pendingFile: pendingFilesCopy.values()) {

            String sourceId = pendingFile.getSourceId();
            String targetId = pendingFile.getTargetId();

            List<String> communicableEntities = this.communicableEntitiesInRange(sourceId);
            if (!communicableEntities.contains(targetId)) {
                this.PENDING_FILES.remove(pendingFile.getTitle());
                if (this.ALL_SATELLITES.containsKey(pendingFile.getSourceId())) {
                    FileHandlingSatellite sourceSatellite = (FileHandlingSatellite)this.ALL_SATELLITES.get(pendingFile.getSourceId());
                    sourceSatellite.getSendingFiles().remove(pendingFile.getTitle());
                } 
                
                if (this.ALL_SATELLITES.containsKey(pendingFile.getTargetId())) {
                    FileHandlingSatellite targetSatellite = (FileHandlingSatellite)this.ALL_SATELLITES.get(pendingFile.getTargetId());
                    targetSatellite.getFiles().remove(pendingFile.getTitle());
                } else if (this.ALL_DEVICES.containsKey(pendingFile.getTargetId())) {
                    Device targetDevice = this.ALL_DEVICES.get(pendingFile.getTargetId());
                    targetDevice.getFiles().remove(pendingFile.getTitle());
                }
                continue;
            }

            if (this.ALL_DEVICES.containsKey(sourceId) && this.ALL_SATELLITES.containsKey(targetId)) {
                Device source = this.ALL_DEVICES.get(sourceId);
                Satellite target = this.ALL_SATELLITES.get(targetId);
                pendingFile.download(this.calculateByteBandwidth(source,target));
                if (pendingFile.isComplete() && target instanceof TeleportingSatellite) {
                    TeleportingSatellite TSTarget = (TeleportingSatellite)target;
                    TSTarget.getCompletedDownloadingFiles().put(pendingFile.getTitle(), pendingFile);
                }

            } else if (this.ALL_SATELLITES.containsKey(sourceId) && this.ALL_DEVICES.containsKey(targetId)) {
                Satellite source = this.ALL_SATELLITES.get(sourceId);
                Device target = this.ALL_DEVICES.get(targetId);
                pendingFile.download(this.calculateByteBandwidth(source,target));

            } else if (this.ALL_SATELLITES.containsKey(sourceId) && this.ALL_SATELLITES.containsKey(targetId)) {
                Satellite source = this.ALL_SATELLITES.get(sourceId);
                Satellite target = this.ALL_SATELLITES.get(targetId);
                pendingFile.download(this.calculateByteBandwidth(source,target));
                if (pendingFile.isComplete() && target instanceof TeleportingSatellite) {
                    TeleportingSatellite TSTarget = (TeleportingSatellite)target;
                    TSTarget.getCompletedDownloadingFiles().put(pendingFile.getTitle(), pendingFile);
                }
            }

            if (pendingFile.isComplete()) {
                this.PENDING_FILES.remove(pendingFile.getTitle());
                if (this.ALL_SATELLITES.containsKey(pendingFile.getSourceId()) && this.ALL_SATELLITES.get(pendingFile.getSourceId()) instanceof TeleportingSatellite) {
                    TeleportingSatellite sourceSatellite = (TeleportingSatellite)this.ALL_SATELLITES.get(pendingFile.getSourceId());
                    sourceSatellite.getCompletedSendingFiles().put(pendingFile.getTitle(), pendingFile);
                }
            }
        }
        for (Satellite s : this.ALL_SATELLITES.values()) {
            if (s instanceof TeleportingSatellite) {
                TeleportingSatellite ts = (TeleportingSatellite)s;
                ts.getCompletedDownloadingFiles().clear();
                ts.getSendingFiles().keySet().removeAll(ts.getCompletedSendingFiles().keySet());
                ts.getCompletedSendingFiles().clear();
            }
        }
    }

       /**
     * Simulate for the specified number of minutes.
     * You shouldn't need to modify this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }
    

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        // Task 2 c)

        // * Source is a Device *******************************************************************/
        if (this.ALL_DEVICES.containsKey(fromId)) {
            Device sourceDevice = this.ALL_DEVICES.get(fromId);
            HashMap<String, File> sourceFiles = sourceDevice.getFiles();

            // If the file of interest does not exist in source or is a partial download in source, throw an error.
            if (!(sourceFiles.containsKey(fileName)) || !(sourceFiles.get(fileName).isComplete())) {
                throw new FileTransferException.VirtualFileNotFoundException(fileName);
            }
            File sourceFile = sourceFiles.get(fileName);
            File sentFile = new File(sourceFile.getTitle(), sourceFile.getFullContent(), fromId, toId, false);

            // We now have the source device and the new file

            // * Target is a Satellite ************************************************************/
            if (this.ALL_SATELLITES.containsKey(toId)) {
                FileHandlingSatellite targetSatellite = (FileHandlingSatellite)this.ALL_SATELLITES.get(toId);
                targetSatellite.checkFileDownloadErrors(sentFile);
                targetSatellite.addFile(sentFile);
                this.PENDING_FILES.put(sentFile.getTitle(), sentFile);
            }

        // * Source is a Satellite ****************************************************************/
        } else if (this.ALL_SATELLITES.containsKey(fromId)) {
            FileHandlingSatellite sourceSatellite = (FileHandlingSatellite)this.ALL_SATELLITES.get(fromId);

            HashMap<String, File> sourceFiles = sourceSatellite.getFiles();

            // If the file of interest does not exist in source or is a partial download in source, throw an error.
            if (!(sourceFiles.containsKey(fileName)) || !(sourceFiles.get(fileName).isComplete())) {
                throw new FileTransferException.VirtualFileNotFoundException(fileName);
            }
            File sourceFile = sourceFiles.get(fileName);
            File sentFile = new File(sourceFile.getTitle(), sourceFile.getFullContent(), fromId, toId, false);

            sourceSatellite.checkFileUploadErrors(sentFile);
            
            // * Target is a Device ***************************************************************/
            if (this.ALL_DEVICES.containsKey(toId)) {
                Device targetDevice = this.ALL_DEVICES.get(toId);
                // Checking for if file already existing in target satellite
                if (targetDevice.getFiles().containsKey(sentFile.getTitle())) {
                    throw new FileTransferException.VirtualFileAlreadyExistsException(sentFile.getTitle());
                }

                sourceSatellite.getSendingFiles().put(sentFile.getTitle(), sentFile);
                targetDevice.addFile(sentFile);
                this.PENDING_FILES.put(sentFile.getTitle(), sentFile);

            // * Target is a Satellite ************************************************************/
            } else if (this.ALL_SATELLITES.containsKey(toId)) {
                FileHandlingSatellite targetSatellite = (FileHandlingSatellite)this.ALL_SATELLITES.get(toId);
                targetSatellite.checkFileDownloadErrors(sentFile);

                sourceSatellite.getSendingFiles().put(sentFile.getTitle(), sentFile);
                targetSatellite.addFile(sentFile);
                this.PENDING_FILES.put(sentFile.getTitle(), sentFile);
            }
        }
    }

    // DFS for relay satellites for finding communicableEntitiesInRange()
    private void dfsRelaySatellites(RelaySatellite rs, ArrayList<String> visible, HashMap<String, Satellite> allRelaySatellitesNotVisited) {
        double range = rs.getRange();
        // Save all reachable satellites. If you find another relay satellite, explore that one recursively.
        for (Satellite s : ALL_SATELLITES.values()) {
            if (s.equals(rs) == false && MathsHelper.isVisible(rs.getHeight(), rs.getPosition(), s.getHeight(), s.getPosition()) && MathsHelper.getDistance(rs.getHeight(), rs.getPosition(), s.getHeight(), s.getPosition()) <= range) {
                visible.add(s.getSatelliteId());
                if (s instanceof RelaySatellite && allRelaySatellitesNotVisited.containsKey(s.getSatelliteId())) {
                    allRelaySatellitesNotVisited.remove(s.getSatelliteId());
                    this.dfsRelaySatellites((RelaySatellite)s, visible, allRelaySatellitesNotVisited);
                }
            }  
        } 
        // Save all reachable devices
        for (Device d : ALL_DEVICES.values()) {
            if (MathsHelper.isVisible(rs.getHeight(), rs.getPosition(), d.getPosition()) && MathsHelper.getDistance(rs.getHeight(), rs.getPosition(), d.getPosition()) <= range) {
                visible.add(d.getDeviceId());
            }
        }
    }

    public List<String> communicableEntitiesInRange(String id) {
        // Task 2 b)

        // Creating a visited array for DFS when searching through relay satellites recursively
        ArrayList<String> communicatableEntitiesInRangArrayList = new ArrayList<String>();
        HashMap<String, Satellite> allRelaySatellitesNotVisited = new HashMap<String, Satellite>();
        for (Satellite s : this.ALL_SATELLITES.values()) {
            if (s instanceof RelaySatellite) {
                allRelaySatellitesNotVisited.put(s.getSatelliteId(), s);
            }
        }

        // * If the initial entity was a device
        if (this.ALL_DEVICES.containsKey(id)) {
            Device device = ALL_DEVICES.get(id);
            boolean isDesktopDevice = false;
            double range = device.getRange();
            if (device instanceof DesktopDevice) {
                isDesktopDevice = true;
            }

            // Finding all satellites reachable
            for (Satellite s : ALL_SATELLITES.values()) {
                if (MathsHelper.isVisible(s.getHeight(), s.getPosition(), device.getPosition()) && MathsHelper.getDistance(s.getHeight(), s.getPosition(), device.getPosition()) <= range) {
                    if (s instanceof StandardSatellite && !isDesktopDevice) {
                        communicatableEntitiesInRangArrayList.add(s.getSatelliteId());
                    } else if (s instanceof TeleportingSatellite) {
                        communicatableEntitiesInRangArrayList.add(s.getSatelliteId());

                    // If we find a relay satellite, use DFS to find all further reachable entities
                    } else if (s instanceof RelaySatellite && allRelaySatellitesNotVisited.containsKey(s.getSatelliteId())) {
                        communicatableEntitiesInRangArrayList.add(s.getSatelliteId());
                        ArrayList<String> visible = new ArrayList<String>();
                        visible.add(s.getSatelliteId());
                        allRelaySatellitesNotVisited.remove(s.getSatelliteId());
                        this.dfsRelaySatellites((RelaySatellite)s, visible, allRelaySatellitesNotVisited);
                        communicatableEntitiesInRangArrayList.addAll(visible);
                    }

                }
            }
            Set<String> set = new LinkedHashSet<String>();
            set.addAll(communicatableEntitiesInRangArrayList);
            communicatableEntitiesInRangArrayList.clear();
            communicatableEntitiesInRangArrayList.addAll(set);

            // Getting rid of all other devices
            ArrayList<String> reachableDevices = new ArrayList<String>();
            for (String reachableTarget : communicatableEntitiesInRangArrayList) {
                if (this.ALL_DEVICES.containsKey(reachableTarget)) {
                    reachableDevices.add(reachableTarget);
                }
            }
            communicatableEntitiesInRangArrayList.removeAll(reachableDevices);

            // Getting rid of all Standard Satellites if the source was a Desktop Device
            if (device instanceof DesktopDevice) {
                ArrayList<String> reachableStandardSatellites = new ArrayList<String>();
                for (String reachableTarget : communicatableEntitiesInRangArrayList) {
                    if (this.ALL_SATELLITES.containsKey(reachableTarget) && this.ALL_SATELLITES.get(reachableTarget) instanceof StandardSatellite) {
                        reachableStandardSatellites.add(reachableTarget);
                    }
                } 
                communicatableEntitiesInRangArrayList.removeAll(reachableStandardSatellites);
            }

            return communicatableEntitiesInRangArrayList;

        // * If the initial entity was a satellite
        } else if (this.ALL_SATELLITES.containsKey(id)) {
            Satellite satellite = ALL_SATELLITES.get(id);
            double range = satellite.getRange();

            // Find all reachable satellites
            for (Satellite s : ALL_SATELLITES.values()) {
                if (s.equals(satellite) == false && MathsHelper.isVisible(satellite.getHeight(), satellite.getPosition(), s.getHeight(), s.getPosition()) && MathsHelper.getDistance(satellite.getHeight(), satellite.getPosition(), s.getHeight(), s.getPosition()) <= range) {
                    communicatableEntitiesInRangArrayList.add(s.getSatelliteId());

                    // If we find a relay satellite, use DFS to find all further reachable entities
                    if (s instanceof RelaySatellite && allRelaySatellitesNotVisited.containsKey(s.getSatelliteId())) {
                        communicatableEntitiesInRangArrayList.add(s.getSatelliteId());
                        ArrayList<String> visible = new ArrayList<String>();
                        visible.add(s.getSatelliteId());
                        allRelaySatellitesNotVisited.remove(s.getSatelliteId());
                        this.dfsRelaySatellites((RelaySatellite)s, visible, allRelaySatellitesNotVisited);
                        communicatableEntitiesInRangArrayList.addAll(visible);
                    }
                }  
            } 
            
            // Find all reachable devices
            for (Device d : ALL_DEVICES.values()) {
                if (MathsHelper.isVisible(satellite.getHeight(), satellite.getPosition(), d.getPosition()) && MathsHelper.getDistance(satellite.getHeight(), satellite.getPosition(), d.getPosition()) <= range) {
                    if (satellite instanceof StandardSatellite && !(d instanceof DesktopDevice)) {
                        communicatableEntitiesInRangArrayList.add(d.getDeviceId());
                    } else if (!(satellite instanceof StandardSatellite)) {
                        communicatableEntitiesInRangArrayList.add(d.getDeviceId());
                    }
                }
            }

            Set<String> set = new LinkedHashSet<String>();
            set.addAll(communicatableEntitiesInRangArrayList);
            communicatableEntitiesInRangArrayList.clear();
            communicatableEntitiesInRangArrayList.addAll(set);

            // Getting rid of the source satellite from the reachable array if it was in it.
            if (communicatableEntitiesInRangArrayList.contains(satellite.getSatelliteId())) {
                communicatableEntitiesInRangArrayList.remove(satellite.getSatelliteId());
            }

            // Getting rid of all Desktop Devices if the source satellite was a Standard Satellite.
            if (satellite instanceof StandardSatellite) {
                ArrayList<String> reachableDesktopDevices = new ArrayList<String>();
                for (String reachableTarget : communicatableEntitiesInRangArrayList) {
                    if (this.ALL_DEVICES.containsKey(reachableTarget) && this.ALL_DEVICES.get(reachableTarget) instanceof DesktopDevice) {
                        reachableDesktopDevices.add(reachableTarget);
                    }
                }
                communicatableEntitiesInRangArrayList.removeAll(reachableDesktopDevices);
            }

            return communicatableEntitiesInRangArrayList;
        }
        return new ArrayList<String>();
    }

    public void createDevice(String deviceId, String type, Angle position, boolean isMoving) {
        createDevice(deviceId, type, position);
        // TODO: Task 3
    }

    public void createSlope(int startAngle, int endAngle, int gradient) {
        // TODO: Task 3
        // If you are not completing Task 3 you can leave this method blank :)
    }
  
    // * HELPER FUCNTIONS *********************************************************************/
    private int calculateByteBandwidth(Device source, Satellite target) {
        FileHandlingSatellite targetSatellite = (FileHandlingSatellite)target;
        return targetSatellite.calculateDownloadSatelliteBandwidth();
    }
    private int calculateByteBandwidth(Satellite source, Device target) {
        FileHandlingSatellite sourceSatellite = (FileHandlingSatellite)source;
        return sourceSatellite.calculateUploadSatelliteBandwidth();
    }
    private int calculateByteBandwidth(Satellite source, Satellite target) {
        FileHandlingSatellite targetSatellite = (FileHandlingSatellite)target;
        FileHandlingSatellite sourceSatellite = (FileHandlingSatellite)source;
        return Math.min(targetSatellite.calculateDownloadSatelliteBandwidth(), sourceSatellite.calculateUploadSatelliteBandwidth());
    }

    // Adjusting t bytes when teleport occurs mid upload/download.
    private void handleFileChangesForTeleport(TeleportingSatellite ts) {
        if (ts.getTeleported()) {
            // In this if statment means the satellite has teleported

            // * Teleporting satellite is sending
            // Making target instantly download all sending files.
            HashMap<String, File> sendingFiles = ts.getSendingFiles();
            for (File sendingFile : sendingFiles.values()) {
                String fullContent = sendingFile.getFullContent();
                String tBytesRemovedContent = fullContent.replace("t","");
                sendingFile.setDownloadedContent(tBytesRemovedContent);
                sendingFile.setFullContent(tBytesRemovedContent);
                sendingFile.setFullSize(tBytesRemovedContent.length());
                sendingFile.setComplete(true);
                this.PENDING_FILES.remove(sendingFile.getTitle());
            }
            ts.getSendingFiles().clear();

            // * Teleporting satellite is receiving/downloading
            // The following hashmaps are for if the device is the source to help remove all downloading files and adjusting them in the source.
            HashMap<String, File> TSFiles = ts.getFiles();
            HashMap<String, File> TSFilesCopy = new HashMap<String, File>();
            TSFilesCopy.putAll(ts.getFiles());

            for (File downloadingFile : TSFilesCopy.values()) {
                if (!downloadingFile.isComplete()) {
                    // * If the source is a satellite
                    if (this.ALL_SATELLITES.containsKey(downloadingFile.getSourceId())) {
                        // Instantly download the file except "t" bytes
                        String fullContent = downloadingFile.getFullContent();
                        String tBytesRemovedContent = fullContent.replace("t","");
                        downloadingFile.setDownloadedContent(tBytesRemovedContent);
                        downloadingFile.setFullContent(tBytesRemovedContent);
                        downloadingFile.setFullSize(tBytesRemovedContent.length());
                        downloadingFile.setComplete(true);
                        this.PENDING_FILES.remove(downloadingFile.getTitle());

                        // Remove file from source satellite sendingFiles hashmap
                        FileHandlingSatellite sourceSatellite = (FileHandlingSatellite)this.ALL_SATELLITES.get(downloadingFile.getSourceId());
                        sourceSatellite.getSendingFiles().remove(downloadingFile.getTitle());
                    
                    // * If the source is a device
                    } else if (this.ALL_DEVICES.containsKey(downloadingFile.getSourceId())) {
                        TSFiles.remove(downloadingFile.getTitle());
                        Device sourceDevice = this.ALL_DEVICES.get(downloadingFile.getSourceId());
                        File adjustedFile = sourceDevice.getFiles().get(downloadingFile.getTitle());

                        String fullContent = adjustedFile.getFullContent();
                        String tBytesRemovedContent = fullContent.replace("t","");
                        adjustedFile.setDownloadedContent(tBytesRemovedContent);
                        adjustedFile.setFullContent(tBytesRemovedContent);
                        adjustedFile.setFullSize(tBytesRemovedContent.length());
                    }
                }
            }
        }
    }
}
