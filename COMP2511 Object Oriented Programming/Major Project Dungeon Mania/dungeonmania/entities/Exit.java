package dungeonmania.entities;

import dungeonmania.util.Position;
import org.json.JSONObject;

public class Exit extends Entity {
    public Exit(int id, Position position) {
        super(id, "exit");
        setPosition(position);
    }

    public Exit(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")));
    }
}
