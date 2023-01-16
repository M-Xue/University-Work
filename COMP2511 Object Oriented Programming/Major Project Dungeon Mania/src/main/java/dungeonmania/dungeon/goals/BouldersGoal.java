package dungeonmania.dungeon.goals;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Switch;

public class BouldersGoal implements GoalComponent {
    private final Dungeon dungeon;

    public BouldersGoal(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    @Override
    public boolean isComplete() {
        for (Entity e : dungeon.getEntities()) {
            if (e instanceof Switch) {
                Switch s = (Switch)e;
                if (!s.isCovered()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return ":boulders";
    }
}
