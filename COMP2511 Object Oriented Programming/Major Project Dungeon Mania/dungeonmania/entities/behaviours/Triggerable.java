package dungeonmania.entities.behaviours;

import dungeonmania.entities.Entity;

public interface Triggerable {
    /**
     * This function is called when other entities are moved in.
     * @param other
     */
    void onTrigger(Entity other);
}
