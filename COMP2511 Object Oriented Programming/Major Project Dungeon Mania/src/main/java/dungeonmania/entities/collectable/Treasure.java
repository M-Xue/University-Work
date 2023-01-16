package dungeonmania.entities.collectable;

import dungeonmania.util.Position;
import org.json.JSONObject;

public class Treasure extends CollectableEntity {
    public Treasure(int id, Position position) {
        super(id, "treasure");
        setPosition(position);
    }

    public Treasure(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")));
    }
}
