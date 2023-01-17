package dungeonmania.entities.actives;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Switch;
import dungeonmania.entities.collectable.Bomb;
import dungeonmania.util.Position;
import static java.util.stream.Collectors.toList;
import dungeonmania.util.PositionExt;

public abstract class Activatable extends Entity {
    private int lastActivatedTick = -1;

    private boolean active = false;
    private String condition = "";

    private ArrayList<Integer> parentActivators = new ArrayList<Integer>();

    
    public Activatable(int id, String type, String condition) {
        super(id, type);
        this.condition = condition;
    }

    // This method gets all entities that can activate other entities that are cardianlly adjacent to the current activatable (this) into an array list
    public ArrayList<Activator> getCardinalActivators() {
        ArrayList<Activator> activators = new ArrayList<Activator>();
        Dungeon dungeon = getDungeon();
        List<Position> cardinallyAdjacentPositions = PositionExt.getCardinallyAdjacentPositions(getPosition());
        for (Position pos : cardinallyAdjacentPositions) {
            List<Entity> entitiesOnTile =  dungeon.getEntitiesOnTile(pos);
            for (Entity entity : entitiesOnTile) {
                if (entity instanceof Activator) {
                    Activator activator = (Activator)entity;
                    activators.add(activator);
                }
            }
        }
        return activators;
    }

    // This method makes the activatable entity check it's cardinally adjacent tiles to look for near by Activators to see if the conditions are met for it to become active, remain active or deactivate.
    // If the conditions are met, the active private instance variable is made true, otherwise, it's turned to false.
    public void activate(int currActivatableSpreadCount) {

        if (this instanceof Switch) {
            Switch s = (Switch)this;
            if (s.isCovered()) {
                if (active && getParentActivators().contains(this.getId())) {
                    return;
                }

                this.active = true;
                this.getParentActivators().clear();
                this.getParentActivators().add(this.getId());
                this.lastActivatedTick = getDungeon().getTickCount();
                return;
            }
            if (active) {return;}
        }

        if (this instanceof Wire) {
            if (active) {return;}
        }

        ArrayList<Activator> activators = getCardinalActivators();
        if (condition.equals("or")) {
            if (activators.stream().filter(a -> a.isActive() == true).count() >= 1) {
                this.parentActivators.add(getActivatorId(activators.stream().filter(a -> a.isActive() == true).collect(Collectors.toList()).get(0)));

                this.active = true;
                this.lastActivatedTick = getDungeon().getTickCount();
            } else {
                this.getParentActivators().clear();
                this.active = false;
            }
        } else if (condition.equals("and")) {
            if (activators.stream().filter(a -> a.isActive() == true).count() >= 2 && activators.stream().allMatch(a -> a.isActive() == true)) {

                for (Activator activeActivator : activators.stream().filter(a -> a.isActive() == true).collect(Collectors.toList())) {
                    this.parentActivators.add(getActivatorId(activeActivator));
                }

                this.active = true;
                this.lastActivatedTick = getDungeon().getTickCount();
            } else {
                this.getParentActivators().clear();
                this.active = false;
            }
        } else if (condition.equals("xor")) {
            if (activators.stream().filter(a -> a.isActive() == true).count() == 1) {

                if (currActivatableSpreadCount == getDungeon().getActivatableSpreadCount()) {return;} //**** GSDFSDGSDFG */
                // you only need to check it here because the condition for XOR might be satisfied for one spread of Activator activations and then be unsatsifed on a later spread stemming from one call of spreadActivatorActivation(). However, for all other conditions, if they are satisfied on a particular spread, can never be unsatisfied in the same call of spreadActivatorActivation() (if AND is satisfied, it means all cardinally adjacent Activators are active and you can't deactivate them during spreading because deactivation is its own seperate fucntion call seperate from activations spreading.)

                this.parentActivators.add(getActivatorId(activators.stream().filter(a -> a.isActive() == true).collect(Collectors.toList()).get(0)));

                this.active = true;
                this.lastActivatedTick = getDungeon().getTickCount();
            } else {
                this.getParentActivators().clear();
                this.active = false;
            }
        } else if (condition.equals("co_and")) {
            List<Activator> activeActivators = activators.stream().filter(a -> a.isActive() == true).collect(toList());
            for (int i = 0; i < activeActivators.size(); i++) {
                for (int j = i + 1; j < activeActivators.size(); j++) {
                    Activatable parent1 = (Activatable)activeActivators.get(i);
                    Activatable parent2 = (Activatable)activeActivators.get(j);
                    if (parent1.getLastActivatedTick() == parent2.getLastActivatedTick()) {

                        this.parentActivators.add(getActivatorId(activeActivators.get(i)));
                        this.parentActivators.add(getActivatorId(activeActivators.get(j)));

                        this.active = true;
                        this.lastActivatedTick = getDungeon().getTickCount();
                        return;
                    }
                }
            }
            this.getParentActivators().clear();
            this.active = false;
        }
    }

    // Returns if the Activatable is currently active
    public boolean isActive() {
        return active;
    }

    // Returns the last turn that the Activatable was activated. This is for checking if two Activators (which all must be Activatables) were activated on the same turn to check the CO_AND condition.
    public int getLastActivatedTick() {
        return lastActivatedTick;
    }

    // This returns the unique entity ID of an activator.
    public int getActivatorId(Activator activator) {
        Entity activatorEntity = (Entity)activator;
        return activatorEntity.getId();
    }

    // This returns what Activators are responsible for the current Activator to be active (which Activators met the activate condition OR, AND, XOR, CO_AND for the current activatable)
    public ArrayList<Integer> getParentActivators() {
        return parentActivators;
    }

    // Sets the active status of the Activatable manually
    public void setActive(boolean active) {
        this.active = active;
    }

    
}
 