package unsw.satellites;

import unsw.utils.Angle;

public abstract class Satellite {

    private String satelliteId;
    private double height;
    private Angle position;
    
    // Constructor
    public Satellite(String satelliteId, double height, Angle position) {
        this.satelliteId = satelliteId;
        this.height = height;
        this.position = position;
    }

    // Getters and setters
    public Angle getPosition() {
        return this.position;
    }
    public void setPosition(Angle position) {
        this.position = position;
    }
    // Only getters. These attributes cannot be altered manually.
    public String getSatelliteId() {
        return this.satelliteId;
    }
    public double getHeight() {
        return this.height;
    }

    // Abstract methods
    public abstract void move();
    public abstract int getRange();

    // Helper function
    public static double normaliseDegrees(double angle) {
        if (angle == 360) {
            angle = 0;
        }
        
        if (angle > 0) {
            return angle % 360;
        } 

        angle *= -1;
        angle %= 360;
        return 360 - angle;
    }
}
