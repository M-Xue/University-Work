package dungeonmania.entities.collectable;

import dungeonmania.entities.behaviours.Durable;
import dungeonmania.entities.behaviours.Weapon;
import dungeonmania.util.Position;
import org.json.JSONObject;

public class Sword extends CollectableEntity implements Durable, Weapon {
    private final int attack;

    /**
     * Durability: Number of times it can be used in battle.
     */
    private int durability;
    
    public Sword(int id, Position position, JSONObject configJSON) {
        super(id, "sword");
        setPosition(position);
        this.attack = configJSON.getInt("sword_attack");
        this.durability = configJSON.getInt("sword_durability");
    }

    public Sword(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(
                id,
                new Position(entityJSON.getInt("x"), entityJSON.getInt("y")),
                configJSON
        );
    }

    public int getAttack() {
        return attack;
    }

    @Override
    public int getDurability() {
        return durability;
    }

    @Override
    public void setDurability(int durability) {
        this.durability = durability;
    }

    @Override
    public double getBonusedAttack(double basicAttack) {
        return basicAttack + attack;
    }
}
