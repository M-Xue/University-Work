package dungeonmania.entities.actives;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import dungeonmania.entities.Entity;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Position;
import static java.util.stream.Collectors.toList;

public class Lightbulb extends Activatable {
    public Lightbulb(int id, Position position, String condition) {
        super(id, "light_bulb_off", condition);
        setPosition(position);
    }

    public Lightbulb(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")), entityJSON.getString("logic"));
    }

    @Override
    public String getType() {
        if (isActive()) {
            return "light_bulb_on";
        } else {
            return "light_bulb_off";
        }
    }
}
