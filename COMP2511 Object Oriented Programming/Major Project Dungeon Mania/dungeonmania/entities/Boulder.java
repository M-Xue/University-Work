package dungeonmania.entities;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.entities.behaviours.PreTriggerable;
import dungeonmania.entities.player.Player;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.json.JSONObject;

public class Boulder extends Entity implements PreTriggerable {
    public Boulder(int id, Position position) {
        super(id, "boulder");
        setPosition(position);
    }

    public Boulder(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")));
    }

    @Override
    public boolean isBlocking(){
        return true;
    }

    @Override
    public void preTrigger(Player player) {
        Dungeon dungeon = getDungeon();
        Position relativePos = Position.calculatePositionBetween(player.getPosition(), getPosition());
        Position targetPosition = getPosition().translateBy(relativePos);
        if (dungeon.isOpenTile(targetPosition)) {
            dungeon.moveEntity(targetPosition, this);
        }
    }
}
