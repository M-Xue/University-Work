package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransferException;
import unsw.response.models.FileInfoResponse;
import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.Arrays;

import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

@TestInstance(value = Lifecycle.PER_CLASS)
public class Task2ExampleTests {
    @Test
    public void testEntitiesInRange() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createSatellite("Satellite2", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(315));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));
        controller.createDevice("DeviceD", "HandheldDevice", Angle.fromDegrees(180));
        controller.createSatellite("Satellite3", "StandardSatellite", 2000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));

        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceB", "DeviceC", "Satellite2"), controller.communicableEntitiesInRange("Satellite1"));
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceB", "DeviceC", "Satellite1"), controller.communicableEntitiesInRange("Satellite2"));
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2", "Satellite1"), controller.communicableEntitiesInRange("DeviceB"));

        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceD"), controller.communicableEntitiesInRange("Satellite3"));
    }

    

    @Test
    public void testMovement() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(340));
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(340), 100 + RADIUS_OF_JUPITER, "StandardSatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(337.95), 100 + RADIUS_OF_JUPITER, "StandardSatellite"), controller.getInfo("Satellite1"));
    }

    

    @Test
    public void testRelayMovement() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "RelaySatellite", 100 + RADIUS_OF_JUPITER,
                                Angle.fromDegrees(180));

        // moves in negative direction
        assertEquals(
                        new EntityInfoResponse("Satellite1", Angle.fromDegrees(180), 100 + RADIUS_OF_JUPITER,
                                        "RelaySatellite"),
                        controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(178.77), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(177.54), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(176.31), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));

        controller.simulate(5);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(170.18), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate(24);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(140.72), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        // edge case
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(139.49), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        // coming back
        controller.simulate(1);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(140.72), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate(5);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(146.85), 100 + RADIUS_OF_JUPITER,
                        "RelaySatellite"), controller.getInfo("Satellite1"));
    }

    @Test
    public void testRelayTakesShortestPath() {
        BlackoutController controller = new BlackoutController();
        // Starts from extreme and starts anticlockwise.
        controller.createSatellite("Satellite1", "RelaySatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(345));

        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(345), 100 + RADIUS_OF_JUPITER, "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(346.23), 100 + RADIUS_OF_JUPITER, "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate(126);
        // Turning around
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(140.90), 100 + RADIUS_OF_JUPITER, "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate(40);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(190), 100 + RADIUS_OF_JUPITER, "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        // Turning around again
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(188.78), 100 + RADIUS_OF_JUPITER, "RelaySatellite"), controller.getInfo("Satellite1"));


        // Starts from extreme and starts clockwise.
        controller.createSatellite("Satellite2", "RelaySatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(344));

        assertEquals(new EntityInfoResponse("Satellite2", Angle.fromDegrees(344), 100 + RADIUS_OF_JUPITER, "RelaySatellite"), controller.getInfo("Satellite2"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite2", Angle.fromDegrees(342.77), 100 + RADIUS_OF_JUPITER, "RelaySatellite"), controller.getInfo("Satellite2"));
        controller.simulate(125);
        // Turning around
        assertEquals(new EntityInfoResponse("Satellite2", Angle.fromDegrees(189.33), 100 + RADIUS_OF_JUPITER, "RelaySatellite"), controller.getInfo("Satellite2"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite2", Angle.fromDegrees(188.1), 100 + RADIUS_OF_JUPITER, "RelaySatellite"), controller.getInfo("Satellite2"));
        controller.simulate(40);
        assertEquals(new EntityInfoResponse("Satellite2", Angle.fromDegrees(139), 100 + RADIUS_OF_JUPITER, "RelaySatellite"), controller.getInfo("Satellite2"));
        controller.simulate();
        // Turning around again
        assertEquals(new EntityInfoResponse("Satellite2", Angle.fromDegrees(140.22), 100 + RADIUS_OF_JUPITER, "RelaySatellite"), controller.getInfo("Satellite2"));
    }

    @Test
    public void testTeleportingMovement() {
        // Test for expected teleportation movement behaviour
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER,
                        Angle.fromDegrees(0));

        // Satellite position should increase if going anticlockwise (except from 360 -> 0)
        // Verify that Satellite1 is going in a anticlockwise direction (default)
        controller.simulate();
        Angle clockwiseOnFirstMovement = controller.getInfo("Satellite1").getPosition();
        controller.simulate();
        Angle clockwiseOnSecondMovement = controller.getInfo("Satellite1").getPosition();
        assertTrue(clockwiseOnSecondMovement.compareTo(clockwiseOnFirstMovement) == 1);

        // It should take 250 simulations to reach theta = 180.
        // Simulate until Satellite1 reaches theta=180
        controller.simulate(250);

        // Verify that Satellite1 is now at theta=0
        assertTrue(controller.getInfo("Satellite1").getPosition().toDegrees() % 360 == 0);
    }

    @Test
    public void testTeleportingMovementExtended() {
        // Test for expected teleportation movement behaviour
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(181));
        controller.createSatellite("Satellite2", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(179.5));
        controller.simulate();
        // Since Satellite 1 starts anti-clockwise but starts after 180, it should not teleport till it makes 359 degree rotation anti clockwise
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(181.72), 10000 + RADIUS_OF_JUPITER, "TeleportingSatellite"), controller.getInfo("Satellite1"));
        // Satellite 2 should hit 180 and should teleport
        assertEquals(new EntityInfoResponse("Satellite2", Angle.fromDegrees(0), 10000 + RADIUS_OF_JUPITER, "TeleportingSatellite"), controller.getInfo("Satellite2"));
    }

    @Test
    public void testSomeExceptionsForSend() {
        // just some of them... you'll have to test the rest
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 5000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

        String msg = "Hey";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        assertThrows(FileTransferException.VirtualFileNotFoundException.class, () -> controller.sendFile("NonExistentFile", "DeviceC", "Satellite1"));

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false), controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
        controller.simulate(msg.length() * 2);
        assertThrows(FileTransferException.VirtualFileAlreadyExistsException.class, () -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
    }

    @Test
    public void testFileExceptions() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("ss", "StandardSatellite", 5000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createSatellite("ts", "TeleportingSatellite", 5000 + RADIUS_OF_JUPITER, Angle.fromDegrees(300));
        controller.createDevice("d1", "LaptopDevice", Angle.fromDegrees(310));

        controller.addFileToDevice("d1", "f1", "1");
        controller.addFileToDevice("d1", "f2", "2");
        controller.addFileToDevice("d1", "f3", "3");
        controller.addFileToDevice("d1", "f4", "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"); // 100 bytes
        controller.addFileToDevice("d1", "f5", "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");

        assertDoesNotThrow(() -> controller.sendFile("f1", "d1", "ss"));
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class, () -> controller.sendFile("f2", "d1", "ss"));
        assertDoesNotThrow(() -> controller.sendFile("f4", "d1", "ts"));
        assertDoesNotThrow(() -> controller.sendFile("f5", "d1", "ts"));
        controller.simulate();
        assertDoesNotThrow(() -> controller.sendFile("f2", "d1", "ss"));
        controller.simulate();
        assertDoesNotThrow(() -> controller.sendFile("f3", "d1", "ss"));
        controller.simulate();
        assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class, () -> controller.sendFile("f4", "d1", "ss"));
        controller.simulate(12);
        assertEquals(new FileInfoResponse("f4", "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", 100, true), controller.getInfo("ts").getFiles().get("f4"));
        assertEquals(new FileInfoResponse("f5", "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", 100, true), controller.getInfo("ts").getFiles().get("f5"));
        assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class, () -> controller.sendFile("f1", "ss", "ts"));
    }

    @Test
    public void testExample() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

        String msg = "Hey";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false), controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        controller.simulate(msg.length() * 2);
        assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true), controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "Satellite1", "DeviceB"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false), controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

        controller.simulate(msg.length());
        assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true), controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

        // Hints for further testing:
        // - What about checking about the progress of the message half way through?
        // - Device/s get out of range of satellite
        // ... and so on.
    }

    @Test
    public void testBandwidthDownloads() {
        BlackoutController controller = new BlackoutController();
        controller.createDevice("d", "HandheldDevice", Angle.fromDegrees(270));
        controller.createSatellite("1", "TeleportingSatellite", 6000 + RADIUS_OF_JUPITER, Angle.fromDegrees(260));
        controller.createSatellite("2", "TeleportingSatellite", 6000 + RADIUS_OF_JUPITER, Angle.fromDegrees(265));
        controller.createSatellite("3", "TeleportingSatellite", 6000 + RADIUS_OF_JUPITER, Angle.fromDegrees(270));
        controller.createSatellite("4", "TeleportingSatellite", 6000 + RADIUS_OF_JUPITER, Angle.fromDegrees(275));
        controller.createSatellite("5", "TeleportingSatellite", 6000 + RADIUS_OF_JUPITER, Angle.fromDegrees(280));
        controller.createSatellite("ss", "StandardSatellite", 6010 + RADIUS_OF_JUPITER, Angle.fromDegrees(255));
        
        String msg = "1234567890123456789012345678901234567890"; // 40 bytes
        controller.addFileToDevice("d", "f1", msg);
        controller.addFileToDevice("d", "f2", msg);
        controller.addFileToDevice("d", "f3", msg);
        controller.addFileToDevice("d", "f4", "12");
        controller.addFileToDevice("d", "f5", "12");
        assertEquals(new FileInfoResponse("f1", "1234567890123456789012345678901234567890", msg.length(), true), controller.getInfo("d").getFiles().get("f1"));
        assertEquals(new FileInfoResponse("f2", "1234567890123456789012345678901234567890", msg.length(), true), controller.getInfo("d").getFiles().get("f2"));
        assertEquals(new FileInfoResponse("f3", "1234567890123456789012345678901234567890", msg.length(), true), controller.getInfo("d").getFiles().get("f3"));
        assertEquals(new FileInfoResponse("f4", "12", 2, true), controller.getInfo("d").getFiles().get("f4"));
        assertEquals(new FileInfoResponse("f5", "12", 2, true), controller.getInfo("d").getFiles().get("f5"));

        assertDoesNotThrow(() -> controller.sendFile("f1", "d", "1"));
        assertEquals(new FileInfoResponse("f1", "", msg.length(), false), controller.getInfo("1").getFiles().get("f1"));
        controller.simulate();


        assertEquals(new FileInfoResponse("f1", "123456789012345", msg.length(), false), controller.getInfo("1").getFiles().get("f1")); 
        assertDoesNotThrow(() -> controller.sendFile("f2", "d", "1"));
        assertEquals(new FileInfoResponse("f2", "", msg.length(), false), controller.getInfo("1").getFiles().get("f2"));
        controller.simulate();
        assertEquals(new FileInfoResponse("f1", "1234567890123456789012", msg.length(), false), controller.getInfo("1").getFiles().get("f1"));
        assertEquals(new FileInfoResponse("f2", "1234567", msg.length(), false), controller.getInfo("1").getFiles().get("f2"));
        assertDoesNotThrow(() -> controller.sendFile("f3", "d", "1"));
        controller.simulate();
        assertEquals(new FileInfoResponse("f1", "123456789012345678901234567", msg.length(), false), controller.getInfo("1").getFiles().get("f1"));
        assertEquals(new FileInfoResponse("f2", "123456789012", msg.length(), false), controller.getInfo("1").getFiles().get("f2"));
        assertEquals(new FileInfoResponse("f3", "12345", msg.length(), false), controller.getInfo("1").getFiles().get("f3"));
        controller.simulate();
        assertEquals(new FileInfoResponse("f1", "12345678901234567890123456789012", msg.length(), false), controller.getInfo("1").getFiles().get("f1"));
        assertEquals(new FileInfoResponse("f2", "12345678901234567", msg.length(), false), controller.getInfo("1").getFiles().get("f2"));
        assertEquals(new FileInfoResponse("f3", "1234567890", msg.length(), false), controller.getInfo("1").getFiles().get("f3"));
        controller.simulate(2);
        assertEquals(new FileInfoResponse("f1", "1234567890123456789012345678901234567890", msg.length(), true), controller.getInfo("1").getFiles().get("f1"));
        assertEquals(new FileInfoResponse("f2", "123456789012345678901234567", msg.length(), false), controller.getInfo("1").getFiles().get("f2"));
        assertEquals(new FileInfoResponse("f3", "12345678901234567890", msg.length(), false), controller.getInfo("1").getFiles().get("f3"));
        controller.simulate();
        assertEquals(new FileInfoResponse("f2", "1234567890123456789012345678901234", msg.length(), false), controller.getInfo("1").getFiles().get("f2"));
        assertEquals(new FileInfoResponse("f3", "123456789012345678901234567", msg.length(), false), controller.getInfo("1").getFiles().get("f3"));
        controller.simulate();
        assertEquals(new FileInfoResponse("f2", "1234567890123456789012345678901234567890", msg.length(), true), controller.getInfo("1").getFiles().get("f2"));
        assertEquals(new FileInfoResponse("f3", "1234567890123456789012345678901234", msg.length(), false), controller.getInfo("1").getFiles().get("f3"));
        controller.simulate();
        assertEquals(new FileInfoResponse("f3", "1234567890123456789012345678901234567890", msg.length(), true), controller.getInfo("1").getFiles().get("f3"));


        assertDoesNotThrow(() -> controller.sendFile("f4", "d", "1"));
        assertDoesNotThrow(() -> controller.sendFile("f5", "d", "1"));
        controller.simulate();
        assertEquals(new FileInfoResponse("f4", "12", 2, true), controller.getInfo("1").getFiles().get("f4"));
        assertEquals(new FileInfoResponse("f5", "12", 2, true), controller.getInfo("1").getFiles().get("f5"));

        assertDoesNotThrow(() -> controller.sendFile("f1", "1", "5"));
        controller.simulate();
        assertEquals(new FileInfoResponse("f1", "1234567890", msg.length(), false), controller.getInfo("5").getFiles().get("f1")); // Should only be 10 bytes sent because of Teleporting satellites bandwidth limit
        assertDoesNotThrow(() -> controller.sendFile("f4", "1", "4"));

        controller.simulate();
        assertEquals(new FileInfoResponse("f1", "123456789012345", msg.length(), false), controller.getInfo("5").getFiles().get("f1"));
        assertEquals(new FileInfoResponse("f4", "12", 2, true), controller.getInfo("4").getFiles().get("f4"));

        controller.simulate();
        assertEquals(new FileInfoResponse("f1", "1234567890123456789012345", msg.length(), false), controller.getInfo("5").getFiles().get("f1"));

        assertDoesNotThrow(() -> controller.sendFile("f4", "1", "ss"));
        controller.simulate();
        assertEquals(new FileInfoResponse("f1", "123456789012345678901234567890", msg.length(), false), controller.getInfo("5").getFiles().get("f1"));
        assertEquals(new FileInfoResponse("f4", "1", 2, false), controller.getInfo("ss").getFiles().get("f4"));
        assertDoesNotThrow(() -> controller.sendFile("f3", "1", "3"));
        controller.simulate();
        assertEquals(new FileInfoResponse("f1", "123456789012345678901234567890123", msg.length(), false), controller.getInfo("5").getFiles().get("f1"));
        assertEquals(new FileInfoResponse("f4", "12", 2, true), controller.getInfo("ss").getFiles().get("f4"));
        assertEquals(new FileInfoResponse("f3", "123", msg.length(), false), controller.getInfo("3").getFiles().get("f3"));
        controller.simulate();
        assertEquals(new FileInfoResponse("f1", "12345678901234567890123456789012345678", msg.length(), false), controller.getInfo("5").getFiles().get("f1"));
        assertEquals(new FileInfoResponse("f3", "12345678", msg.length(), false), controller.getInfo("3").getFiles().get("f3"));
        controller.simulate();
        assertEquals(new FileInfoResponse("f1", "1234567890123456789012345678901234567890", msg.length(), true), controller.getInfo("5").getFiles().get("f1"));
        assertEquals(new FileInfoResponse("f3", "1234567890123", msg.length(), false), controller.getInfo("3").getFiles().get("f3"));
        controller.simulate();
        assertEquals(new FileInfoResponse("f3", "12345678901234567890123", msg.length(), false), controller.getInfo("3").getFiles().get("f3"));
        controller.simulate(2);
        assertEquals(new FileInfoResponse("f3", "1234567890123456789012345678901234567890", msg.length(), true), controller.getInfo("3").getFiles().get("f3"));




    }

    @Test
    public void testRelayDFSCommunicatableEntitiesInRange() {

        BlackoutController controller = new BlackoutController();

        controller.createSatellite("1", "RelaySatellite", 100260, Angle.fromDegrees(200));
        controller.createSatellite("2", "RelaySatellite", 96707, Angle.fromDegrees(265));
        controller.createSatellite("3", "RelaySatellite", 93056, Angle.fromDegrees(340));

        controller.createSatellite("s", "StandardSatellite", 84878, Angle.fromDegrees(49));
        controller.createSatellite("t", "TeleportingSatellite", 87007, Angle.fromDegrees(126));

        controller.createDevice("h", "HandheldDevice", Angle.fromDegrees(267));
        controller.createDevice("d", "DesktopDevice", Angle.fromDegrees(253));
       
        assertListAreEqualIgnoringOrder(Arrays.asList( "2", "3", "s", "t", "h", "d"), controller.communicableEntitiesInRange("1"));
        assertListAreEqualIgnoringOrder(Arrays.asList( "1", "3", "s", "t", "h", "d"), controller.communicableEntitiesInRange("2"));
        assertListAreEqualIgnoringOrder(Arrays.asList( "2", "1", "s", "t", "h", "d"), controller.communicableEntitiesInRange("3"));
        assertListAreEqualIgnoringOrder(Arrays.asList( "1", "2", "3", "t", "h"), controller.communicableEntitiesInRange("s"));
        assertListAreEqualIgnoringOrder(Arrays.asList( "1", "2", "3", "s", "h", "d"), controller.communicableEntitiesInRange("t"));
        assertListAreEqualIgnoringOrder(Arrays.asList( "1", "2", "3", "t", "s"), controller.communicableEntitiesInRange("h"));
        assertListAreEqualIgnoringOrder(Arrays.asList( "1", "2", "3", "t"), controller.communicableEntitiesInRange("d"));
    }

    @Test
    public void testTBytesRemovedOnTeleport() {
        BlackoutController controller = new BlackoutController();

        // Checking t bytes are deleted from source device
        controller.createDevice("d", "HandheldDevice", Angle.fromDegrees(180));
        controller.createSatellite("ts", "TeleportingSatellite", 76715, Angle.fromDegrees(179.5));

        controller.addFileToDevice("d", "f1", "tttttzzzzz");
        controller.addFileToDevice("d", "f2", "ttttt");
        controller.addFileToDevice("d", "f3", "Hello World");
        controller.addFileToDevice("d", "f4", "content");
        assertDoesNotThrow(() -> controller.sendFile("f1", "d", "ts"));
        assertDoesNotThrow(() -> controller.sendFile("f2", "d", "ts"));
        assertDoesNotThrow(() -> controller.sendFile("f3", "d", "ts"));
        assertDoesNotThrow(() -> controller.sendFile("f4", "d", "ts"));
        assertEquals(new FileInfoResponse("f1", "tttttzzzzz", 10, true), controller.getInfo("d").getFiles().get("f1"));
        assertEquals(new FileInfoResponse("f2", "ttttt", 5, true), controller.getInfo("d").getFiles().get("f2"));
        assertEquals(new FileInfoResponse("f3", "Hello World", 11, true), controller.getInfo("d").getFiles().get("f3"));
        assertEquals(new FileInfoResponse("f4", "content", 7, true), controller.getInfo("d").getFiles().get("f4"));

        assertEquals(new FileInfoResponse("f1", "", 10, false), controller.getInfo("ts").getFiles().get("f1"));
        assertEquals(new FileInfoResponse("f2", "", 5, false), controller.getInfo("ts").getFiles().get("f2"));
        assertEquals(new FileInfoResponse("f3", "", 11, false), controller.getInfo("ts").getFiles().get("f3"));
        assertEquals(new FileInfoResponse("f4", "", 7, false), controller.getInfo("ts").getFiles().get("f4"));

        controller.simulate();
        // Confirming satellite has teleported
        assertEquals(new EntityInfoResponse("ts", Angle.fromDegrees(0), 76715, "TeleportingSatellite"), controller.getInfo("ts"));
        assertEquals(new FileInfoResponse("f1", "zzzzz", 5, true), controller.getInfo("d").getFiles().get("f1"));
        assertEquals(new FileInfoResponse("f2", "", 0, true), controller.getInfo("d").getFiles().get("f2"));
        assertEquals(new FileInfoResponse("f3", "Hello World", 11, true), controller.getInfo("d").getFiles().get("f3"));
        assertEquals(new FileInfoResponse("f4", "conen", 5, true), controller.getInfo("d").getFiles().get("f4"));

        assertEquals(null, controller.getInfo("ts").getFiles().get("f1"));
        assertEquals(null, controller.getInfo("ts").getFiles().get("f2"));
        assertEquals(null, controller.getInfo("ts").getFiles().get("f3"));
        assertEquals(null, controller.getInfo("ts").getFiles().get("f4"));
    }
}


