package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xml.sax.EntityResolver;

import static dungeonmania.TestUtils.*;

import dungeonmania.entities.Entity;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestSceptre {

    @Test
    @DisplayName("Test build sceptre 1 wood + 1 key + 1 sunstone")
    public void testBuildSceptre1() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests_further", "c_buildablesTests_further");

        assertEquals(8, getEntities(res, "wood").size());
        assertEquals(5, getEntities(res, "sun_stone").size());
        assertEquals(4, getEntities(res, "key").size());

        // pick up 1 sunstone
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        // pick up 1 key
        res = dmc.tick(Direction.LEFT);
        
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        // pick up 1 wood
        res = dmc.tick(Direction.RIGHT);

        assertEquals(new Position(1, -1), getPlayer(res).get().getPosition());
        assertEquals(7, getEntities(res, "wood").size());
        assertEquals(4, getEntities(res, "sun_stone").size());
        assertEquals(3, getEntities(res, "key").size());
        assertEquals(1, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "key").size());
        
        // Build sceptre
        assertTrue(res.getBuildables().contains("sceptre"));
        res = assertDoesNotThrow(() -> {return dmc.build("sceptre");});
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "key").size());
        assertEquals(1, getInventory(res, "sceptre").size());

    }

    @Test
    @DisplayName("Test build sceptre 1 wood + 1 treasure + 1 sunstone")
    public void testBuildSceptre2() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests_further", "c_buildablesTests_further");

        assertEquals(8, getEntities(res, "wood").size());
        assertEquals(5, getEntities(res, "sun_stone").size());
        assertEquals(8, getEntities(res, "treasure").size());

        // pick up 1 sunstone
        res = dmc.tick(Direction.LEFT);

        res = dmc.tick(Direction.RIGHT);
        // pick up 1 treasure
        res = dmc.tick(Direction.RIGHT);
        // pick up 1 wood
        res = dmc.tick(Direction.UP);

        assertEquals(new Position(1, -1), getPlayer(res).get().getPosition());
        assertEquals(7, getEntities(res, "wood").size());
        assertEquals(4, getEntities(res, "sun_stone").size());
        assertEquals(7, getEntities(res, "treasure").size());
        assertEquals(1, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "treasure").size());
        
        // Build sceptre
        System.out.println(res.getBuildables());
        assertTrue(res.getBuildables().contains("sceptre"));
        res = assertDoesNotThrow(() -> {return dmc.build("sceptre");});
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "sceptre").size());

    }


    @Test
    @DisplayName("Test build sceptre 2 arrows + 1 key + 1 sunstone")
    public void testBuildSceptre3() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests_further", "c_buildablesTests_further");

        assertEquals(8, getEntities(res, "arrow").size());
        assertEquals(5, getEntities(res, "sun_stone").size());
        assertEquals(4, getEntities(res, "key").size());

        // pick up 1 sunstone
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        // pick up 1 key
        res = dmc.tick(Direction.LEFT);
        
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        // pick up 2 arrows
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(new Position(2, -2), getPlayer(res).get().getPosition());
        assertEquals(6, getEntities(res, "arrow").size());
        assertEquals(4, getEntities(res, "sun_stone").size());
        assertEquals(3, getEntities(res, "key").size());
        assertEquals(2, getInventory(res, "arrow").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "key").size());
        
        // Build sceptre
        assertTrue(res.getBuildables().contains("sceptre"));
        res = assertDoesNotThrow(() -> {return dmc.build("sceptre");});
        assertEquals(0, getInventory(res, "arrow").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "key").size());
        assertEquals(1, getInventory(res, "sceptre").size());

    }

    @Test
    @DisplayName("Test build sceptre 2 arrows + 1 treasure + 1 sunstone")
    public void testBuildSceptre4() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests_further", "c_buildablesTests_further");

        assertEquals(8, getEntities(res, "arrow").size());
        assertEquals(5, getEntities(res, "sun_stone").size());
        assertEquals(8, getEntities(res, "treasure").size());

        // pick up 1 sunstone
        res = dmc.tick(Direction.LEFT);

        res = dmc.tick(Direction.RIGHT);
        // pick up 1 treasure
        res = dmc.tick(Direction.RIGHT);

        res = dmc.tick(Direction.LEFT);
        
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        // pick up 2 arrows
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(new Position(2, -2), getPlayer(res).get().getPosition());
        assertEquals(6, getEntities(res, "arrow").size());
        assertEquals(4, getEntities(res, "sun_stone").size());
        assertEquals(7, getEntities(res, "treasure").size());
        assertEquals(2, getInventory(res, "arrow").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "treasure").size());
        
        // Build sceptre
        assertTrue(res.getBuildables().contains("sceptre"));
        res = assertDoesNotThrow(() -> {return dmc.build("sceptre");});
        assertEquals(0, getInventory(res, "arrow").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "sceptre").size());

    }
    
    @Test
    @DisplayName("Test build sceptre 2 arrows + 1 sunstone (substitute 1 key/treasure) + 1 sunstone")
    public void testBuildSceptre5() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests_further", "c_buildablesTests_further");

        assertEquals(8, getEntities(res, "arrow").size());
        assertEquals(5, getEntities(res, "sun_stone").size());
        assertEquals(8, getEntities(res, "treasure").size());

        // pick up 2 sunstone
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);

        res = dmc.tick(Direction.RIGHT);
        // dont pick up 1 treasure
        res = dmc.tick(Direction.RIGHT);
        
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        // pick up 2 arrows
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(new Position(2, -2), getPlayer(res).get().getPosition());
        assertEquals(6, getEntities(res, "arrow").size());
        assertEquals(3, getEntities(res, "sun_stone").size());
        assertEquals(8, getEntities(res, "treasure").size());
        assertEquals(2, getInventory(res, "arrow").size());
        assertEquals(2, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "treasure").size());
        
        // Build sceptre
        assertTrue(res.getBuildables().contains("sceptre"));
        res = assertDoesNotThrow(() -> {return dmc.build("sceptre");});
        assertEquals(0, getInventory(res, "arrow").size());
        assertEquals(2, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "sceptre").size());

    }

    @Test
    @DisplayName("Test build sceptre 0/1 wood + 1 key + 1 sunstone")
    public void testBuildSceptreInsufficient1() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests_further", "c_buildablesTests_further");

        assertEquals(8, getEntities(res, "wood").size());
        assertEquals(5, getEntities(res, "sun_stone").size());
        assertEquals(4, getEntities(res, "key").size());

        // pick up 1 sunstone
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        // pick up 1 key
        res = dmc.tick(Direction.LEFT);
        
        // dont pick up 1 wood

        assertEquals(new Position(-2, 2), getPlayer(res).get().getPosition());
        assertEquals(8, getEntities(res, "wood").size());
        assertEquals(4, getEntities(res, "sun_stone").size());
        assertEquals(3, getEntities(res, "key").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "key").size());
        
        // Cannot build sceptre
        assertFalse(res.getBuildables().contains("sceptre"));
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "key").size());
        assertEquals(0, getInventory(res, "sceptre").size());

    }

    @Test
    @DisplayName("Test build sceptre 1 wood + 0/1 treasure + 1 sunstone")
    public void testBuildSceptreInsufficient2() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests_further", "c_buildablesTests_further");

        assertEquals(8, getEntities(res, "wood").size());
        assertEquals(5, getEntities(res, "sun_stone").size());
        assertEquals(8, getEntities(res, "treasure").size());

        // pick up 1 sunstone
        res = dmc.tick(Direction.LEFT);

        res = dmc.tick(Direction.RIGHT);
        // dont pick up 1 treasure
        // pick up 1 wood
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(new Position(1, -1), getPlayer(res).get().getPosition());
        assertEquals(7, getEntities(res, "wood").size());
        assertEquals(4, getEntities(res, "sun_stone").size());
        assertEquals(8, getEntities(res, "treasure").size());
        assertEquals(1, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "treasure").size());
        
        // Cannot build sceptre
        assertFalse(res.getBuildables().contains("sceptre"));
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        assertEquals(1, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "treasure").size());
        assertEquals(0, getInventory(res, "sceptre").size());

    }

    @Test
    @DisplayName("Test build sceptre 2 arrows + 1 key + 0/1 sunstone")
    public void testBuildSceptreInsufficient3() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests_further", "c_buildablesTests_further");

        assertEquals(8, getEntities(res, "arrow").size());
        assertEquals(5, getEntities(res, "sun_stone").size());
        assertEquals(4, getEntities(res, "key").size());

        // dont pick up 1 sunstone
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        // pick up 1 key
        res = dmc.tick(Direction.LEFT);
        
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        // pick up 2 arrows
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(new Position(2, -2), getPlayer(res).get().getPosition());
        assertEquals(6, getEntities(res, "arrow").size());
        assertEquals(5, getEntities(res, "sun_stone").size());
        assertEquals(3, getEntities(res, "key").size());
        assertEquals(2, getInventory(res, "arrow").size());
        assertEquals(0, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "key").size());
        
        // Cannot build sceptre
        assertFalse(res.getBuildables().contains("sceptre"));
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        assertEquals(2, getInventory(res, "arrow").size());
        assertEquals(0, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "key").size());
        assertEquals(0, getInventory(res, "sceptre").size());

    }

    @Test
    @DisplayName("Test build sceptre insufficient materials 1/2 arrows + 1 treasure + 1 sunstone")
    public void testBuildSceptreInsufficient4() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests_further", "c_buildablesTests_further");

        assertEquals(8, getEntities(res, "arrow").size());
        assertEquals(5, getEntities(res, "sun_stone").size());
        assertEquals(8, getEntities(res, "treasure").size());

        // pick up 1 sunstone
        res = dmc.tick(Direction.LEFT);

        res = dmc.tick(Direction.RIGHT);
        // pick up 1 treasure
        res = dmc.tick(Direction.RIGHT);

        res = dmc.tick(Direction.LEFT);
        
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        // pick up 1 arrow
        res = dmc.tick(Direction.RIGHT);

        assertEquals(new Position(1, -2), getPlayer(res).get().getPosition());
        assertEquals(7, getEntities(res, "arrow").size());
        assertEquals(4, getEntities(res, "sun_stone").size());
        assertEquals(7, getEntities(res, "treasure").size());
        assertEquals(1, getInventory(res, "arrow").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "treasure").size());
        
        // Cannot build sceptre
        assertFalse(res.getBuildables().contains("sceptre"));
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        assertEquals(1, getInventory(res, "arrow").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(0, getInventory(res, "sceptre").size());

    }

    @Test
    @DisplayName("Test cannot use sceptre")
    public void testUseSceptreError() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests_further", "c_buildablesTests_further");

        assertEquals(8, getEntities(res, "wood").size());
        assertEquals(5, getEntities(res, "sun_stone").size());
        assertEquals(8, getEntities(res, "treasure").size());

        // pick up 1 sunstone
        res = dmc.tick(Direction.LEFT);

        res = dmc.tick(Direction.RIGHT);
        // pick up 1 treasure
        res = dmc.tick(Direction.RIGHT);
        // pick up 1 wood
        res = dmc.tick(Direction.UP);

        assertEquals(new Position(1, -1), getPlayer(res).get().getPosition());
        assertEquals(7, getEntities(res, "wood").size());
        assertEquals(4, getEntities(res, "sun_stone").size());
        assertEquals(7, getEntities(res, "treasure").size());
        assertEquals(1, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "treasure").size());
        
        // Build sceptre
        assertTrue(res.getBuildables().contains("sceptre"));
        res = assertDoesNotThrow(() -> {return dmc.build("sceptre");});
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "sceptre").size());

        // Cannot use sceptre
        String sceptre = getInventory(res, "sceptre").get(0).getId();
        assertThrows(IllegalArgumentException.class, () -> dmc.tick(sceptre));
    }

    @Test
    @DisplayName("Test cannot interact with sceptre")
    public void testInteractSceptreError() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests_further", "c_buildablesTests_further");

        assertEquals(8, getEntities(res, "wood").size());
        assertEquals(5, getEntities(res, "sun_stone").size());
        assertEquals(8, getEntities(res, "treasure").size());

        // pick up 1 sunstone
        res = dmc.tick(Direction.LEFT);

        res = dmc.tick(Direction.RIGHT);
        // pick up 1 treasure
        res = dmc.tick(Direction.RIGHT);
        // pick up 1 wood
        res = dmc.tick(Direction.UP);

        assertEquals(new Position(1, -1), getPlayer(res).get().getPosition());
        assertEquals(7, getEntities(res, "wood").size());
        assertEquals(4, getEntities(res, "sun_stone").size());
        assertEquals(7, getEntities(res, "treasure").size());
        assertEquals(1, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "treasure").size());
        
        // Build sceptre
        assertTrue(res.getBuildables().contains("sceptre"));
        res = assertDoesNotThrow(() -> {return dmc.build("sceptre");});
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "sceptre").size());

        // Cannot interact with sceptre
        String sceptre = getInventory(res, "sceptre").get(0).getId();
        assertThrows(IllegalArgumentException.class, () -> dmc.interact(sceptre));
    }
    

}
