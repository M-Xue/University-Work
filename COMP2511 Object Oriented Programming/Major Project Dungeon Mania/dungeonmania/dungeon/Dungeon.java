package dungeonmania.dungeon;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import dungeonmania.battle.Battle;
import dungeonmania.dungeon.actions.*;
import dungeonmania.entities.*;
import dungeonmania.entities.behaviours.Incrementable;
import dungeonmania.entities.collectable.TimeTurner;
import dungeonmania.entities.player.Player;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.dungeon.goals.GoalManager;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;
import org.json.*;

import dungeonmania.response.models.DungeonResponse;

/**
 * @author Jason,
 * @version 1.0
 */

public class Dungeon {
    private final UUID id;
    private final String dungeonName;
    private final String configName;
    private final JSONObject configJSON;

    private final MobSpawn mobSpawn;
    private final long randomSeed;
    private final Random randomizer;
    private final JSONObject dungeonJSON;

    private DungeonMap dungeonMap;
    private GoalManager goalManager;

    private int tickCount = 0;
    private int activatableSpreadCount = 0;

    public Dungeon(String dungeonName, String configName) throws IllegalArgumentException {
        this(dungeonName, loadDungeonJSON(dungeonName), configName, new Random().nextLong());
    }

    public Dungeon(String dungeonName, JSONObject dungeonJSON, String configName, long randomSeed) {
        this.id = UUID.randomUUID();
        this.dungeonName = dungeonName;

        this.configName = configName;
        try {
            configJSON = new JSONObject(FileLoader.loadResourceFile("/configs/" + configName + ".json"));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        this.mobSpawn = new MobSpawn(this, configJSON);

        this.randomSeed = randomSeed;
        randomizer = new Random(randomSeed);

        this.dungeonJSON = dungeonJSON;

        initialise();
    }

    private static JSONObject loadDungeonJSON(String dungeonName) {
        try {
            return new JSONObject(FileLoader.loadResourceFile("/dungeons/" + dungeonName + ".json"));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void initialise() {
        dungeonMap = new DungeonMap(this);

        // Add entities
        JSONArray entitiesArr = dungeonJSON.getJSONArray("entities");
        for (int i = 0; i < entitiesArr.length(); i++) {
            JSONObject entityJSON = entitiesArr.getJSONObject(i);
            int entityId = generateEntityId();
            Entity entity = EntityFactory.createEntity(entityId, entityJSON, configJSON);
            addEntity(entity);
        }

        // Add goals
        JSONObject goalsObj = dungeonJSON.getJSONObject("goal-condition");
        goalManager = new GoalManager(goalsObj, this);
    }

    public DungeonResponse getResponse() {
        return new DungeonResponse(
                id.toString(),
                dungeonName,
                dungeonMap.getEntityResponses(),
                getPlayer().getInventoryResponse(),
                getPlayer().getBattleResponses(),
                getPlayer().getBuildableNames(),
                goalManager.getGoalsResString());
    }

    public UUID getId() {
        return id;
    }

    public String getDungeonName() {
        return dungeonName;
    }

    public JSONObject getConfigJSON() {
        return configJSON;
    }

    public Random getRandomizer() {
        return randomizer;
    }

    /**
     * Gets how many times the activatable spread function has iterated through the while loop.
     * This is necessary to check that a XOR Activator doesn't activate too early.
     * @return
     */
    public int getActivatableSpreadCount() {
        return activatableSpreadCount;
    }
    public void incrementActivatableSpreadCount() {
        this.activatableSpreadCount++;
    }

    // Actions ================================================================================

    /*
        1. Use item
        2. Interact will all entities on tile (there shouldn't be anything to interact with since you should have already interacted with them when you moved onto the tile)
            Edge case: maybe something spawned on the same tile at the player
        3. Increment all entities
            When we increment entities, they should dynamically change their behaviour so any items the player has used should immediatly take effect on the movement stratagies on relevant enemies.
        4. Goal observer
     */
    public void tick(String itemUsedId) throws IllegalArgumentException, InvalidActionException {
        // this tick is for if the player spends the turn using an item (check DungeonManiaController)
        getPlayer().useItem(itemUsedId);
        increment();

        this.tickCount++;
    }

    public void tick(Direction movementDirection) {
        // Move player into its new position
        // We check for blocking in the player.move() method
        getPlayer().move(movementDirection);
        increment();
        
        this.tickCount++;
    }

    // Returns how many turns have passed since the start of the game
    public int getTickCount() {
        return tickCount;
    }

    public void interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        getPlayer().interact(entityId);
    }

    public void build(String buildableType) throws IllegalArgumentException, InvalidActionException {
        getPlayer().build(buildableType);
    }

    public void saveGame(String name) {
        JSONObject data = new JSONObject();
        data.put("dungeon_name", dungeonName);
        data.put("dungeon_data", dungeonJSON);
        data.put("config_name", configName);
        data.put("random_seed", randomSeed);

        JSONArray playerActions = new JSONArray();
        for (PlayerAction action : getPlayer().getActions()) {
            playerActions.put(action.toJSON());
        }
        data.put("player_actions", new JSONArray(playerActions));

        try {
            Path folderPath = Paths.get("saves");
            if (!Files.exists(folderPath)) {
                Files.createDirectory(folderPath);
            }

            Path filepath = Paths.get(String.format("saves/%s.json", name));
            Files.writeString(filepath, data.toString(4));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Dungeon loadGame(String name) throws IllegalArgumentException {
        try {
            Path filepath = Paths.get(String.format("saves/%s.json", name));
            JSONObject data = new JSONObject(Files.readString(filepath));

            String dungeonName = data.getString("dungeon_name");
            JSONObject dungeonJSON = data.getJSONObject("dungeon_data");
            String configName = data.getString("config_name");
            long randomSeed = data.getLong("random_seed");
            JSONArray playerActions = data.getJSONArray("player_actions");

            Dungeon dungeon = new Dungeon(dungeonName, dungeonJSON, configName, randomSeed);
            dungeon.simulate(playerActions);

            return dungeon;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cannot load save file " + name);
        }
    }

    public static List<String> allGames() {
        ArrayList<String> result = new ArrayList<>();
        File[] files = new File("saves").listFiles();

        assert files != null;
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.toLowerCase().endsWith(".json") && file.isFile()) {
                result.add(fileName.substring(0, fileName.length() - 5));
            }
        }

        return result;
    }

    public void rewind(int ticks, boolean isConsumeTimeTurner) throws IllegalArgumentException {
        Player player = getPlayer();
        List<PlayerAction> playerActions = player.getActions();

        if (ticks <= 0) {
            throw new IllegalArgumentException("The rewind ticks should be greater than 0");
        } else if (ticks > playerActions.size()) {
            throw new IllegalArgumentException("The number of ticks have not occurred yet");
        }

        List<PlayerAction> simulateActions = playerActions.subList(0, playerActions.size() - ticks);
        List<PlayerAction> actionsQueue = playerActions.subList(playerActions.size() - ticks, playerActions.size());

        initialise();
        simulate(simulateActions);

        Player dummyPlayer = getPlayer();
        dummyPlayer.setActionsQueue(actionsQueue);
        dummyPlayer.setIsDummy(true);
        if (!isConsumeTimeTurner) {
            dummyPlayer.cancelMove();
        }

        List<PlayerAction> keepActions = new ArrayList<>(simulateActions);
        keepActions.add(new RewindAction(player.getPosition(), actionsQueue, isConsumeTimeTurner));
        player.setActions(keepActions);

        player.setId(generateEntityId());
        addEntity(player);

        if (isConsumeTimeTurner) {
            player.removeItem(TimeTurner.class, 1);
        }
    }

    // Others =================================================================================

    public void increment() {
        List<Player> players = dungeonMap.getEntities().stream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .collect(Collectors.toList());
        players.forEach(Player::increment);

        List<Incrementable> incrementables = dungeonMap.getEntities().stream()
                .filter(entity -> !(entity instanceof Player))
                .filter(Incrementable.class::isInstance)
                .map(entity -> (Incrementable) entity)
                .collect(Collectors.toList());
        //incrementables.forEach(Incrementable::increment);
        for (Incrementable i : incrementables) {
            i.increment();
        }

        mobSpawn.tick();
    }

    public void addBattle(Battle battle) {
        getPlayer().addBattle(battle);
    }

    public Grid generateGrid(Entity entity) {
        return new Grid(dungeonMap, entity);
    }

    private void simulate(JSONArray playerActions) {
        playerActions.forEach(o -> {
            PlayerAction action = PlayerAction.fromJSON((JSONObject) o);
            action.execute(getPlayer());
            if (!(action instanceof RewindAction)) {
                increment();
            }
        });
    }

    private void simulate(List<PlayerAction> playerActions) {
        playerActions.forEach(action -> {
            action.execute(getPlayer());
            if (!(action instanceof RewindAction)) {
                increment();
            }
        });
    }

    // DungeonMap =============================================================================

    public int generateEntityId() {
        return dungeonMap.generateEntityId();
    }

    public Entity getEntity(int entityId) {
        return dungeonMap.getEntity(entityId);
    }

    public Collection<Entity> getEntities() {
        return dungeonMap.getEntities();
    }

    public void addEntity(Entity entity) {
        dungeonMap.addEntity(entity);
    }

    public void removeEntity(Entity entity) {
        dungeonMap.removeEntity(entity);
    }

    public void moveEntity(Position destination, Entity entity) {
        dungeonMap.moveEntity(destination, entity);
    }

    public void moveEntity(Direction direction, Entity entity) {
        dungeonMap.moveEntity(direction, entity);
    }

    public boolean hasEntityOnTile(Position position, Class<?> entityClass) {
        return dungeonMap.hasEntityOnTile(position, entityClass);
    }

    public List<Entity> getEntitiesOnTile(Position position) {
        return dungeonMap.getEntitiesOnTile(position);
    }

    public <T> T getFirstEntityOnTile(Position position, Class<T> entityClass) {
        return dungeonMap.getFirstEntityOnTile(position, entityClass);
    }

    public boolean isOpenTile(Position position) {
        return dungeonMap.isOpenTile(position);
    }

    public List<Position> getCardinallyAdjacentOpenPositions(Position position) {
        return dungeonMap.getCardinallyAdjacentOpenPositions(position);
    }

    public Player getPlayer() {
        return dungeonMap.getPlayer();
    }

    public HashMap<String, ArrayList<Portal>> getPortals() {
        return dungeonMap.getPortals();
    }
}
