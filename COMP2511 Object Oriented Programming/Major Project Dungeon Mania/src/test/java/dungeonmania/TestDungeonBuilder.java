package dungeonmania;

import static dungeonmania.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;


public class TestDungeonBuilder {
        public void testGeneratedDungeon(DungeonResponse res, Position start, Position end) {
            assertEquals(start, getPlayer(res).get().getPosition());
            assertEquals(1, getEntities(res, "exit").size());
            assertEquals(end, getEntities(res, "exit").get(0).getPosition());
    
            // Test top and bottom boundary walls
            for (int x = start.getX() - 1; x < end.getX() + 2; x++) {
                Position topWallPos = new Position(x, start.getY()-1);
                assertTrue(getEntities(res, "wall").stream().anyMatch(e -> e.getPosition().equals(topWallPos)));
                Position botWallPos = new Position(x, end.getY()+1);
                assertTrue(getEntities(res, "wall").stream().anyMatch(e -> e.getPosition().equals(botWallPos)));
            }
    
            // Test left and right boundary walls
            for (int y = start.getY() - 1; y < end.getY() + 2; y++) {
                Position leftWallPos = new Position(start.getX()-1, y);
                assertTrue(getEntities(res, "wall").stream().anyMatch(e -> e.getPosition().equals(leftWallPos)));
                Position rightWallPos = new Position(end.getX()+1, y);
                assertTrue(getEntities(res, "wall").stream().anyMatch(e -> e.getPosition().equals(rightWallPos)));
            }
    
    }

    @Test
    @DisplayName("Test generate dungeon start=(0,0) end=(9,9)")
    public void testGenerate() {
        Position start = new Position(0, 0);
        Position end = new Position(9, 9);

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = assertDoesNotThrow(() -> dmc.generateDungeon(start.getX(), start.getY(), end.getX(), end.getY(), "c_dungeonBuilderTest_generateDungeon"));

        testGeneratedDungeon(res, start, end);
    }

    @Test
    @DisplayName("Test generate dungeon start=(-5,-5) end=(4,4)")
    public void testGenerateNegativesPositives() {
        Position start = new Position(-5, -5);
        Position end = new Position(4, 4);

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = assertDoesNotThrow(() -> dmc.generateDungeon(start.getX(), start.getY(), end.getX(), end.getY(), "c_dungeonBuilderTest_generateDungeon"));

        testGeneratedDungeon(res, start, end);
    }

    @Test
    @DisplayName("Test generate dungeon start=(-10,-10) end=(-1,-1)")
    public void testGenerateAllNegatives() {
        Position start = new Position(-10,-10);
        Position end = new Position(-1,-1);

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = assertDoesNotThrow(() -> dmc.generateDungeon(start.getX(), start.getY(), end.getX(), end.getY(), "c_dungeonBuilderTest_generateDungeon"));

        testGeneratedDungeon(res, start, end);
    }

    @Test
    @DisplayName("Test generate dungeon start=(-50,-50) end=(-49,-49)")
    public void testGenerateAllLargeNegatives() {
        Position start = new Position(-50,-50);
        Position end = new Position(-49,-49);

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = assertDoesNotThrow(() -> dmc.generateDungeon(start.getX(), start.getY(), end.getX(), end.getY(), "c_dungeonBuilderTest_generateDungeon"));

        testGeneratedDungeon(res, start, end);
    }

    @Test
    @DisplayName("Test generate dungeon start=(49,49) end=(50, 50)")
    public void testGenerateAllLargePositives() {
        Position start = new Position(49,49);
        Position end = new Position(50,50);

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = assertDoesNotThrow(() -> dmc.generateDungeon(start.getX(), start.getY(), end.getX(), end.getY(), "c_dungeonBuilderTest_generateDungeon"));

        testGeneratedDungeon(res, start, end);
    }

    @Test
    @DisplayName("Test generate dungeon start=(-50,-50) end=(50, 50)")
    public void testGenerateLargeNegativesPositives() {
        Position start = new Position(-50,-50);
        Position end = new Position(50,50);

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = assertDoesNotThrow(() -> dmc.generateDungeon(start.getX(), start.getY(), end.getX(), end.getY(), "c_dungeonBuilderTest_generateDungeon"));

        testGeneratedDungeon(res, start, end);
    }

    @Test
    @DisplayName("Test generate dungeon start=(3,7) end=(8, 12)")
    public void testGenerateRectangular() {
        Position start = new Position(3,7);
        Position end = new Position(8,12);

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = assertDoesNotThrow(() -> dmc.generateDungeon(start.getX(), start.getY(), end.getX(), end.getY(), "c_dungeonBuilderTest_generateDungeon"));

        testGeneratedDungeon(res, start, end);
    }

    @Test
    @DisplayName("Test generate dungeon start=(-3,-7) end=(8, 12)")
    public void testGenerateRectangularNegativePositive() {
        Position start = new Position(-3,-7);
        Position end = new Position(8,12);

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = assertDoesNotThrow(() -> dmc.generateDungeon(start.getX(), start.getY(), end.getX(), end.getY(), "c_dungeonBuilderTest_generateDungeon"));

        testGeneratedDungeon(res, start, end);
    }

    @Test
    @DisplayName("Test generate dungeon start=(-43,-37) end=(-21, -12)")
    public void testGenerateRectangularAllNegatives() {
        Position start = new Position(-43,-37);
        Position end = new Position(-21,-12);

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = assertDoesNotThrow(() -> dmc.generateDungeon(start.getX(), start.getY(), end.getX(), end.getY(), "c_dungeonBuilderTest_generateDungeon"));

        testGeneratedDungeon(res, start, end);
    }

    @Test
    @DisplayName("Test generate dungeon and save and load")
    public void testGenerateAndSaveAndLoad() {
        Position start = new Position(-5,-5);
        Position end = new Position(9, 12);

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = assertDoesNotThrow(() -> dmc.generateDungeon(start.getX(), start.getY(), end.getX(), end.getY(), "c_dungeonBuilderTest_generateDungeon"));

        testGeneratedDungeon(res, start, end);

        DungeonResponse saveRes = assertDoesNotThrow(() -> dmc.saveGame("generated_dungeon_1"));
        DungeonResponse loadRes = assertDoesNotThrow(() -> dmc.loadGame("generated_dungeon_1"));

        testGeneratedDungeon(saveRes, start, end);
        testGeneratedDungeon(loadRes, start, end);

        
    }

}
