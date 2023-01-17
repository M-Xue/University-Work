package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static dungeonmania.TestUtils.*;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestBuildables {

    @Test
    @DisplayName("Test build bow successfully with 3/3 arrow 1/1 wood")
    public void buildBowSuccess() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");
        
        for (int i = 0; i < 8; i++) {
            res = dmc.tick(Direction.DOWN);
        }
        
        // pick up 1 wood
        res = dmc.tick(Direction.DOWN);

        // pick up 3 arrows
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        

        assertEquals(1, getInventory(res, "wood").size());
        assertEquals(3, getInventory(res, "arrow").size());

        assertEquals(new Position(3, 11), getEntities(res, "player").get(0).getPosition());

        assertTrue(res.getBuildables().size() == 1);
        assertTrue(res.getBuildables().contains("bow"));
        assertFalse(res.getBuildables().contains("shield"));

        res = assertDoesNotThrow(() -> {return dmc.build("bow");});
        assertEquals(1, getInventory(res, "bow").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(0, getInventory(res, "arrow").size());
        
    }

    @Test
    @DisplayName("Test build bow successfully with 4/3 arrow 1/1 wood")
    public void buildBowSuccess2() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");
        
        for (int i = 0; i < 8; i++) {
            res = dmc.tick(Direction.DOWN);
        }
        
        // pick up 1 wood
        res = dmc.tick(Direction.DOWN);

        // pick up 4 arrows
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        

        assertEquals(1, getInventory(res, "wood").size());
        assertEquals(4, getInventory(res, "arrow").size());

        assertEquals(new Position(4, 11), getEntities(res, "player").get(0).getPosition());

        assertTrue(res.getBuildables().size() == 1);
        assertTrue(res.getBuildables().contains("bow"));
        assertFalse(res.getBuildables().contains("shield"));

        res = assertDoesNotThrow(() -> {return dmc.build("bow");});
        assertEquals(1, getInventory(res, "bow").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "arrow").size());
        
    }

    @Test
    @DisplayName("Test build bow successfully with 4/3 arrow 2/1 wood")
    public void buildBowSuccess3() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");
        
        for (int i = 0; i < 8; i++) {
            res = dmc.tick(Direction.DOWN);
        }
        
        // pick up 1 wood
        res = dmc.tick(Direction.DOWN);
        
        // pick up 4 arrows
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        
        // pick up 1 wood
        res = dmc.tick(Direction.UP);

        assertEquals(2, getInventory(res, "wood").size());
        assertEquals(4, getInventory(res, "arrow").size());

        assertEquals(new Position(4, 10), getEntities(res, "player").get(0).getPosition());

        assertTrue(res.getBuildables().size() == 1);
        assertTrue(res.getBuildables().contains("bow"));
        assertFalse(res.getBuildables().contains("shield"));

        res = assertDoesNotThrow(() -> {return dmc.build("bow");});
        assertEquals(1, getInventory(res, "bow").size());
        assertEquals(1, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "arrow").size());
        
    }


    @Test
    @DisplayName("Test build shield successfully with 2 wood 1 treasure")
    public void buildShieldWithTreasure() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");
        
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        // Pick up treasure
        res = dmc.tick(Direction.UP);

        for (int i = 0; i < 9; i++) {
            res = dmc.tick(Direction.DOWN);
        }
        
        // pick up 2 wood
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.LEFT);        

        assertEquals(2, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "treasure").size());

        assertEquals(new Position(3, 10), getEntities(res, "player").get(0).getPosition());

        assertTrue(res.getBuildables().size() == 1);
        assertTrue(res.getBuildables().contains("shield"));
        assertFalse(res.getBuildables().contains("bow"));

        res = assertDoesNotThrow(() -> {return dmc.build("shield");});
        assertEquals(1, getInventory(res, "shield").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(0, getInventory(res, "treasure").size());
        
    }

    @Test
    @DisplayName("Test build shield successfully with 2 wood 1 key")
    public void buildShieldWithKey() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");
        
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        // Pick up key
        res = dmc.tick(Direction.UP);
        
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.LEFT);
        for (int i = 0; i < 7; i++) {
            res = dmc.tick(Direction.DOWN);
        }
        
        // pick up 2 wood
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.LEFT);        

        assertEquals(2, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "key").size());

        assertEquals(new Position(3, 10), getEntities(res, "player").get(0).getPosition());

        assertTrue(res.getBuildables().size() == 1);
        assertTrue(res.getBuildables().contains("shield"));
        assertFalse(res.getBuildables().contains("bow"));

        res = assertDoesNotThrow(() -> {return dmc.build("shield");});
        assertEquals(1, getInventory(res, "shield").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(0, getInventory(res, "key").size());
        
    }


    @Test
    @DisplayName("Test build shield successfully with 2 wood 1 key 1 treasure")
    /**
     * Assumption: if both treasure and key are available for building shield,
     * use treasure first.
     */
    public void buildShieldWithTreasureOrKey() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");
        
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        // Pick up key
        res = dmc.tick(Direction.UP);
        // Pick up treasure
        res = dmc.tick(Direction.UP);
        
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.LEFT);
        for (int i = 0; i < 7; i++) {
            res = dmc.tick(Direction.DOWN);
        }
        
        // pick up 2 wood
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.LEFT);        

        assertEquals(2, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "key").size());

        assertEquals(new Position(3, 10), getEntities(res, "player").get(0).getPosition());

        assertTrue(res.getBuildables().size() == 1);
        assertTrue(res.getBuildables().contains("shield"));
        assertFalse(res.getBuildables().contains("bow"));

        res = assertDoesNotThrow(() -> {return dmc.build("shield");});
        assertEquals(1, getInventory(res, "shield").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(0, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "key").size());
        
    }

    @Test
    @DisplayName("Test insufficient materials to build bow with 2/3 wood 1/1 arrow")
    public void buildBowInsufficient() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");
        
        for (int i = 0; i < 8; i++) {
            res = dmc.tick(Direction.DOWN);
        }
        
        // pick up 1 wood
        res = dmc.tick(Direction.DOWN);

        // pick up 2 arrows
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        

        assertEquals(1, getInventory(res, "wood").size());
        assertEquals(2, getInventory(res, "arrow").size());

        assertEquals(new Position(2, 11), getEntities(res, "player").get(0).getPosition());

        assertTrue(res.getBuildables().size() == 0);
        assertFalse(res.getBuildables().contains("bow"));
        assertFalse(res.getBuildables().contains("shield"));

        assertThrows(InvalidActionException.class, () -> {dmc.build("bow");});

        
    }

    @Test
    @DisplayName("Test insufficient materials to build shield with 2/2 wood 0/1 treasure, 0/1 key")
    public void buildShieldInsufficient() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");
        
        for (int i = 0; i < 8; i++) {
            res = dmc.tick(Direction.DOWN);
        }
        
        // pick up 2 wood
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);


        assertEquals(2, getInventory(res, "wood").size());

        assertEquals(new Position(2, 10), getEntities(res, "player").get(0).getPosition());

        assertTrue(res.getBuildables().size() == 0);
        assertFalse(res.getBuildables().contains("bow"));
        assertFalse(res.getBuildables().contains("shield"));

        assertThrows(InvalidActionException.class, () -> {dmc.build("shield");});

        
    }


    @Test
    @DisplayName("Test build 2 bows")
    public void testBuildTwoBows() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");
        
        for (int i = 0; i < 8; i++) {
            res = dmc.tick(Direction.DOWN);
        }
        
        // pick up 4 wood
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        
        // pick up 8 arrows
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        
        assertEquals(4, getInventory(res, "wood").size());
        assertEquals(8, getInventory(res, "arrow").size());

        assertEquals(new Position(4, 12), getEntities(res, "player").get(0).getPosition());

        assertTrue(res.getBuildables().size() == 1);
        assertTrue(res.getBuildables().contains("bow"));
        assertFalse(res.getBuildables().contains("shield"));

        // Build first bow
        res = assertDoesNotThrow(() -> {return dmc.build("bow");});
        assertEquals(1, getInventory(res, "bow").size());
        assertEquals(3, getInventory(res, "wood").size());
        assertEquals(5, getInventory(res, "arrow").size());

        // Build second bow
        res = assertDoesNotThrow(() -> {return dmc.build("bow");});
        assertEquals(2, getInventory(res, "bow").size());
        assertEquals(2, getInventory(res, "wood").size());
        assertEquals(2, getInventory(res, "arrow").size());
    }

}
