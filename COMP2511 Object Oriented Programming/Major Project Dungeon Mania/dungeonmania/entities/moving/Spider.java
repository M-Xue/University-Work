package dungeonmania.entities.moving;

import dungeonmania.entities.Entity;
import dungeonmania.entities.player.Player;
import dungeonmania.entities.behaviours.Triggerable;
import dungeonmania.entities.moving.strategies.CircularMovement;
import dungeonmania.util.Position;
import dungeonmania.battle.Battle;
import org.json.JSONObject;

public class Spider extends MovingEntity implements Triggerable {
    public Spider(int id, Position position, JSONObject configJSON) {
        super(
                id,
                "spider",
                configJSON.getDouble("spider_health"),
                configJSON.getDouble("spider_attack")
        );
        setPosition(position);
        setMovementStrategy(new CircularMovement());
    }

    public Spider(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")), configJSON);
    }

    @Override
    public void onTrigger(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            getDungeon().addBattle(new Battle(player, this));
        }
    }
}
