package dungeonmania.dungeon.actions;

import dungeonmania.entities.player.Player;
import org.json.JSONObject;

public interface PlayerAction {
    void execute(Player player);

    JSONObject toJSON();

    static PlayerAction fromJSON(JSONObject jsonObject) {
        String type = jsonObject.getString("type");

        switch (type) {
            case "movement":
                return new MovementAction(jsonObject);
            case "use_item":
                return new UseItemAction(jsonObject);
            case "interact":
                return new InteractAction(jsonObject);
            case "build":
                return new BuildAction(jsonObject);
            case "rewind":
                return new RewindAction(jsonObject);
            default:
                throw new IllegalArgumentException("Unknown action type");
        }
    }
}
