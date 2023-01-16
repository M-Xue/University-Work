package dungeonmania.entities.actives;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.entities.Entity;
import dungeonmania.util.Position;
import dungeonmania.util.PositionExt;

public class Wire extends Activator  {
    public Wire(int id, Position position, String type) {
        super(id, type, "or");
        setPosition(position);
    } 

    public Wire(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, new Position(entityJSON.getInt("x"), entityJSON.getInt("y")), entityJSON.getString("type"));
    } 
}
