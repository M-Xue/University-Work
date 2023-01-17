package dungeonmania.dungeon.actions;

import dungeonmania.entities.player.Player;
import dungeonmania.util.Direction;
import org.json.JSONObject;

public class MovementAction implements PlayerAction {
    private final Direction direction;

    public MovementAction(Direction direction) {
        this.direction = direction;
    }

    public MovementAction(JSONObject jsonObject) {
        this(Direction.valueOf(jsonObject.getString("direction")));
    }

    @Override
    public void execute(Player player) {
        player.move(direction);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "movement");
        jsonObject.put("direction", direction.name());
        return jsonObject;
    }

    @Override
    public String toString() {
        return direction.name();
    }
}
