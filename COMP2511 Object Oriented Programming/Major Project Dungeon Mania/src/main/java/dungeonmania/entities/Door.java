package dungeonmania.entities;

import dungeonmania.entities.behaviours.PreTriggerable;
import dungeonmania.entities.collectable.Key;
import dungeonmania.entities.collectable.SunStone;
import dungeonmania.entities.player.Player;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.json.JSONObject;

import java.util.List;

public class Door extends Entity implements PreTriggerable {
    private final int keyId;
    private boolean isLocked;

    public Door(int id, Position position, int keyId) {
        super(id, "door");
        setPosition(position);
        this.isLocked = true;
        this.keyId = keyId;
    }

    public Door(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(
                id,
                new Position(entityJSON.getInt("x"), entityJSON.getInt("y")),
                entityJSON.getInt("key")
        );
    }

    public boolean isLocked() {
        return isLocked;
    }

    public Integer getKeyId() {
        return keyId;
    }

    @Override
    public String getType() {
        if (isLocked) {
            return "door";
        } else {
            return "door_open";
        }
    }

    /**
     * Attempt to unlock this door with given key.
     * Returns true if unlock was successful, false otherwise.
     * @param key
     * @return
     */
    private boolean tryUnlock(Key key) {
        if (key.getKeyId().equals(getKeyId())) {
            this.isLocked = false;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isBlocking(){
        return isLocked;
    }

    @Override
    public void preTrigger(Player player) {
        if (!isLocked) return;

        if (player.hasItem(SunStone.class)) {
            this.isLocked = false;
        } else {
            List<Key> keys = player.getItemsByClass(Key.class);
            for (Key key : keys) {
                if (tryUnlock(key)) {
                    player.removeItem(key);
                }
            }
        }
    }
}
