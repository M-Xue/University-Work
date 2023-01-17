package dungeonmania.dungeon.goals;

public class OrGoal implements GoalComponent {
    private final GoalComponent child1;
    private final GoalComponent child2;

    public OrGoal(GoalComponent child1, GoalComponent child2) {
        this.child1 = child1;
        this.child2 = child2;
    }

    @Override
    public boolean isComplete() {
        return (child1.isComplete() || child2.isComplete());
    }

    @Override
    public String toString() {
        if (isComplete()) {
            return "";
        } else {
            return String.format("(%s OR %s)", child1, child2);
        }
    }
}
