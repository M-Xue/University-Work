package dungeonmania.entities.collectable;

import dungeonmania.entities.Entity;
import dungeonmania.entities.player.Player;
import dungeonmania.util.Position;
import org.json.JSONObject;

public class Key extends CollectableEntity {
    private final int keyId;

    public Key(int id, Position position, int keyId) {
        super(id, "key");
        setPosition(position);
        this.keyId = keyId;
    }

    public Key(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(
                id,
                new Position(entityJSON.getInt("x"), entityJSON.getInt("y")),
                entityJSON.getInt("key")
        );
    }

    public Integer getKeyId() {
        return keyId;
    }

    @Override
    public void onTrigger(Entity other) {
        if (other instanceof Player) {
            Player player = (Player) other;
            if (player.hasItem(Key.class)) {
                return;
            }
        }
        super.onTrigger(other);
    }
}
