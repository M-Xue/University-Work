package dungeonmania.entities.collectable;

import dungeonmania.entities.Entity;
import dungeonmania.entities.ItemEntity;
import dungeonmania.entities.player.Player;
import dungeonmania.entities.behaviours.Triggerable;

public abstract class CollectableEntity extends ItemEntity implements Triggerable {
    public CollectableEntity(int id, String type) {
        super(id, type);
    }

    @Override
    public void onTrigger(Entity other) {
        if (other instanceof Player) {
            Player player = (Player) other;
            player.addItem(this);
            getDungeon().removeEntity(this);
        }
    }
}
