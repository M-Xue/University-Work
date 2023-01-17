package dungeonmania.entities.moving;

import dungeonmania.entities.player.Player;
import dungeonmania.entities.behaviours.Triggerable;
import dungeonmania.entities.Entity;
import dungeonmania.entities.collectable.potions.InvincibilityPotion;
import dungeonmania.entities.moving.strategies.RandomMovement;
import dungeonmania.entities.moving.strategies.RunAwayMovement;
import dungeonmania.util.Position;
import dungeonmania.battle.Battle;
import org.json.JSONObject;

public class ZombieToast extends MovingEntity implements Triggerable {

    public ZombieToast(int id, Position position, JSONObject configJSON) {
        super(
                id,
                "zombie_toast",
                configJSON.getDouble("zombie_health"),
                configJSON.getDouble("zombie_attack")
        );
        setPosition(position);
        setMovementStrategy(new RandomMovement());
    }

    public ZombieToast(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")), configJSON);
    }

    @Override
    public void onTrigger(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            getDungeon().addBattle(new Battle(player, this));
        }
    }

    @Override
    public void increment(){
        Player player = this.getDungeon().getPlayer();

        if (player.hasPotionEffect(InvincibilityPotion.class)) {
            this.setMovementStrategy(new RunAwayMovement());
        } else {
            this.setMovementStrategy(new RandomMovement());
        }

        super.increment();
    }
}
