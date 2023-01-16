package dungeonmania.entities.buildable;

import dungeonmania.entities.ItemEntity;
import dungeonmania.entities.behaviours.Durable;
import dungeonmania.entities.behaviours.Weapon;
import dungeonmania.entities.collectable.Arrow;
import dungeonmania.entities.collectable.Wood;
import dungeonmania.util.Pair;
import org.json.JSONObject;

import java.util.List;

public class Bow extends BuildableEntity implements Durable, Weapon {
    private int durability;

    public Bow(int id, JSONObject configJSON) {
        super(id, "bow");
        this.durability = configJSON.getInt("bow_durability");
    }

    public Bow(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, configJSON);
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
        return basicAttack * 2;
    }

    @Override
    public List<List<Pair<Class<? extends ItemEntity>, Integer>>> getMaterials() {
        return List.of(
                List.of(Pair.of(Wood.class, 1)),
                List.of(Pair.of(Arrow.class, 3))
        );
    }
}

