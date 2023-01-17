package dungeonmania.entities;

import dungeonmania.util.Position;
import org.json.JSONObject;

public class Wall extends Entity {
    public Wall(int id, Position position) {
        super(id, "wall");
        setPosition(position);
    }

    public Wall(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")));
    }

    @Override
    public boolean isBlocking() {
        return true;
    }
}
