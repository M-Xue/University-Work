package dungeonmania.util;

import java.util.List;

public class PositionExt {
    public static List<Position> getCardinallyAdjacentPositions(Position position) {
        return List.of(
                position.translateBy(Direction.UP),
                position.translateBy(Direction.DOWN),
                position.translateBy(Direction.LEFT),
                position.translateBy(Direction.RIGHT)
        );
    }
}
