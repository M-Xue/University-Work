package dungeonmania.entities.moving;

import org.json.JSONObject;

import dungeonmania.battle.Battle;
import dungeonmania.entities.Entity;
import dungeonmania.entities.ItemEntity;
import dungeonmania.entities.player.Player;
import dungeonmania.entities.behaviours.Interactable;
import dungeonmania.entities.behaviours.Triggerable;
import dungeonmania.entities.buildable.Sceptre;
import dungeonmania.entities.collectable.Treasure;
import dungeonmania.entities.collectable.potions.InvincibilityPotion;
import dungeonmania.entities.collectable.potions.InvisibilityPotion;
import dungeonmania.entities.moving.strategies.FollowMovement;
import dungeonmania.entities.moving.strategies.RandomMovement;
import dungeonmania.entities.moving.strategies.RunAwayMovement;
import dungeonmania.entities.moving.strategies.ShortestPathMovement;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.util.Position;

public class Assassin extends MovingEntity implements Triggerable, Interactable {
    private final double bribeFailRate;
    private final int reconRaius;
    private final int bribeAmount;
    private final int bribeRadius;
    private final int allyAttack;
    private final int allyDefence;
    private boolean isBribed;
    private int mindControlDuration;

    public Assassin(int id, Position position, JSONObject configJSON) {
        super(
                id,
                "assassin",
                configJSON.optDouble("assassin_health",1.0),
                configJSON.optDouble("assassin_attack",1.0));
        this.isBribed= false;
        this.mindControlDuration = 0;
        this.bribeAmount = configJSON.optInt("assassin_bribe_amount",1);
        this.bribeRadius = configJSON.getInt("bribe_radius");
        this.allyAttack = configJSON.getInt("ally_attack");
        this.allyDefence = configJSON.getInt("ally_defence");
        // potentially will need to be changed
        this.setPosition(position);
        this.setMovementStrategy(new ShortestPathMovement());
        this.bribeFailRate = configJSON.optDouble("assassin_bribe_fail_rate",0.0);
        this.reconRaius = configJSON.optInt("assassin_recon_radius",1);
    }

    public Assassin(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")), configJSON);
    }

    public int getAllyAttack() {
        return allyAttack;
    }

    public int getAllyDefence() {
        return allyDefence;
    }

    @Override
    public void onTrigger(Entity entity) {
        if (entity instanceof Player && !isAlly()) {
            Player player = (Player) entity;
            getDungeon().addBattle(new Battle(player, this));
        }
    }

    @Override
    public void onInteract(Player player) throws InvalidActionException {
        if (player.hasItem(Sceptre.class)) {
            // Sceptre mind control from any distance, but only lasts for
            // mind_control_duration ticks.
            mindControlDuration = player.getFirstItem(Sceptre.class).getMindControlDuration();
        } else if (withinRadius(player.getPosition(), bribeRadius)
                && player.countItems(Treasure.class) >= bribeAmount) {
            player.removeItem(Treasure.class, bribeAmount);
            if (bribeFailed()) {
                // Might need to be changed to something else
                System.out.println("Assassin did not want to bribe and just took the money " + getIdString());
            } else {
                isBribed = true;
            }
        } else {
            throw new InvalidActionException(String.format(
                    "Player does not have sceptre, or not within bribe radius, or not enough treasure to bribe assassin %s",
                    getIdString()));
        }

    }

    private boolean bribeFailed() {
        return Math.random() < bribeFailRate;
    }

    public boolean isAlly() {
        return isBribed || mindControlDuration > 0;
    }

    @Override
    public boolean isHostile() {
        return !isAlly();
    }

    @Override
    public boolean isInteractable() {
        return !isAlly();
    }

    @Override
    public void increment() {
        Player player = this.getDungeon().getPlayer();

        mindControlDuration--;

        if (isAlly()) {
            this.setMovementStrategy(new FollowMovement());
        } else if (player.hasPotionEffect(InvincibilityPotion.class)) {
            this.setMovementStrategy(new RunAwayMovement());
        } else if (player.hasPotionEffect(InvisibilityPotion.class)
                && !withinRadius(player.getPosition(), reconRaius)) {
            this.setMovementStrategy(new RandomMovement());
        } else {
            this.setMovementStrategy(new ShortestPathMovement());
        }

        super.increment();
    }

    private boolean withinRadius(Position otherPosition, int radius) {
        Position offset = Position.calculatePositionBetween(getPosition(), otherPosition);
        if (offset.getX() <= radius && offset.getX() >= -radius && offset.getY() <= radius
                && offset.getY() >= -radius) {
            return true;
        } else {
            return false;
        }
    }
}
