package dungeonmania.entities.collectable.potions;

import dungeonmania.entities.player.Player;
import dungeonmania.entities.behaviours.Durable;
import dungeonmania.entities.behaviours.Usable;
import dungeonmania.entities.collectable.CollectableEntity;

public class Potion extends CollectableEntity implements Usable, Durable {
    private int durability;

    public Potion(int id, String type) {
        super(id, type);
    }

    public boolean isExpired() {
        return getDurability() < 0;
    }

    @Override
    public void onUse(Player player) {
        player.addPotionEffect(this);
    }

    @Override
    public int getDurability() {
        return durability;
    }

    @Override
    public void setDurability(int durability) {
        this.durability = durability;
    }
}
