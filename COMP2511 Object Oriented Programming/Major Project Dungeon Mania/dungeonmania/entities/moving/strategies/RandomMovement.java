package dungeonmania.entities.moving.strategies;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.entities.Entity;
import dungeonmania.util.Position;

import java.util.List;
import java.util.Random;

public class RandomMovement implements MovementStrategy {
    @Override
    public void move(Entity movingEntity) {
        Dungeon dungeon = movingEntity.getDungeon();
        List<Position> availablePositions = dungeon.getCardinallyAdjacentOpenPositions(movingEntity.getPosition());

        if (!availablePositions.isEmpty()) {
            Random randomizer = movingEntity.getDungeon().getRandomizer();
            Position randomPosition = availablePositions.get(randomizer.nextInt(availablePositions.size()));
            dungeon.moveEntity(randomPosition, movingEntity);
        }
    }
}
