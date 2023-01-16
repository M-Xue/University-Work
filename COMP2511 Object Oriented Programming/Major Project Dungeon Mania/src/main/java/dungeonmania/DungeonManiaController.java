package dungeonmania;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.dungeon.DungeonBuilder;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;

import java.util.ArrayList;
import java.util.List;

public class DungeonManiaController {
    private Dungeon currentDungeon;

    public String getSkin() {
        return "default";
    }

    public String getLocalisation() {
        return "en_US";
    }

    /**
     * /dungeons
     */
    public static List<String> dungeons() {
        return FileLoader.listFileNamesInResourceDirectory("dungeons");
    }

    /**
     * /configs
     */
    public static List<String> configs() {
        return FileLoader.listFileNamesInResourceDirectory("configs");
    }

    /**
     * /game/new
     */
    public DungeonResponse newGame(String dungeonName, String configName) throws IllegalArgumentException {
        Dungeon dungeon = new Dungeon(dungeonName, configName);
        this.currentDungeon = dungeon;
        return dungeon.getResponse();
    }

    /**
     * /game/dungeonResponseModel
     */
    public DungeonResponse getDungeonResponseModel() {
        return currentDungeon.getResponse();
    }

    /**
     * /game/tick/item
     */
    public DungeonResponse tick(String itemUsedId) throws IllegalArgumentException, InvalidActionException {
        currentDungeon.tick(itemUsedId);
        return currentDungeon.getResponse();
    }

    /**
     * /game/tick/movement
     */
    public DungeonResponse tick(Direction movementDirection) {
        currentDungeon.tick(movementDirection);
        return currentDungeon.getResponse();
    }

    /**
     * /game/build
     */
    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        currentDungeon.build(buildable);
        return currentDungeon.getResponse();
    }

    /**
     * /game/interact
     */
    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        currentDungeon.interact(entityId);
        return currentDungeon.getResponse();
    }

    /**
     * /game/save
     */
    public DungeonResponse saveGame(String name) throws IllegalArgumentException {
        currentDungeon.saveGame(name);
        return currentDungeon.getResponse();
    }

    /**
     * /game/load
     */
    public DungeonResponse loadGame(String name) throws IllegalArgumentException {
        currentDungeon = Dungeon.loadGame(name);
        return currentDungeon.getResponse();
    }

    /**
     * /games/all
     */
    public List<String> allGames() {
        return Dungeon.allGames();
    }

    /**
     * /api/game/rewind
     */
    public DungeonResponse rewind(int ticks) throws IllegalArgumentException {
        currentDungeon.rewind(ticks, true);
        return currentDungeon.getResponse();
    }

    /**
     * /api/game/new/generate
     */
    public DungeonResponse generateDungeon(int xStart, int yStart, int xEnd, int yEnd, String configName) {
        Dungeon dungeon = DungeonBuilder.generateDungeon(xStart, yStart, xEnd, yEnd, configName);
        this.currentDungeon = dungeon;
        return dungeon.getResponse();
    }

}
