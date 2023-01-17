package dungeonmania.entities;

import dungeonmania.entities.actives.*;
import dungeonmania.entities.buildable.*;
import dungeonmania.entities.collectable.*;
import dungeonmania.entities.collectable.potions.*;
import dungeonmania.entities.moving.*;
import dungeonmania.entities.player.Player;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;


public class EntityFactory {
    private static final HashMap<String, Class<? extends Entity>> normalEntities = new HashMap<>();
    private static final HashMap<String, Class<? extends MovingEntity>> movingEntities = new HashMap<>();
    private static final HashMap<String, Class<? extends CollectableEntity>> collectableEntities = new HashMap<>();
    private static final HashMap<String, Class<? extends BuildableEntity>> buildableEntities = new HashMap<>();

    static {
        normalEntities.put("player", Player.class);
        normalEntities.put("boulder", Boulder.class);
        normalEntities.put("door", Door.class);
        normalEntities.put("switch", Switch.class);
        normalEntities.put("exit", Exit.class);
        normalEntities.put("wall", Wall.class);
        normalEntities.put("portal", Portal.class);
        normalEntities.put("zombie_toast_spawner", ZombieToastSpawner.class);
        normalEntities.put("swamp_tile", SwampTile.class);
        normalEntities.put("wire", Wire.class);
        normalEntities.put("light_bulb_off", Lightbulb.class);
        normalEntities.put("switch_door", SwitchDoor.class);
        normalEntities.put("time_travelling_portal", TimeTravellingPortal.class);
        normalEntities.put("swamp", SwampTile.class);

        // Moving Entities
        movingEntities.put("mercenary", Mercenary.class);
        movingEntities.put("spider", Spider.class);
        movingEntities.put("zombie_toast", ZombieToast.class);
        movingEntities.put("assassin", Assassin.class);
        movingEntities.put("hydra", Hydra.class);

        // Collectable Entities
        collectableEntities.put("treasure", Treasure.class);
        collectableEntities.put("key", Key.class);
        collectableEntities.put("invincibility_potion", InvincibilityPotion.class);
        collectableEntities.put("invisibility_potion", InvisibilityPotion.class);
        collectableEntities.put("wood", Wood.class);
        collectableEntities.put("arrow", Arrow.class);
        collectableEntities.put("bomb", Bomb.class);
        collectableEntities.put("sword", Sword.class);
        collectableEntities.put("sun_stone", SunStone.class);
        collectableEntities.put("time_turner", TimeTurner.class);

        // Buildable Entities
        buildableEntities.put("bow", Bow.class);
        buildableEntities.put("shield", Shield.class);
        buildableEntities.put("sceptre", Sceptre.class);
        buildableEntities.put("midnight_armour", MidnightArmour.class);
    }

    /**
     * Expects entityJSON to have a `type` field at the very least.
     * The String value of `type` key will be used to find the
     * corresponding Entity constructor.
     * @param entityJSON
     * @param configJSON
     * @return The Entity to be created.
     */
    public static Entity createEntity(int id, JSONObject entityJSON, JSONObject configJSON) throws IllegalArgumentException {
        String type = entityJSON.getString("type");
        try {
            return getEntityClass(type)
                    .getConstructor(Integer.TYPE, JSONObject.class, JSONObject.class)
                    .newInstance(id, entityJSON, configJSON);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Class<? extends Entity> getEntityClass(String type) {
        Class<? extends Entity> entity;
        entity = getNormalEntityClass(type);
        if (entity == null) entity = getMovingEntityClass(type);
        if (entity == null) entity = getCollectableEntityClass(type);
        if (entity == null) entity = getBuildableEntityClass(type);
        return entity;
    }

    public static Class<? extends Entity> getNormalEntityClass(String type) {
        return normalEntities.get(type);
    }

    public static Class<? extends MovingEntity> getMovingEntityClass(String type) {
        return movingEntities.get(type);
    }

    public static Class<? extends CollectableEntity> getCollectableEntityClass(String type) {
        return collectableEntities.get(type);
    }

    public static Class<? extends BuildableEntity> getBuildableEntityClass(String type) {
        return buildableEntities.get(type);
    }

    public static Collection<Class<? extends BuildableEntity>> getBuildableClasses() {
        return buildableEntities.values();
    }
}
