package dungeonmania.entities.collectable;

import dungeonmania.util.Position;
import org.json.JSONObject;

public class Wood extends CollectableEntity {
    public Wood(int id, Position position) {
        super(id, "wood");
        setPosition(position);
    }

    public Wood(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")));
    }

}
