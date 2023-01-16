package dungeonmania.entities.collectable.potions;

import dungeonmania.util.Position;
import org.json.JSONObject;

public class InvincibilityPotion extends Potion {
    public InvincibilityPotion(int id, Position position, JSONObject configJSON) {
        super(id, "invincibility_potion");
        setPosition(position);
        setDurability(configJSON.getInt("invincibility_potion_duration"));
    }

    public InvincibilityPotion(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(
                id,
                new Position(entityJSON.getInt("x"), entityJSON.getInt("y")),
                configJSON
        );
    }
}
