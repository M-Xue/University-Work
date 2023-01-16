package dungeonmania.entities.moving;

import dungeonmania.entities.player.Player;
import dungeonmania.entities.behaviours.Triggerable;
import dungeonmania.entities.Entity;
import dungeonmania.entities.ItemEntity;
import dungeonmania.entities.collectable.potions.InvincibilityPotion;
import dungeonmania.entities.moving.strategies.RandomMovement;
import dungeonmania.entities.moving.strategies.RunAwayMovement;
import dungeonmania.util.Pair;
import dungeonmania.util.Position;
import dungeonmania.battle.Battle;

import java.util.List;

import org.json.JSONObject;

public class Hydra extends MovingEntity implements Triggerable {
    private final double healthIncreaseRate;
    private final int healthIncreaseAmount;

    public Hydra(int id, Position position, JSONObject configJSON) {
        super(
            id,
            "hydra",
            configJSON.optDouble("hydra_health",1),
            configJSON.optDouble("hydra_attack",1)
        );
        setPosition(position);
        setMovementStrategy(new RandomMovement());
        healthIncreaseRate = configJSON.optDouble("hydra_health_increase_rate",0.5);
        healthIncreaseAmount = configJSON.optInt("hydra_health_increase_amount", 1);
    }

    public Hydra(int id, JSONObject entityJSON, JSONObject configJSON) {
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

    @Override
    public Pair<Double, List<ItemEntity>> onDamaged(double attack) {
        double deltaHealth;
        if (increaseHealth()) {
            deltaHealth = healthIncreaseAmount;
        } else {
            deltaHealth = -(attack / 5);
        }
        
        setHealth(getHealth() + deltaHealth);
        return Pair.of(deltaHealth, null);
    }

    private boolean increaseHealth() {
        return Math.random() < healthIncreaseRate;
    }
}
