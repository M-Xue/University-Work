package dungeonmania;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static dungeonmania.TestUtils.*;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceTests {
    private void deleteAllSaves() {
        File[] files = new File("saves").listFiles();
        for (File file : files) {
            if (file.getName().equals(".gitignore")) continue;
            file.delete();
        }
    }

    public static void assertEntityEqualsExceptId(EntityResponse expected, EntityResponse actual) {
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getPosition(), actual.getPosition());
        assertEquals(expected.isInteractable(), actual.isInteractable());
    }

    public static void assertEntitiesEqualExceptId(List<EntityResponse> expected, List<EntityResponse> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEntityEqualsExceptId(expected.get(i), actual.get(i));
        }
    }
    
    private void assertItemsEqualsExceptId(ItemResponse expected, ItemResponse actual) {
        assertEquals(expected.getType(), actual.getType());
    }

    private void assertItemsEqualExceptId(List<ItemResponse> expected, List<ItemResponse> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertItemsEqualsExceptId(expected.get(i), actual.get(i));
        }
    }

    @Test
    @DisplayName("Test save game")
    public void testSaveGame() {
        deleteAllSaves();
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_movementTest_testMovement", "c_movementTest_testMovement");

        dmc.tick(Direction.RIGHT);
        dmc.saveGame("save1");

        List<String> allGames = dmc.allGames();
        assertIterableEquals(allGames, List.of("save1"));
    }

    @Test
    @DisplayName("Test load positions")
    public void testLoadPositions() {
        deleteAllSaves();
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_movementTest_testMovement", "c_movementTest_testMovement");

        for (int i = 0; i < 5; i++) {
            dmc.tick(Direction.RIGHT);
        }

        DungeonResponse saveResponse = dmc.saveGame("save1");
        dmc.newGame("d_mercenaryTest_movementMercenary", "c_movementTest_testMovement");
        DungeonResponse loadResponse = dmc.loadGame("save1");

        assertEntitiesEqualExceptId(saveResponse.getEntities(), loadResponse.getEntities());
        assertItemsEqualExceptId(saveResponse.getInventory(), loadResponse.getInventory());

    }

    @Test
    @DisplayName("Test load invalid game name")
    public void testInvalidGameName() {
        deleteAllSaves();
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_movementTest_testMovement", "c_movementTest_testMovement");

        dmc.tick(Direction.RIGHT);
        dmc.saveGame("save1");

        assertThrows(IllegalArgumentException.class, () -> dmc.loadGame("save2"));
    }

    @Test
    @DisplayName("Test spawned entities have the same properties")
    public void testSpawner() {
        deleteAllSaves();
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_battleTest_zombieSpawner", "c_movementTest_testMovement");

        for (int i = 0; i < 2; i++) {
            dmc.tick(Direction.DOWN);
        }

        DungeonResponse saveResponse = dmc.saveGame("save1");
        dmc.newGame("d_mercenaryTest_movementMercenary", "c_movementTest_testMovement");
        DungeonResponse loadResponse = dmc.loadGame("save1");

        assertEntitiesEqualExceptId(saveResponse.getEntities(), loadResponse.getEntities());
        assertItemsEqualExceptId(saveResponse.getInventory(), loadResponse.getInventory());

    }

    @Test
    @DisplayName("Test build bow and has bow after save and load")
    public void testBuildBow() {
        deleteAllSaves();
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_build_bow", "c_buildablesTests");
        
        assertEquals(1, getEntities(res, "wood").size());
        assertEquals(3, getEntities(res, "arrow").size());
        
        // pick up 1 wood and 3 arrow
        for (int i = 0; i < 4; i++) {
            res = dmc.tick(Direction.RIGHT);
        }
        
        assertEquals(0, getEntities(res, "wood").size());
        assertEquals(0, getEntities(res, "arrow").size());
        assertEquals(1, getInventory(res, "wood").size());
        assertEquals(3, getInventory(res, "arrow").size());
        
        // Build bow
        res = assertDoesNotThrow(() -> dmc.build("bow"));
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(0, getInventory(res, "arrow").size());
        assertEquals(1, getInventory(res, "bow").size());
        
        DungeonResponse saveResponse = dmc.saveGame("save1");
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(0, getInventory(res, "arrow").size());
        assertEquals(1, getInventory(res, "bow").size());

        dmc.newGame("d_mercenaryTest_movementMercenary", "c_movementTest_testMovement");

        DungeonResponse loadResponse = dmc.loadGame("save1");

        assertEntitiesEqualExceptId(saveResponse.getEntities(), loadResponse.getEntities());
        assertItemsEqualExceptId(saveResponse.getInventory(), loadResponse.getInventory());
        
        // Shoudl have bow from last save
        assertEquals(0, getInventory(loadResponse, "wood").size());
        assertEquals(0, getInventory(loadResponse, "arrow").size());
        assertEquals(1, getInventory(loadResponse, "bow").size());

    }
}
