package dungeonmania.entities.collectable.potions;

import dungeonmania.util.Position;
import org.json.JSONObject;

public class InvisibilityPotion extends Potion {
    public InvisibilityPotion(int id, Position position, JSONObject configJSON) {
        super(id, "invisibility_potion");
        setPosition(position);
        setDurability(configJSON.getInt("invisibility_potion_duration"));
    }

    public InvisibilityPotion(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(
                id,
                new Position(entityJSON.getInt("x"), entityJSON.getInt("y")),
                configJSON
        );
    }
}
