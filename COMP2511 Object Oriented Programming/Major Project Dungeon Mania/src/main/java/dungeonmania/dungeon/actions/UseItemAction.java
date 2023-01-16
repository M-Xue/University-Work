package dungeonmania.dungeon.actions;

import dungeonmania.entities.player.Player;
import org.json.JSONObject;

public class UseItemAction implements PlayerAction {
    private final String itemUsedId;

    public UseItemAction(String itemUsedId) {
        this.itemUsedId = itemUsedId;
    }

    public UseItemAction(JSONObject jsonObject) {
        this(jsonObject.getString("item"));
    }

    @Override
    public void execute(Player player) {
        try {
            player.useItem(itemUsedId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "use_item");
        jsonObject.put("item", itemUsedId);
        return jsonObject;
    }
}
