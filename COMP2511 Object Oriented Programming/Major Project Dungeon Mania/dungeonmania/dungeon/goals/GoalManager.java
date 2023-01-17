package dungeonmania.dungeon.goals;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.dungeon.Dungeon;


public class GoalManager {
    private final GoalComponent topGoal;
    private final Dungeon dungeon;

    public GoalManager(JSONObject goalsObj, Dungeon dungeon) throws IllegalArgumentException {
        this.dungeon = dungeon;
        this.topGoal = this.addGoalsRecursive(goalsObj);
    }

    private GoalComponent addGoalsRecursive(JSONObject goalsObj) {
        if (goalsObj.has("subgoals")) {
            JSONArray subgoalsArr = goalsObj.getJSONArray("subgoals");
            String operator = goalsObj.getString("goal");
            if (operator.equals("AND")) {
                return new AndGoal(
                        addGoalsRecursive(subgoalsArr.getJSONObject(0)),
                        addGoalsRecursive(subgoalsArr.getJSONObject(1))
                );
            } else if (operator.equals("OR")) {
                return new OrGoal(
                        addGoalsRecursive(subgoalsArr.getJSONObject(0)),
                        addGoalsRecursive(subgoalsArr.getJSONObject(1))
                );
            }
        } else {
            String goal = goalsObj.getString("goal");
            switch (goal) {
                case "exit":
                    return new ExitGoal(this.dungeon);
                case "boulders":
                    return new BouldersGoal(this.dungeon);
                case "treasure":
                    return new TreasureGoal(this.dungeon);
                case "enemies":
                    return new EnemiesGoal(this.dungeon);
            }
        }
        throw new IllegalArgumentException("Unknown goal type");
    }
    
    public String getGoalsResString() {
        if (topGoal.isComplete()) {
            return "";
        } else {
            return topGoal.toString();
        }
    }
}



















// private final ArrayList<Goal> goals;
    // private final Supergoal supergoal;

// public GoalManager(JSONObject goalsObj, Dungeon dungeon) throws IllegalArgumentException {

    // goals = new ArrayList<>();

    // if (goalsObj.has("subgoals")) {
    //     // Subgoals exist
    //     supergoal = Supergoal.fromString(goalsObj.getString("goal"));

    //     JSONArray subgoalsArr = goalsObj.getJSONArray("subgoals");
    //     for (int i = 0; i < subgoalsArr.length(); i++) {
    //         JSONObject subgoalObj = subgoalsArr.getJSONObject(i);
    //         goals.add(Goal.fromString(subgoalObj.getString("goal")));
    //     }
    // } else {
    //     supergoal = null;
    //     goals.add(Goal.fromString(goalsObj.getString("goal")));
    // }
    
// }

// public ArrayList<Goal> getGoals() {
    //     return goals;
    // }

    // public Supergoal getSupergoal() {
    //     return supergoal;
    // }