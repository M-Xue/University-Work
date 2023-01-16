package dungeonmania.entities.moving.strategies;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.dungeon.Grid;
import dungeonmania.entities.Entity;
import dungeonmania.util.PathFinding;
import dungeonmania.util.Position;

public class ShortestPathMovement implements MovementStrategy {
    @Override
    public void move(Entity movingEntity) {
        Dungeon dungeon = movingEntity.getDungeon();
        Grid grid = dungeon.generateGrid(movingEntity);
        Position next = PathFinding.getNextMove(
                grid,
                dungeon.getPortals(),
                movingEntity.getPosition(),
                dungeon.getPlayer().getPosition()
        );
        dungeon.moveEntity(next, movingEntity);
    }
}
