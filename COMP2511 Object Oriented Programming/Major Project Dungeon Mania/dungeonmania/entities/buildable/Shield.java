package dungeonmania.entities.buildable;

import dungeonmania.entities.ItemEntity;
import dungeonmania.entities.behaviours.Durable;
import dungeonmania.entities.collectable.Key;
import dungeonmania.entities.collectable.SunStone;
import dungeonmania.entities.collectable.Treasure;
import dungeonmania.entities.collectable.Wood;
import dungeonmania.util.Pair;
import org.json.JSONObject;

import java.util.List;

public class Shield extends BuildableEntity implements Durable {
    private final int defence;
    private int durability;

    public Shield(int id, JSONObject configJSON) {
        super(id, "shield");
        this.durability = configJSON.getInt("shield_durability");
        this.defence = configJSON.getInt("shield_defence");
    }

    public Shield(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, configJSON);
    }

    public int getDefence() {
        return defence;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    @Override
    public List<List<Pair<Class<? extends ItemEntity>, Integer>>> getMaterials() {
        return List.of(
                List.of(Pair.of(Wood.class, 2)),
                List.of(
                        Pair.of(SunStone.class, 1),
                        Pair.of(Treasure.class, 1),
                        Pair.of(Key.class, 1)
                )
        );
    }

}

