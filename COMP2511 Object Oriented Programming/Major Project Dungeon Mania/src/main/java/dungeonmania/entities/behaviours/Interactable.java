package dungeonmania.entities.behaviours;

import dungeonmania.entities.player.Player;
import dungeonmania.exceptions.InvalidActionException;

public interface Interactable {
    void onInteract(Player player) throws InvalidActionException;

    boolean isInteractable();
}
