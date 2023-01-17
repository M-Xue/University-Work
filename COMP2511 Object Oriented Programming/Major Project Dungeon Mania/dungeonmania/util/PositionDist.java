package dungeonmania.util;

public class PositionDist implements Comparable<PositionDist> {
    private Position pos;  
    private int dist = 0; 
    
    public PositionDist(Position pos, int dist) {
        this.pos = pos;
        this.dist = dist;
    }

    public Position getPos() {
        return pos;
    }

    public int getDist() {
        return dist;
    }

    public void setDist(int dist) {
        this.dist = dist;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }

    @Override
    public int compareTo(PositionDist o) {
        if(this.pos == o.pos) return 0; 
        else if(this.getDist() < o.getDist()) return -1;
        else return 1;
    }



}
