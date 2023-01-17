package dungeonmania.dungeon;

import java.io.FileWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.util.FileLoader;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Position;

public class DungeonBuilder {

    public DungeonBuilder() {

    }

    /**
     * Generate a dungeon maze using randomised prims algorithm.
     * Maxe represented by 2D arraylist, true for path, false for wall.
     * 
     * @param width
     * @param height
     * @return
     */
    public static List<List<Boolean>> generateMaze(int width, int height) {
        // initialise 2D arraylist representation of maze, all walls.j
        List<List<Boolean>> maze = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            List<Boolean> row = new ArrayList<>();
            for (int j = 0; j < width; j++) {
                row.add(false);
            }
            maze.add(row);
        }

        // top left corner is start, init as path
        Position start = new Position(0, 0);
        // set start to path
        maze.get(start.getY()).set(start.getX(), true);

        List<Position> options = new ArrayList<>();
        options.addAll(getNeighbouringPositions(maze, start, width, height, false, 2));

        while (options.size() > 0) {
            // get random option
            int randomIndex = (int) (Math.random() * options.size());
            // remove option from list
            Position next = options.remove(randomIndex);

            List<Position> neighbouringPaths = getNeighbouringPositions(maze, next, width, height, true, 2);

            if (neighbouringPaths.size() > 0) {
                // get random neighbouring path
                int randomPathIndex = (int) (Math.random() * neighbouringPaths.size());
                Position neighbouringPath = neighbouringPaths.get(randomPathIndex);

                // set next to path
                maze.get(next.getY()).set(next.getX(), true);
                // set intermediary between next and neighbouringPath to path
                Position intermediary = new Position((int) ((next.getX() + neighbouringPath.getX()) / 2),
                (int) ((next.getY() + neighbouringPath.getY()) / 2));
                maze.get(intermediary.getY()).set(intermediary.getX(), true);
                maze.get(neighbouringPath.getY()).set(neighbouringPath.getX(), true);
                // neighbouringPath is already a path, no need to set.

                // add all neighbouring walls of next to options
                options.addAll(getNeighbouringPositions(maze, next, width, height, false, 2));
            }
        }

        // connect exit to the rest of the generated paths.
        Position end = new Position(width - 1, height - 1);
        if (maze.get(end.getY()).get(end.getX()) == false) {
            // if end is a wall, make it a path.
            maze.get(end.getY()).set(end.getX(), true);

            // for all neighbours of end, if all walls, then make a
            // random one into a path.
            List<Position> neighbouringWalls = getNeighbouringPositions(maze, end, width, height, false, 1);
            if (neighbouringWalls.stream()
                    .allMatch(predicate -> maze.get(predicate.getY()).get(predicate.getX()) == false)) {
                // if all neighbouring walls are walls, make one into path
                int randomWallIndex = (int) (Math.random() * neighbouringWalls.size());
                Position neighbouringWall = neighbouringWalls.get(randomWallIndex);
                maze.get(neighbouringWall.getY()).set(neighbouringWall.getX(), true);
            }
        }

        return maze;
    }

    public static Dungeon generateDungeon(int xStart, int yStart, int xEnd, int yEnd, String configName) {
        int width = xEnd + 1- xStart;
        int height = yEnd + 1 - yStart;
        List<List<Boolean>> unboundedMaze = generateMaze(width, height);
        List<List<Boolean>> boundedMaze = new ArrayList<>();
        List<Boolean> list=new ArrayList<>(Arrays.asList(new Boolean[width+2]));
        Collections.fill(list, Boolean.FALSE);
        boundedMaze.add(list);
        for (int i = 0; i < height; i++) {
            // Add walls to either end of row
            List<Boolean> row = unboundedMaze.get(i);
            row.add(0, false);   
            row.add(false);   
            boundedMaze.add(row);
        }
        boundedMaze.add(list);

        JSONObject dungeonJSON = new JSONObject();
        JSONArray entities = new JSONArray();

        JSONObject player = new JSONObject();
        player.put("x", xStart);
        player.put("y", yStart);
        player.put("type", "player");
        entities.put(player);
        JSONObject exit = new JSONObject();
        exit.put("x", xEnd);
        exit.put("y", yEnd);
        exit.put("type", "exit");
        entities.put(exit);
        for (int row = 0; row < boundedMaze.size(); row++) {
            for (int col = 0; col < boundedMaze.get(row).size(); col++) {
                if (boundedMaze.get(row).get(col) == false) {
                    JSONObject wall = new JSONObject();
                    wall.put("x", xStart - 1 + col);
                    wall.put("y", yStart - 1 + row);
                    wall.put("type", "wall");
                    entities.put(wall);
                } 
            }
        }

        dungeonJSON.put("entities", entities);

        JSONObject goalCondition = new JSONObject();
        goalCondition.put("goal", "exit");
        dungeonJSON.put("goal-condition", goalCondition);
        
        String dungeonName = "generated_dungeon_" + configName;
        return new Dungeon(dungeonName, dungeonJSON, configName, new Random().nextLong());
    }

    /**
     * Get a list of walls or paths cardinally adjacent to and `dist` steps away
     * from `position` that are not outside of the boundaries.
     * 
     * @param maze
     * @param position
     * @param width
     * @param height
     * @param isPath
     * @param dist
     * @return
     */
    public static List<Position> getNeighbouringPositions(List<List<Boolean>> maze, Position pos, int width,
            int height, boolean isPath, int dist) {
        List<Position> candidates = new ArrayList<>();
        candidates.add(new Position(pos.getX() - dist, pos.getY()));
        candidates.add(new Position(pos.getX() + dist, pos.getY()));
        candidates.add(new Position(pos.getX(), pos.getY() - dist));
        candidates.add(new Position(pos.getX(), pos.getY() + dist));
        List<Position> neighbours = new ArrayList<>();
        for (Position candidate : candidates) {
            if (candidate.getX() >= 0 && candidate.getX() < width && candidate.getY() >= 0
                    && candidate.getY() < height) {
                if (isPath) {
                    if (maze.get(candidate.getY()).get(candidate.getX()) == true) {
                        neighbours.add(candidate);
                    }
                } else {
                    if (maze.get(candidate.getY()).get(candidate.getX()) == false) {
                        neighbours.add(candidate);
                    }
                }
            }
        }
        return neighbours;
    }

    public static void debugPrintMaze(List<List<Boolean>> maze) {
        for (List<Boolean> row : maze) {
            for (Boolean cell : row) {
                System.out.print(cell ? " " : "X");
            }
            System.out.println();
        }
        System.out.println();

    }

    public static void main(String[] args) {
        List<List<Boolean>> maze = generateMaze(11,11);
        debugPrintMaze(maze);
        maze = generateMaze(12,12);
        debugPrintMaze(maze);
        maze = generateMaze(12, 11);
        debugPrintMaze(maze);
    }

}
