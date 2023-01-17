package dungeonmania.entities;

import dungeonmania.entities.behaviours.Triggerable;
import dungeonmania.entities.moving.Mercenary;
import dungeonmania.entities.player.Player;
import dungeonmania.util.Position;


import org.json.JSONObject;

public class Portal extends Entity implements Triggerable {
    private final String colour;

    public Portal(int id, Position position, String colour) {
        super(id, "portal");
        setPosition(position);
        this.colour = colour;
    }

    public Portal(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(
                id,
                new Position(entityJSON.getInt("x"), entityJSON.getInt("y")),
                entityJSON.getString("colour")
        );
    }

    public String getColour() {
        return colour;
    }

    @Override
    public void onTrigger(Entity other) {
        if (other instanceof Mercenary || other instanceof Player) {
            Position targePortalPosition = null;
            for (Portal portal : this.getDungeon().getPortals().get(this.colour)) {
                if (!this.equals(portal)) {
                    targePortalPosition = portal.getPosition();
                }
            }

            if (targePortalPosition != null) {
                if (this.getDungeon().getCardinallyAdjacentOpenPositions(targePortalPosition).size() > 0) {
                    this.getDungeon().moveEntity(this.getDungeon().getCardinallyAdjacentOpenPositions(targePortalPosition).get(0), other);
                }
                
            }
        }
    }

    @Override
    public boolean isBlocking() {
        Position targePortalPosition = null;
        for (Portal portal : this.getDungeon().getPortals().get(this.colour)) {
            if (!this.equals(portal)) {
                targePortalPosition = portal.getPosition();
            }
        }
        if (this.getDungeon().getCardinallyAdjacentOpenPositions(targePortalPosition).size() > 0) {
            return false;
        }

        return true;
    }
    public String getType() {
        return "portal_" + colour.toLowerCase();
    }
}
