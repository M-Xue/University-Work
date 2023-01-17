package dungeonmania.entities;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.entities.actives.Activatable;
import dungeonmania.entities.actives.Activator;
import dungeonmania.entities.behaviours.Triggerable;
import dungeonmania.entities.collectable.Bomb;
import dungeonmania.util.Position;

import dungeonmania.util.PositionExt;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Note: There should be no way to deactivate a switch in the sense that
 * its `isActivated` attribute goes from true to false. This accounts for
 * the strange reference implementation behaviour where a "deativated"
 * switch can detonate a bomb.
 */

public class Switch extends Activator implements Triggerable {
    public Switch(int id, Position position, String condition) {
        super(id, "switch", condition);
        setPosition(position);
    }

    public Switch(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")), entityJSON.optString("logic"));
    } 

    @Override
    public void onTrigger(Entity other) {
        if (other instanceof Boulder) {
            spreadActivatorActivation();
            spreadActivatableActivation();
        } else {
            spreadDeactivation(this);
            spreadActivatorActivation();
            spreadActivatableActivation();
        }
    }

    /**
     * Used for checking boulder goal dynamically, which can be
     * "un-achieved if boulder goes off this switch."
     */
    public boolean isCovered() {
        return getDungeon().hasEntityOnTile(getPosition(), Boulder.class);
    }

    /**
     * Necessary for bombs placed down next to this switch to detonate.
     * @return
     */
    public boolean isActivated() {
        return super.isActive();
    }
}
