package dungeonmania.entities;

import dungeonmania.entities.behaviours.Incrementable;
import dungeonmania.entities.behaviours.Triggerable;
import dungeonmania.entities.moving.MovingEntity;
import dungeonmania.util.Position;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

public class SwampTile extends Entity implements Triggerable, Incrementable {
    private final int movementFactor;
    private HashMap<MovingEntity, Integer> affectedEntities;

    public SwampTile(int id, Position position, JSONObject entityJSON) {
        super(id, "swamp_tile");
        setPosition(position);
        this.movementFactor = entityJSON.getInt("movement_factor");
        this.affectedEntities = new HashMap<>();
    }

    public SwampTile(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")), entityJSON);
    }

    public int getMovementFactor() {
        return this.movementFactor;
    }

    @Override
    public void onTrigger(Entity other) {
        if (other instanceof MovingEntity) {
            getStuck((MovingEntity) other);
        }
    }

    private void getStuck(MovingEntity other) {
        affectedEntities.put(other, movementFactor);
        other.setStuck(true);
    }

    private void getUnstuck(MovingEntity other) {
        affectedEntities.remove(other);
        other.setStuck(false);
    }

    @Override
    public void increment() { 
        Set<Entry<MovingEntity, Integer>> copyAffectedEntities = new HashSet<>();
        for (Entry<MovingEntity, Integer> e : affectedEntities.entrySet()) {
            copyAffectedEntities.add(e);
        }
        for (Entry<MovingEntity, Integer> e : copyAffectedEntities) {
            int ticksToGo = e.getValue();
            if (ticksToGo > 0) {
                e.setValue(ticksToGo - 1);
            } else {
                getUnstuck(e.getKey());
            }
        }
    }
}
