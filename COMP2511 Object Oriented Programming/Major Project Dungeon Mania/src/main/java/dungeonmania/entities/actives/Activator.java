package dungeonmania.entities.actives;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.entities.Entity;
import dungeonmania.entities.collectable.Bomb;
import dungeonmania.util.Position;

public abstract class Activator extends Activatable {
    public Activator(int id, String type, String condition) {
        super(id, type, condition);
    }

    // Deactivates a given Activatable. If the Activatable is responsible for another Activatable being active (i.e., it is a Activator with Activatables depending on it), recursively go to that Activatable and deactivate it and its dependenant Activatables as well. 
    public void spreadDeactivation(Activatable deactivatedActivatable) {
        deactivatedActivatable.setActive(false);
        deactivatedActivatable.getParentActivators().clear();

        Collection<Entity> entityList = getDungeon().getEntities();
        List<Activatable> activatables = entityList.stream()
            .filter(other -> other != deactivatedActivatable)
            .filter(Activatable.class::isInstance)
            .map(other -> (Activatable) other)
            .collect(Collectors.toList());

        for (Activatable a : activatables) {
            if (a.getParentActivators().contains(deactivatedActivatable.getId())) {
                spreadDeactivation(a);
            }
        }
    }

    // Activates all Activators if they satisfy their conditions to be activated.
    public void spreadActivatorActivation() {
        Collection<Entity> entityList = getDungeon().getEntities();
        List<Activator> activators = entityList.stream()
                .filter(Activator.class::isInstance)
                .map(other -> (Activator) other)
                .collect(Collectors.toList());

        int prevNumActiveActivators = 0;
        int newNumActiveActivators = -1;

        while (prevNumActiveActivators != newNumActiveActivators) {
            getDungeon().incrementActivatableSpreadCount();
            int currActivatableSpreadCount = getDungeon().getActivatableSpreadCount();

            prevNumActiveActivators = (int)activators.stream().filter(a -> a.isActive() == true).count();
            activators.stream()
                .filter(Activatable.class::isInstance)
                .map(other -> (Activatable) other)
                .forEach(a -> a.activate(currActivatableSpreadCount));
            newNumActiveActivators = (int)activators.stream().filter(a -> a.isActive() == true).count();
        }
    }

    // Activates all Activatables if they satisfy their conditions to be activated. 
    public void spreadActivatableActivation() {
        Collection<Entity> entityList = getDungeon().getEntities();
        List<Activatable> activatables = entityList.stream()
            .filter(other -> other != this)
            .filter(Activatable.class::isInstance)
            .filter(a -> !(a instanceof Activator))
            .map(other -> (Activatable) other)
            .collect(Collectors.toList());

        for (Activatable a : activatables) {
            a.activate(-1);
        }

        List<Bomb> bombs = entityList.stream()
            .filter(Bomb.class::isInstance)
            .map(other -> (Bomb) other)
            .collect(Collectors.toList());
        bombs.stream().forEach(b -> b.activate());
    }
}
