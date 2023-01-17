package dungeonmania.dungeon.actions;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.entities.collectable.TimeTurner;
import dungeonmania.entities.player.Player;
import dungeonmania.util.Position;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RewindAction implements PlayerAction {
    private final Position position;
    private final List<PlayerAction> actionsQueue;
    private final boolean isConsumeTimeTurner;

    public RewindAction(Position position, List<PlayerAction> actionsQueue, boolean isConsumeTimeTurner) {
        this.position = position;
        this.actionsQueue = actionsQueue;
        this.isConsumeTimeTurner = isConsumeTimeTurner;
    }

    public RewindAction(JSONObject jsonObject) {
        this(
                new Position(jsonObject.getInt("x"), jsonObject.getInt("y")),
                actionsQueueFromJSON(jsonObject.getJSONArray("actions_queue")),
                jsonObject.getBoolean("is_consume_time_turner")
        );
    }

    @Override
    public void execute(Player player) {
        Dungeon dungeon = player.getDungeon();

        Player clonedPlayer = player.clone();
        clonedPlayer.setId(dungeon.generateEntityId());
        clonedPlayer.setPosition(position);

        player.setActionsQueue(actionsQueue);
        player.setIsDummy(true);

        if (isConsumeTimeTurner) {
            clonedPlayer.removeItem(TimeTurner.class, 1);
        }
        clonedPlayer.getActions().add(this);
        dungeon.addEntity(clonedPlayer);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("type", "rewind");
        jsonObject.put("x", position.getX());
        jsonObject.put("y", position.getY());

        JSONArray actionsQueue = new JSONArray();
        this.actionsQueue.forEach(action -> actionsQueue.put(action.toJSON()));
        jsonObject.put("actions_queue", actionsQueue);

        jsonObject.put("is_consume_time_turner", isConsumeTimeTurner);

        return jsonObject;
    }

    private static List<PlayerAction> actionsQueueFromJSON(JSONArray jsonArray) {
        ArrayList<PlayerAction> actionsQueue = new ArrayList<>();
        jsonArray.forEach(o -> actionsQueue.add(PlayerAction.fromJSON((JSONObject) o)));
        return actionsQueue;
    }
}
