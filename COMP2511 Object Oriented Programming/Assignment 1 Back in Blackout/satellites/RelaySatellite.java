package unsw.satellites;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;


public class RelaySatellite extends Satellite {
    private static final int LINEAR_VELOCITY = 1500;
    private static final int RANGE = 300000;

    private int currDirection = MathsHelper.CLOCKWISE;

    public RelaySatellite(String satelliteId, double height, Angle position) {
        super(satelliteId, height, position);
    }

    // Movement
    public double getAngularVelocity() {
        return LINEAR_VELOCITY / this.getHeight();
    }
    public void move() {

        Angle initialPosition = this.getPosition();
        Angle positionChange = Angle.fromRadians(this.getAngularVelocity());

        if (initialPosition.toDegrees() > 190 && initialPosition.toDegrees() < 345) {
            this.currDirection = MathsHelper.CLOCKWISE;
        } else if ((initialPosition.toDegrees() < 140 && initialPosition.toDegrees() > 0) || (initialPosition.toDegrees() < 360 && initialPosition.toDegrees() >= 345)) {
            this.currDirection = MathsHelper.ANTI_CLOCKWISE;
        }

        if (this.currDirection == MathsHelper.ANTI_CLOCKWISE) {
            Angle newPosition = Angle.fromDegrees(Satellite.normaliseDegrees(initialPosition.add(positionChange).toDegrees()));
            this.setPosition(newPosition);
        } else if (this.currDirection == MathsHelper.CLOCKWISE) {
            Angle newPosition = Angle.fromDegrees(Satellite.normaliseDegrees(initialPosition.subtract(positionChange).toDegrees()));
            this.setPosition(newPosition);
        }
    }

    public int getRange() {
        return RANGE;
    }
}
