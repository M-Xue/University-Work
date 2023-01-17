package dungeonmania.dungeon.actions;

import dungeonmania.entities.player.Player;
import org.json.JSONObject;

public class InteractAction implements PlayerAction {
    private final String entityId;

    public InteractAction(String entityId) {
        this.entityId = entityId;
    }

    public InteractAction(JSONObject jsonObject) {
        this(jsonObject.getString("entity_id"));
    }

    @Override
    public void execute(Player player) {
        try {
            player.interact(entityId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "interact");
        jsonObject.put("entity_id", entityId);
        return jsonObject;
    }
}
