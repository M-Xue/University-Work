package dungeonmania.entities.collectable;

import dungeonmania.util.Position;
import org.json.JSONObject;

public class SunStone extends CollectableEntity {
    public SunStone(int id, Position position) {
        super(id, "sun_stone");
        setPosition(position);
    }

    public SunStone(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")));
    }
}
