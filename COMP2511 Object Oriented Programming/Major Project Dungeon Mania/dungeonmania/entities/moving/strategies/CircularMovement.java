package dungeonmania.entities.moving.strategies;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.entities.Boulder;
import dungeonmania.entities.Entity;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import java.util.List;

public class CircularMovement implements MovementStrategy {
    private Position initPosition;
    private List<Position> movementPath;
    private boolean isReverse = false;

    @Override
    public void move(Entity movingEntity) {
        Dungeon dungeon = movingEntity.getDungeon();
        Position currPosition = movingEntity.getPosition();

        if (initPosition == null) {
            initPosition = currPosition;
            movementPath = List.of(
                    initPosition.translateBy(0, -1),
                    initPosition.translateBy(1, -1),
                    initPosition.translateBy(1, 0),
                    initPosition.translateBy(1, 1),
                    initPosition.translateBy(0, 1),
                    initPosition.translateBy(-1, 1),
                    initPosition.translateBy(-1, 0),
                    initPosition.translateBy(-1, -1)
            );
        }

        if (currPosition.equals(initPosition)) {
            Position topPosition = currPosition.translateBy(Direction.UP);
            if (!dungeon.hasEntityOnTile(topPosition, Boulder.class)) {
                dungeon.moveEntity(topPosition, movingEntity);
            }
        } else {
            Position nextPosition = getNextPosition(currPosition);

            if (dungeon.hasEntityOnTile(nextPosition, Boulder.class)) {
                isReverse = !isReverse;
                nextPosition = getNextPosition(currPosition);
            }

            if (!dungeon.hasEntityOnTile(nextPosition, Boulder.class)) {
                dungeon.moveEntity(nextPosition, movingEntity);
            }
        }
    }

    private Position getNextPosition(Position currPosition) {
        int currCycle = movementPath.indexOf(currPosition);

        if (isReverse) {
            if (currCycle == 0) {
                return movementPath.get(movementPath.size() - 1);
            } else {
                return movementPath.get(currCycle - 1);
            }
        } else {
            if (currCycle == movementPath.size() - 1) {
                return movementPath.get(0);
            } else {
                return movementPath.get(currCycle + 1);
            }
        }
    }
}
