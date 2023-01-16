package dungeonmania.dungeon.actions;

import dungeonmania.entities.player.Player;
import org.json.JSONObject;

public class BuildAction implements PlayerAction {
    private final String buildableType;

    public BuildAction(String buildableType) {
        this.buildableType = buildableType;
    }

    public BuildAction(JSONObject jsonObject) {
        this(jsonObject.getString("buildable_type"));
    }

    @Override
    public void execute(Player player) {
        try {
            player.build(buildableType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "build");
        jsonObject.put("buildable_type", buildableType);
        return jsonObject;
    }
}
