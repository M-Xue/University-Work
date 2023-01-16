package dungeonmania.util;

import dungeonmania.dungeon.Grid;
import dungeonmania.entities.Portal;

import java.util.*;

public class PathFinding {

    public static Position toGrid(Position pos, Grid grid) {
        return new Position(pos.getX()+2-grid.getMinX(), pos.getY()+2-grid.getMinY());
    }

    public static Position toTrue(Position pos, Grid grid) {
        return new Position(pos.getX()+grid.getMinX()-2, pos.getY()+grid.getMinY()-2);
    }

    public static Position getNextMove(Grid grid, HashMap<String, ArrayList<Portal>> portals, Position start, Position dest) {
        char[][] arr = grid.getGrid();
        Position startPos = toGrid(start,grid);
        Position destPos = toGrid(dest,grid);
        HashMap<Position, Position> prev = new HashMap<>();
        HashMap<Position, Integer> dist = new HashMap<>();
        HashMap<Position, Boolean> swamp = new HashMap<>();
        int movement_factor = grid.getMovementFactor();

        PriorityQueue<PositionDist> q = new PriorityQueue<PositionDist>();
        
        for (int y = 0; y < arr.length; y++) {
            for (int x = 0; x < arr[0].length; x++) {
                Position p = new Position(x, y);
                if (arr[y][x] == 'e'|| arr[y][x] == 'o') {
                    dist.put(p, Integer.MAX_VALUE);
                    prev.put(p, null);
                }
                if (arr[y][x] == 's') swamp.put(p, true);    
            }
        }

        dist.put(startPos, 0);
        prev.put(startPos, null);
        q.add(new PositionDist(startPos, 0));
        

        while(!q.isEmpty()) {

            PositionDist a = q.poll();
            int aX = a.getPos().getX();
            int aY = a.getPos().getY();
            if(aX==destPos.getX() && aY==destPos.getY()) break;

            Position up = new Position(aX, aY-1);
            Position down = new Position(aX, aY+1);
            Position left = new Position(aX-1, aY);
            Position right = new Position(aX+1, aY);           

            List <Position> neigh = new ArrayList<Position>();
            neigh.add(up);    neigh.add(down);    neigh.add(left);    neigh.add(right);

            for(Position p : neigh) {
                Position adj = checkPortal(p, portals, a.getPos(), grid, prev);
                if(!prev.containsKey(adj)) {
                    int total = dist.get(a.getPos()) + ((swamp.containsKey(adj))?movement_factor+1:1);                   
                    dist.put(adj, total);
                    prev.put(adj, a.getPos());
                    q.add(new PositionDist(adj, total));
                }
            }
        }
        return backtrack(prev, destPos, startPos, grid);
    }


    private static Position checkPortal(Position pos, HashMap<String, ArrayList<Portal>> pMap, Position original, Grid grid, HashMap<Position, Position> v) {
        for (List<Portal> q : pMap.values()) {
            Position a = toGrid(q.get(0).getPosition(), grid);
            Position b = toGrid(q.get(1).getPosition(), grid);

            if (a.getX() == pos.getX() && a.getY() == pos.getY()) {
                Position c = new Position((pos.getX()-original.getX()+b.getX()), (pos.getY()-original.getY()+b.getY()));
                if(v.containsKey(c)) return pos;
                else return c;
            }
            if (b.getX() == pos.getX() && b.getY() == pos.getY()) {
                Position c = new Position((pos.getX()-original.getX()+a.getX()), (pos.getY()-original.getY()+a.getY()));
                if(v.containsKey(c)) return pos;
                else return c;
            }

        }
        return pos;
    }

    private static Position backtrack(HashMap<Position, Position> prev, Position destPos, Position startPos, Grid grid) {
        if(prev.get(destPos) == null) return toTrue(startPos, grid);            
        if (prev.get(destPos).getX() == startPos.getX() && prev.get(destPos).getY() == startPos.getY()) {
            return toTrue(destPos, grid);
        } else return backtrack(prev, prev.get(destPos), startPos, grid);


    }
}
