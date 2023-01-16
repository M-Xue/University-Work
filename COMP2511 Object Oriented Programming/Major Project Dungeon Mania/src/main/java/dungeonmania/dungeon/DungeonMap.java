package dungeonmania.dungeon;

import dungeonmania.entities.Entity;
import dungeonmania.entities.Portal;
import dungeonmania.entities.behaviours.Triggerable;
import dungeonmania.entities.player.Player;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.util.PositionExt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DungeonMap {
    private final Dungeon dungeon;

    private final HashMap<Integer, Entity> entityById = new HashMap<>();
    private final HashMap<Position, ArrayList<Entity>> entitiesByPos = new HashMap<>();
    private final HashMap<String, ArrayList<Portal>> portals = new HashMap<>();
    private Player player;

    private int entityIdTracker = 0;

    public DungeonMap(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public Collection<Entity> getEntities() {
        return entityById.values();
    }

    public Collection<Position> getUsedPositions() {
        return entitiesByPos.keySet();
    }

    /**
     * entities is a list of all entities currently in the dungeon
     * (all entities in the Player's inventory aren't included); if a
     * Player or enemy dies it is removed from this list.
     * @return
     */
    public List<EntityResponse> getEntityResponses() {
        ArrayList<EntityResponse> responses = new ArrayList<>();
        for (Entity entity : entityById.values()) {
            responses.add(entity.getResponse());
        }
        return responses;
    }

    public Entity getEntity(int entityId) {
        return entityById.get(entityId);
    }

    public Player getPlayer() {
        return player;
    }

    public List<Entity> getTile(Position position) {
        return entitiesByPos.get(position);
    }

    public boolean isOpenTile(Position position) {
        List<Entity> entityList = getTile(position);

        if (entityList != null) {
            return entityList.stream().noneMatch(Entity::isBlocking);
        } else {
            return true;
        }
    }

    public boolean hasEntityOnTile(Position position, Class<?> entityClass) {
        if (entitiesByPos.containsKey(position)) {
            ArrayList<Entity> entityList = entitiesByPos.get(position);
            return entityList.stream().anyMatch(entityClass::isInstance);
        } else {
            return false;
        }
    }

    public <T> T getFirstEntityOnTile(Position position, Class<T> entityClass) {
        List<Entity> entities = entitiesByPos.get(position);
        if (entities == null) return null;
        return entities.stream()
                .filter(entityClass::isInstance)
                .map(entityClass::cast)
                .findFirst()
                .orElse(null);
    }

    public List<Entity> getEntitiesOnTile(Position position) {
        if (entitiesByPos.containsKey(position)) {
            return entitiesByPos.get(position);
        } else {
            return List.of();
        }
    }

    public List<Position> getCardinallyAdjacentOpenPositions(Position position) {
        List<Position> cardinallyAdjacentPositions = PositionExt.getCardinallyAdjacentPositions(position);

        return cardinallyAdjacentPositions.stream()
                .filter(this::isOpenTile)
                .collect(Collectors.toList());
    }

    public void addEntity(Entity entity) {
        entity.setDungeon(dungeon);
        Position position = entity.getPosition();

        ArrayList<Entity> entityList;
        if (entitiesByPos.containsKey(position)) {
            entityList = entitiesByPos.get(position);
        } else {
            entityList = new ArrayList<>();
            entitiesByPos.put(position, entityList);
        }

        if (entity instanceof Player && !((Player) entity).isDummy()) {
            this.player = (Player) entity;
        } else if (entity instanceof Portal) {
            // Adding portals
            Portal portal = (Portal) entity;
            if (!portals.containsKey(portal.getColour())) {
                portals.put(portal.getColour(), new ArrayList<>());
                portals.get(portal.getColour()).add(portal);
            } else {
                portals.get(portal.getColour()).add(portal);
            }
        }

        entityList.add(entity);
        entityById.put(entity.getId(), entity);
    }

    public void removeEntity(Entity entity) {
        Position position = entity.getPosition();
        ArrayList<Entity> entityList = entitiesByPos.get(position);

        entityList.remove(entity);

        if (entityList.isEmpty()) {
            entitiesByPos.remove(position);
        }

        entityById.remove(entity.getId());
    }

    public void moveEntity(Position destination, Entity entity) {
        removeEntity(entity);
        entity.setPosition(destination);
        addEntity(entity);

        List<Entity> entityList = getEntitiesOnTile(destination);
        List<Triggerable> triggerables = entityList.stream()
                .filter(other -> other != entity)
                .filter(Triggerable.class::isInstance)
                .map(other -> (Triggerable) other)
                .collect(Collectors.toList());

        for (Triggerable triggerable : triggerables) {
            triggerable.onTrigger(entity);
        }
    }

    public void moveEntity(Direction direction, Entity entity) {
        this.moveEntity(entity.getPosition().translateBy(direction), entity);
    }

    public int generateEntityId() {
        int currentId = entityIdTracker;
        entityIdTracker++;
        return currentId;
    }

    public HashMap<String, ArrayList<Portal>> getPortals() {
        return portals;
    }
}
