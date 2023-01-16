package dungeonmania.dungeon;

import dungeonmania.entities.Entity;
import dungeonmania.entities.SwampTile;
import dungeonmania.util.Position;

import java.util.List;

public class Grid {
    private final char[][] grid;
    private final int minX;
    private final int maxX;
    private final int minY;
    private final int maxY;
    private int movementFactor = 0;

    public Grid(DungeonMap dungeonMap, Entity entity) {
        int minX = entity.getPosition().getX();
        int maxX = minX;
        int minY = entity.getPosition().getY();
        int maxY = minY;

        for (Position pos : dungeonMap.getUsedPositions()) {
            if (pos.getX() < minX)
                minX = pos.getX();
            if (pos.getX() > maxX)
                maxX = pos.getX();
            if (pos.getY() < minY)
                minY = pos.getY();
            if (pos.getY() > maxY)
                maxY = pos.getY();
        }

        int arrX = (maxX - minX) + 5;
        int arrY = (maxY - minY) + 5;
        char[][] arr = new char[arrY][arrX];

        // initialize array with 1 for edges and 0 for empty add in obstacles
        for (int x = 0; x < arrX; x++) {
            arr[0][x] = 'e';
            arr[arrY - 1][x] = 'e';
        }
        for (int y = 0; y < arrY; y++) {
            arr[y][0] = 'e';
            arr[y][arrX - 1] = 'e';
        }

        for (Entity e : dungeonMap.getEntities()) {
            // add in logic for door to see if its open or closed
            //System.out.println(e.getType().toString());
            if (e.getType().equals("boulder") || e.getType().equals("wall")) {
                arr[e.getPosition().getY() + 2 - minY][e.getPosition().getX() + 2 - minX] = 'o';
            } else if (e.getType().contains("portal")) {
                arr[e.getPosition().getY() + 2 - minY][e.getPosition().getX() + 2 - minX] = 'p';
                //System.out.println(e.toString());
            } else if (e.getType().equals("player")) {
                arr[e.getPosition().getY() + 2 - minY][e.getPosition().getX() + 2 - minX] = 'h';
            } else if (e.getType().equals("mercenary")) {
                arr[e.getPosition().getY() + 2 - minY][e.getPosition().getX() + 2 - minX] = 'm';
            } else if (e.getType().equals("swamp")) {
                arr[e.getPosition().getY() + 2 - minY][e.getPosition().getX() + 2 - minX] = 's';
                this.movementFactor = ((SwampTile) e).getMovementFactor();
            } else if (e.getType().equals("door")) {
                if(e.isBlocking()) arr[e.getPosition().getY() + 2 - minY][e.getPosition().getX() + 2 - minX] = 'o';
            }
        }
        // quick debug print of array
//        System.out.println(Arrays.deepToString(arr).replace("], ", "\n").replace("[[", "").replace("]]", "").replace("[", ""));
//        System.out.println("");
        this.grid = arr;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }


    public char[][] getGrid() {
        return grid;
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMovementFactor() {
        return movementFactor;
    }
}
