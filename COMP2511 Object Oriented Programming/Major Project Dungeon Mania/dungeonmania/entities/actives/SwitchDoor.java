package dungeonmania.entities.actives;

import org.json.JSONObject;

import dungeonmania.entities.Entity;
import dungeonmania.util.Position;

public class SwitchDoor extends Activatable  {
    public SwitchDoor(int id, Position position, String condition) {
        super(id, "switch_door", condition);
        setPosition(position);
    }

    public SwitchDoor(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")), entityJSON.getString("logic"));
    } 

    @Override
    public boolean isBlocking() {
        return !this.isActive();
    }
}
