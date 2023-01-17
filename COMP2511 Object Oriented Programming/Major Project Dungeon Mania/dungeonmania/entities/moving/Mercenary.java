package dungeonmania.entities.moving;

import dungeonmania.battle.Battle;
import dungeonmania.entities.Entity;
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
import dungeonmania.entities.player.Player;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.util.Position;
import org.json.JSONObject;

public class Mercenary extends MovingEntity implements Triggerable, Interactable {
    private final Integer bribeAmount;
    private final Integer bribeRadius;
    private final int allyAttack;
    private final int allyDefence;
    private boolean isBribed;
    private int mindControlDuration;

    public Mercenary(int id, Position position, JSONObject configJSON) {
        super(
                id,
                "mercenary",
                configJSON.getDouble("mercenary_health"),
                configJSON.getDouble("mercenary_attack")
        );
        setPosition(position);
        this.isBribed = false;
        this.mindControlDuration = 0;
        this.bribeAmount = configJSON.getInt("bribe_amount");
        this.bribeRadius = configJSON.getInt("bribe_radius");
        this.allyAttack = configJSON.getInt("ally_attack");
        this.allyDefence = configJSON.getInt("ally_defence");
        this.setMovementStrategy(new ShortestPathMovement());
    }

    public Mercenary(int id, JSONObject entityJSON, JSONObject configJSON) {
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
        } else if (withinBribeRadius(player.getPosition()) && player.countItems(Treasure.class) >= bribeAmount) {
            player.removeItem(Treasure.class, bribeAmount);
            isBribed = true;
        } else {
            throw new InvalidActionException(String.format("Player does not have sceptre, or not within bribe radius, or not enough treasure to bribe mercenary %s", getIdString()));
        }
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

        mindControlDuration = Math.max(0, mindControlDuration-1);

        if (isAlly()) {
            this.setMovementStrategy(new FollowMovement());
        } else if (player.hasPotionEffect(InvincibilityPotion.class)) {
            this.setMovementStrategy(new RunAwayMovement());
        } else if (player.hasPotionEffect(InvisibilityPotion.class)) {
            this.setMovementStrategy(new RandomMovement());
        } else {
            this.setMovementStrategy(new ShortestPathMovement());
        }

        super.increment();
    }

    private boolean withinBribeRadius(Position otherPosition) {
        Position offset = Position.calculatePositionBetween(getPosition(), otherPosition);
        return offset.getX() <= bribeRadius && offset.getX() >= -bribeRadius && offset.getY() <= bribeRadius
                && offset.getY() >= -bribeRadius;
    }

}
