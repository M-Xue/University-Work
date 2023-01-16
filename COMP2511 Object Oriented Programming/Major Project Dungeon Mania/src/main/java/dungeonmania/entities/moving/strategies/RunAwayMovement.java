package dungeonmania.entities.moving.strategies;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.entities.Entity;
import dungeonmania.entities.player.Player;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class RunAwayMovement implements MovementStrategy{
    @Override
    public void move(Entity movingEntity) {
        Dungeon dungeon = movingEntity.getDungeon();
        Player player = dungeon.getPlayer();

        Position playerPos = player.getPosition();
        Position entityPos = movingEntity.getPosition();

        if (playerPos.getX() < entityPos.getX() && dungeon.isOpenTile(entityPos.translateBy(Direction.RIGHT))) {
            dungeon.moveEntity(Direction.RIGHT, movingEntity);
        } else if (playerPos.getX() > entityPos.getX() && dungeon.isOpenTile(entityPos.translateBy(Direction.LEFT))) {
            dungeon.moveEntity(Direction.LEFT, movingEntity);
        } else if (playerPos.getY() < entityPos.getY() && dungeon.isOpenTile(entityPos.translateBy(Direction.DOWN))) {
            dungeon.moveEntity(Direction.DOWN, movingEntity);
        } else if (playerPos.getY() > entityPos.getY() && dungeon.isOpenTile(entityPos.translateBy(Direction.UP))) {
            dungeon.moveEntity(Direction.UP, movingEntity);
        }
    }
}
