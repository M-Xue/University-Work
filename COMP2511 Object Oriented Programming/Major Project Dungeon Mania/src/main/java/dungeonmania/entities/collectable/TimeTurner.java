package dungeonmania.entities.collectable;

import dungeonmania.util.Position;
import org.json.JSONObject;

public class TimeTurner extends CollectableEntity {
    public TimeTurner(int id, Position position) {
        super(id, "time_turner");
        setPosition(position);
    }

    public TimeTurner(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")));
    }
}
