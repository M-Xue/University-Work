package dungeonmania.entities;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.entities.behaviours.Interactable;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Position;

abstract public class Entity implements Cloneable {
    private final String type;
    private int id;
    private Position position;
    
    private Dungeon dungeon;

    public Entity(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdString() {return Integer.toString(id);}

    public String getType() {return type;}

    public EntityResponse getResponse() {
        boolean isInteractable = this instanceof Interactable && ((Interactable) this).isInteractable();

        return new EntityResponse(
                getIdString(),
                getType(),
                getPosition(),
                isInteractable
        );
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    //* This function should be overridden by all entities with the potential to block.
    public boolean isBlocking() {
        return false;
    }

    @Override
    public Entity clone() {
        try {
            return (Entity) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
