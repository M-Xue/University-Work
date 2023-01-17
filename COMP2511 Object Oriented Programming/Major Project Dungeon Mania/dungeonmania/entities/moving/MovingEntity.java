package dungeonmania.entities.moving;

import dungeonmania.entities.ItemEntity;
import dungeonmania.entities.LivingEntity;
import dungeonmania.entities.behaviours.Incrementable;
import dungeonmania.entities.moving.strategies.MovementStrategy;
import dungeonmania.util.Pair;
import dungeonmania.util.Position;

import java.util.List;

abstract public class MovingEntity extends LivingEntity implements Incrementable {
    private MovementStrategy movementStrategy;
    private boolean isStuck;

    public MovingEntity(int id, String type, double health, double attack) {
        super(id, type, health, attack);
        isStuck = false;
    }

    @Override
    public void increment() {
        if (movementStrategy != null && !isStuck) {
            movementStrategy.move(this);
        }
    }

    public void setMovementStrategy(MovementStrategy movementStrategy) {
        this.movementStrategy = movementStrategy;
    }

    public boolean isStuck() {
        return isStuck;
    }

    public void setStuck(boolean isStuck) {
        this.isStuck = isStuck;
    }

    @Override
    public Pair<Double, List<ItemEntity>> onDamaged(double attack) {
        double deltaHealth = -(attack / 5);
        setHealth(getHealth() + deltaHealth);
        return Pair.of(deltaHealth, null);
    }

    @Override
    public Pair<Double, List<ItemEntity>> attack() {
        return Pair.of(getBasicAttack(), null);
    }

    public boolean isHostile() {
        return true;
    }
}
