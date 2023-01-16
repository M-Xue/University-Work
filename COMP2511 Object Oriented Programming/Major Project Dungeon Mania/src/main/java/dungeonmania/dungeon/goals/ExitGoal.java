package dungeonmania.dungeon.goals;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Exit;

public class ExitGoal implements GoalComponent {
    private final Dungeon dungeon;

    public ExitGoal(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    @Override
    public boolean isComplete() {
        Exit exit = null;
        for (Entity e : this.dungeon.getEntities()) {
            if (e instanceof Exit) {
                exit = (Exit)e;
            }
        }

        if (exit != null && this.dungeon.getPlayer().getPosition().equals(exit.getPosition())) {
            return true;
        } 

        return false;
    }

    @Override
    public String toString() {
        return ":exit";
    }
}
