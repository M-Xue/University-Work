package dungeonmania.entities.moving.strategies;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.dungeon.Grid;
import dungeonmania.entities.Entity;
import dungeonmania.util.PathFinding;
import dungeonmania.util.Position;

public class FollowMovement implements MovementStrategy{
    @Override
    public void move(Entity movingEntity) {
        Dungeon dungeon = movingEntity.getDungeon();
        Grid grid = dungeon.generateGrid(movingEntity);

        Position dest = dungeon.getPlayer().getLastPosition();
        if (dest == null) {
            dest = dungeon.getPlayer().getPosition();
        }

        Position next = PathFinding.getNextMove(
                grid,
                dungeon.getPortals(),
                movingEntity.getPosition(),
                dest
        );
        dungeon.moveEntity(next, movingEntity);
    }
}
