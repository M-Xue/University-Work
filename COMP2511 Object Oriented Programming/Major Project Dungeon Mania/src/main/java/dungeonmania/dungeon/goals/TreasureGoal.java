package dungeonmania.dungeon.goals;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.entities.collectable.SunStone;
import dungeonmania.entities.collectable.Treasure;

public class TreasureGoal implements GoalComponent {
    private final Dungeon dungeon;

    public TreasureGoal(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    @Override
    public boolean isComplete() {
        if (this.dungeon.getConfigJSON().getInt("treasure_goal") <= this.dungeon.getPlayer().countItems(Treasure.class) + this.dungeon.getPlayer().countItems(SunStone.class)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return ":treasure";
    }
}
