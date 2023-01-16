package dungeonmania.entities;

import dungeonmania.util.Pair;
import dungeonmania.util.Position;

import java.util.List;

public abstract class LivingEntity extends Entity {
    private final double basicAttack;
    private double health;

    public LivingEntity(int id, String type, double health, double basicAttack) {
        super(id, type);
        this.health = health;
        this.basicAttack = basicAttack;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(Double health) {
        this.health = health;
    }

    public double getBasicAttack() {
        return basicAttack;
    }

    public boolean isDead() {
        return getHealth() <= 0;
    }

    /**
     * Get the actual attack with the weapons used in the battle, and consume weapon durability.
     * Called when the entity is attacking other entities.
     * @return A Pair of {@code (actualAttack, weaponsUsed)}. If the entity cannot use
     * weapons, then {@code weaponsUsed} will be null.
     */
    public abstract Pair<Double, List<ItemEntity>> attack();

    /**
     * Get the change of health with the weapons used in the battle, and consume weapon durability.
     * Called when the entity is attacked by other entities.
     * @param attack The actual attack of another entity.
     * @return A Pair of {@code (deltaHealth, weaponsUsed)}. If the entity
     * cannot use weapons, then {@code weaponsUsed} will be null.
     */
    public abstract Pair<Double, List<ItemEntity>> onDamaged(double attack);
}
