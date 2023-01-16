package dungeonmania.entities;

import dungeonmania.entities.behaviours.PreTriggerable;
import dungeonmania.entities.player.Player;
import dungeonmania.util.Position;
import org.json.JSONObject;


public class TimeTravellingPortal extends Entity implements PreTriggerable {
    public TimeTravellingPortal(int id, Position position) {
        super(id, "time_travelling_portal");
        setPosition(position);
    }

    public TimeTravellingPortal(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")));
    }

    @Override
    public void preTrigger(Player player) {
        int ticks = player.getActions().size();
        if (!player.isDummy() && ticks > 0) {
            player.getDungeon().rewind(Math.min(ticks, 30), false);
        }
    }

    @Override
    public boolean isBlocking() {
        return true;
    }
}
