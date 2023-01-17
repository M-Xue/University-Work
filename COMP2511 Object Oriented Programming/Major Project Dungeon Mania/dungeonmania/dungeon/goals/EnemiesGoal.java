package dungeonmania.dungeon.goals;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.entities.Entity;
import dungeonmania.entities.ZombieToastSpawner;

public class EnemiesGoal implements GoalComponent {
    private final Dungeon dungeon;

    public EnemiesGoal(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    @Override
    public boolean isComplete() {
        for (Entity e : dungeon.getEntities()) {
            if (e instanceof ZombieToastSpawner) {
                return false;
            }
        }
        if (this.dungeon.getPlayer().getEnemiesKilled() < this.dungeon.getConfigJSON().getInt("enemy_goal")) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ":enemies";
    }
}
