package dungeonmania.entities.collectable;

import dungeonmania.util.Position;
import org.json.JSONObject;

public class Arrow extends CollectableEntity {
    public Arrow(int id, Position position) {
        super(id, "arrow");
        setPosition(position);
    }

    public Arrow(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")));
    }

}
