package dungeonmania.entities;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.entities.behaviours.Incrementable;
import dungeonmania.entities.behaviours.Interactable;
import dungeonmania.entities.behaviours.Weapon;
import dungeonmania.entities.moving.ZombieToast;
import dungeonmania.entities.player.Player;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.util.Position;

import java.util.List;
import java.util.Random;

import dungeonmania.util.PositionExt;
import org.json.JSONObject;

public class ZombieToastSpawner extends Entity implements Interactable, Incrementable {
    private int spawnCounter = 0;
    private final int spawnRate;

    public ZombieToastSpawner(int id, Position position, JSONObject configJSON) {
        super(id, "zombie_toast_spawner");
        setPosition(position);
        this.spawnRate = configJSON.getInt("zombie_spawn_rate");
    }

    public ZombieToastSpawner(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")), configJSON);
    }

    @Override
    public void increment() {
        this.spawnCounter++;
        
        if (this.spawnCounter >= this.spawnRate) {
            Dungeon dungeon = getDungeon();
            List<Position> availablePositions = dungeon.getCardinallyAdjacentOpenPositions(this.getPosition());

            if (!availablePositions.isEmpty()) {
                Random randomizer = getDungeon().getRandomizer();
                Position randomPosition = availablePositions.get(randomizer.nextInt(availablePositions.size()));
                getDungeon().addEntity(new ZombieToast(dungeon.generateEntityId(), randomPosition, dungeon.getConfigJSON()));
            }

            this.spawnCounter = 0;
        }
    }

    @Override
    public void onInteract(Player player) throws InvalidActionException {
        boolean hasWeapon = player.hasItem(Weapon.class);

        if (!hasWeapon) {
            throw new InvalidActionException("Player does not have a weapon to destroy zombie toast spawner");
        }

        List<Position> cardinallyAdjacentPositions = PositionExt.getCardinallyAdjacentPositions(player.getPosition());
        if (cardinallyAdjacentPositions.contains(getPosition())) {
            getDungeon().removeEntity(this);
        } else {
            throw new InvalidActionException(String.format("Player is not adjacent to zombie toast spawner %s", this.getIdString()));
        }
    }

    @Override
    public boolean isInteractable() {
        return true;
    }
}
