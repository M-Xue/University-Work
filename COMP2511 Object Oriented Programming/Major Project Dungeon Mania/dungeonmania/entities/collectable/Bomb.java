package dungeonmania.entities.collectable;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.entities.behaviours.Usable;
import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.util.PositionExt;
import org.json.JSONObject;

import dungeonmania.entities.Entity;
import dungeonmania.entities.player.Player;
import dungeonmania.entities.Switch;
import dungeonmania.entities.actives.Activatable;
import dungeonmania.entities.actives.Activator;

import static java.util.stream.Collectors.toList;


/**
 * Detonates when placed down and cardinally adjacent to an active switch.
 * This is implemented using a switch observer pattern to observe when a
 * switch gets activated and also logically when the bomb is placed down
 * onto map.
 */
public class Bomb extends CollectableEntity implements Usable {
    private final int blastRadius;
    private boolean placed = false;

    private String condition = "";

    public Bomb(int id, Position position, JSONObject configJSON, String condition) {
        super(id, "bomb");
        setPosition(position);
        this.blastRadius = configJSON.getInt("bomb_radius");
        if (!condition.equals("")) {
            this.condition = condition;
        } else {
            this.condition = "or";
        }
    }

    public Bomb(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")), configJSON, entityJSON.optString("logic"));
    }

    public Boolean isPlaced() {
        return placed;
    }

    /**
     * Destroy all entities within the blastRadius of the bomb except for
     * player and itself.
     * Expects the bomb to be placed down and not previously detonated.
     */
    public void detonate() {
        if (!isPlaced()) {return;}
        Dungeon dungeon = getDungeon();

        List<Entity> entitiesToRemove = dungeon.getEntities().stream()
                .filter(entity -> withinBlastRadius(entity.getPosition()))
                .filter(entity -> !(entity instanceof Player))
                .collect(Collectors.toList());
        
        entitiesToRemove.stream()
            .filter(e -> e instanceof Activator)
            .map(e -> (Activator)e)
            .forEach(a -> a.spreadDeactivation(a));
        
        entitiesToRemove.forEach(dungeon::removeEntity);

        List<Switch> activeSwitches = dungeon.getEntities().stream()
            .filter(e -> e instanceof Switch)
            .map(e -> (Switch)e)
            .filter(s -> s.isActive())
            .collect(toList());

        if (activeSwitches.size() > 0) {
            activeSwitches.get(0).spreadActivatorActivation();
            activeSwitches.get(0).spreadActivatableActivation();
        }
    }

    public boolean withinBlastRadius(Position entityPos) {
        Position offset = Position.calculatePositionBetween(entityPos, getPosition());
        return offset.getX() <= blastRadius && offset.getX() >= -blastRadius && offset.getY() <= blastRadius
                && offset.getY() >= -blastRadius;
    }

    public boolean isBlocking() {
        return isPlaced();
    }

    /**
     * A bomb can only be placed once. A player places it down on
     * the map after picking it up.
     */
    @Override
    public void onUse(Player player) {
        Dungeon dungeon = getDungeon();
        Position position = player.getPosition();

        setPosition(position);
        dungeon.addEntity(this);

        // Must be placed before detonated.
        placed = true;

        activate();
    }

    // Puts the item in the players inventory the first time the player steps on top of the bomb. After it is placed, the player cannot pick it up again.
    @Override
    public void onTrigger(Entity other) {
        if (!isPlaced()) {
            super.onTrigger(other);
        }
    }
    
    // This method gets all entities that can activate other entities that are cardianlly adjacent to the current activatable (this) into an array list
    public ArrayList<Activator> getCardinalActivators() {
        ArrayList<Activator> activators = new ArrayList<Activator>();
        Dungeon dungeon = getDungeon();
        List<Position> cardinallyAdjacentPositions = PositionExt.getCardinallyAdjacentPositions(getPosition());
        for (Position pos : cardinallyAdjacentPositions) {
            List<Entity> entitiesOnTile =  dungeon.getEntitiesOnTile(pos);
            for (Entity entity : entitiesOnTile) {
                if (entity instanceof Activator) {
                    Activator activator = (Activator)entity;
                    activators.add(activator);
                }
            }
        }
        return activators;
    }

    // Checks if its conditions are met for surrounding Activators. If conditions are met, detonate the bomb.
    public void activate() {
        ArrayList<Activator> activators = getCardinalActivators();
        if (condition.equals("or")) {
            if (activators.stream().filter(a -> a.isActive() == true).count() >= 1) {
                detonate();
            }
        } else if (condition.equals("and")) {
            if (activators.stream().filter(a -> a.isActive() == true).count() >= 2 && activators.stream().allMatch(a -> a.isActive() == true)) {
                detonate();
            }
        } else if (condition.equals("xor")) {
            if (activators.stream().filter(a -> a.isActive() == true).count() == 1) {
                detonate();
            }
        } else if (condition.equals("co_and")) {
            List<Activator> activeActivators = activators.stream().filter(a -> a.isActive() == true).collect(toList());
            for (int i = 0; i < activeActivators.size(); i++) {
                for (int j = i + 1; j < activeActivators.size(); j++) {
                    Activatable parent1 = (Activatable)activeActivators.get(i);
                    Activatable parent2 = (Activatable)activeActivators.get(j);
                    if (parent1.getLastActivatedTick() == parent2.getLastActivatedTick()) {
                        detonate();
                    }
                }
            }
        }
    }

}
