package dungeonmania.entities.behaviours;

import dungeonmania.entities.player.Player;
import dungeonmania.util.Direction;

public interface PreTriggerable {
    /**
     * This function is called before other entities (temporarily only players) are moved in.
     * @param player
     */
    void preTrigger(Player player);
}
