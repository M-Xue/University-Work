package dungeonmania.dungeon.goals;

public class AndGoal implements GoalComponent {
    private final GoalComponent child1;
    private final GoalComponent child2;

    public AndGoal(GoalComponent child1, GoalComponent child2) {
        this.child1 = child1;
        this.child2 = child2;
    }

    @Override
    public boolean isComplete() {
        if (child1 instanceof ExitGoal && !child2.isComplete()) {
            return false;
        } else if (child2 instanceof ExitGoal && !child1.isComplete()) {
            return false;
        }
        return (child1.isComplete() && child2.isComplete());
    }

    @Override
    public String toString() {
        if (isComplete()) {
            return "";
        } else if (child1.isComplete() && !(child1 instanceof ExitGoal)) {
            return child2.toString();
        } else if (child2.isComplete() && !(child2 instanceof ExitGoal)) {
            return child1.toString();
        } else {
            return String.format("(%s AND %s)", child1, child2);
        }
    }
}
