package dungeonmania.entities.buildable;

import dungeonmania.entities.ItemEntity;
import dungeonmania.entities.behaviours.Weapon;
import dungeonmania.entities.collectable.Arrow;
import dungeonmania.entities.collectable.Key;
import dungeonmania.entities.collectable.SunStone;
import dungeonmania.entities.collectable.Sword;
import dungeonmania.entities.collectable.Treasure;
import dungeonmania.entities.collectable.Wood;
import dungeonmania.entities.moving.ZombieToast;
import dungeonmania.util.Pair;
import org.json.JSONObject;

import java.util.List;

public class MidnightArmour extends BuildableEntity implements Weapon{
    private final int attackBonus;
    private final int defenceBonus;

    public MidnightArmour(int id, JSONObject configJSON) {
        super(id, "midnight_armour");
        this.attackBonus = configJSON.getInt("midnight_armour_attack");
        this.defenceBonus = configJSON.getInt("midnight_armour_defence");
    }

    public MidnightArmour(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, configJSON);
    }

    public double getBonusedAttack(double basicAttack) {
        return basicAttack + attackBonus;
    }

    public double getDefence() {
        return defenceBonus;
    }

    /**
     * Can only be built is there are no zombies
     * currently in dungoen.
     */
    public boolean canBuild() {
        return !getDungeon().getEntities().stream().anyMatch(entity -> entity instanceof ZombieToast);
    }

    @Override
    public List<List<Pair<Class<? extends ItemEntity>, Integer>>> getMaterials() {
        return List.of(
                List.of(
                        Pair.of(Sword.class, 1)
                ),    
                List.of(
                        Pair.of(SunStone.class, 1)
                )
        );
    }

}

