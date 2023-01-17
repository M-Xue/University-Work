package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static dungeonmania.TestUtils.*;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestBomb {
    
    @Test
    @DisplayName("Test place bombs")
    public void placeBombs() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest", "c_bombTest");

        // 4 bombs on map
        assertEquals(4, getEntities(res, "bomb").size());
        
        // pick up 2 bombs
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        
        assertEquals(new Position(2, 0), getPlayer(res).get().getPosition());
        // 2 bombs on map
        assertEquals(2, getEntities(res, "bomb").size());
        // 2 bombs on inventory
        assertEquals(2, getInventory(res, "bomb").size());
        
        // place bomb
        String bombId1 = getInventory(res, "bomb").get(0).getId();
        res = dmc.tick(Direction.RIGHT);
        String bombId2 = getInventory(res, "bomb").get(1).getId();
        res = assertDoesNotThrow(() -> {return dmc.tick(bombId1);});
        res = assertDoesNotThrow(() -> {return dmc.tick(bombId2);});

        assertEquals(new Position(3, 0), getPlayer(res).get().getPosition());
        // 4 bombs on map
        assertEquals(4, getEntities(res, "bomb").size());
        // 0 bombs on inventory
        assertEquals(0, getInventory(res, "bomb").size());


    }


    @Test
    @DisplayName("Test place bomb diagonal to switch in blast radius, does not detonate.")
    public void placeBombsDiagonalToSwitch() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest", "c_bombTest");

        // 4 bombs on map
        assertEquals(4, getEntities(res, "bomb").size());
        
        // pick up 2 bombs
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        
        assertEquals(new Position(2, 0), getPlayer(res).get().getPosition());
        // 2 bombs on map
        assertEquals(2, getEntities(res, "bomb").size());
        // 2 bombs on inventory
        assertEquals(2, getInventory(res, "bomb").size());
        
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 3), getPlayer(res).get().getPosition());
        
        // place bomb
        String bombId1 = getInventory(res, "bomb").get(0).getId();
        res = dmc.tick(Direction.RIGHT);
        res = assertDoesNotThrow(() -> {return dmc.tick(bombId1);});

        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        // move boulder onto switch, does not detonate bomb
        res = dmc.tick(Direction.RIGHT);

        assertEquals(3, getEntities(res, "bomb").size());
        assertEquals(1, getInventory(res, "bomb").size());


    }


    @Test
    @DisplayName("Test place bombs within blast radius to switch but not cardinally adjacent, does not detonate.")
    public void placeBombNotCardinallyAdj() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest", "c_bombTest");

        // 4 bombs on map
        assertEquals(4, getEntities(res, "bomb").size());
        
        // pick up 2 bombs
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        
        assertEquals(new Position(2, 0), getPlayer(res).get().getPosition());
        // 2 bombs on map
        assertEquals(2, getEntities(res, "bomb").size());
        // 2 bombs on inventory
        assertEquals(2, getInventory(res, "bomb").size());
        
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(4, 2), getPlayer(res).get().getPosition());
        
        // place bomb
        String bombId1 = getInventory(res, "bomb").get(0).getId();
        res = dmc.tick(Direction.RIGHT);
        res = assertDoesNotThrow(() -> {return dmc.tick(bombId1);});

        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        // move boulder onto switch, does not detonate bomb
        res = dmc.tick(Direction.RIGHT);

        assertEquals(3, getEntities(res, "bomb").size());
        assertEquals(1, getInventory(res, "bomb").size());
    }

    @Test
    @DisplayName("Test detonate bomb removes everything within blast radius from map, except player.")
    public void detonateBomb() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest", "c_bombTest");

        assertEquals(4, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "invincibility_potion").size());
        assertEquals(1, getEntities(res, "invisibility_potion").size());
        assertEquals(2, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "arrow").size());
        assertEquals(1, getEntities(res, "wood").size());
        assertEquals(2, getEntities(res, "key").size());
        assertEquals(2, getEntities(res, "door").size());
        assertEquals(3, getEntities(res, "switch").size());
        assertEquals(3, getEntities(res, "boulder").size());
        assertEquals(3, getEntities(res, "wall").size());
        assertEquals(1, getEntities(res, "exit").size());
        assertEquals(1, getEntities(res, "zombie_toast_spawner").size());

        // pick up 2 bombs
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        
        assertEquals(new Position(2, 0), getPlayer(res).get().getPosition());
        
        
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(5, 3), getPlayer(res).get().getPosition());
        
        // place bomb cadinally adj to switch
        String bombId1 = getInventory(res, "bomb").get(0).getId();
        res = assertDoesNotThrow(() -> {return dmc.tick(bombId1);});
        // already used
        assertThrows(InvalidActionException.class, () -> dmc.tick(bombId1));

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        // move boulder onto switch, detonate bomb
        res = dmc.tick(Direction.RIGHT);

        // 1 bomb on inventory
        assertEquals(1, getInventory(res, "bomb").size());
        // reduced entities on map
        assertEquals(2, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "invincibility_potion").size());
        assertEquals(1, getEntities(res, "invisibility_potion").size());
        assertEquals(0, getEntities(res, "treasure").size());
        assertEquals(0, getEntities(res, "arrow").size());
        assertEquals(1, getEntities(res, "wood").size());
        assertEquals(1, getEntities(res, "key").size());
        assertEquals(1, getEntities(res, "door").size());
        assertEquals(2, getEntities(res, "switch").size());
        assertEquals(2, getEntities(res, "boulder").size());
        assertEquals(3, getEntities(res, "wall").size());
        assertEquals(1, getEntities(res, "exit").size());
        assertEquals(1, getEntities(res, "zombie_toast_spawner").size());

        res = assertDoesNotThrow(() -> {return dmc.tick(Direction.RIGHT);});
        res = assertDoesNotThrow(() -> {return dmc.tick(Direction.DOWN);});
        // Walk through position where locked door previously was.
        res = assertDoesNotThrow(() -> {return dmc.tick(Direction.DOWN);});
        assertEquals(new Position(4, 5), getPlayer(res).get().getPosition());
    }
    
    @Test
    @DisplayName("Test placing bomb on already activated switch detonates bomb.")
    public void detonateBombAfterSwitchActivate() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest", "c_bombTest");

        assertEquals(4, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "invincibility_potion").size());
        assertEquals(1, getEntities(res, "invisibility_potion").size());
        assertEquals(2, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "arrow").size());
        assertEquals(1, getEntities(res, "wood").size());
        assertEquals(2, getEntities(res, "key").size());
        assertEquals(2, getEntities(res, "door").size());
        assertEquals(3, getEntities(res, "switch").size());
        assertEquals(3, getEntities(res, "boulder").size());
        assertEquals(3, getEntities(res, "wall").size());
        assertEquals(1, getEntities(res, "exit").size());
        assertEquals(1, getEntities(res, "zombie_toast_spawner").size());

        // pick up 2 bombs
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        
        assertEquals(new Position(2, 0), getPlayer(res).get().getPosition());
        
        
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(2, 3), getPlayer(res).get().getPosition());
        
        // move boulder onto switch, activate switch
        res = dmc.tick(Direction.RIGHT);
        
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);

        // place bomb cadinally adj to activated switch
        String bombId1 = getInventory(res, "bomb").get(0).getId();
        res = assertDoesNotThrow(() -> {return dmc.tick(bombId1);});
        // already used
        assertThrows(InvalidActionException.class, () -> dmc.tick(bombId1));

        // 1 bomb on inventory
        assertEquals(1, getInventory(res, "bomb").size());
        // reduced entities on map
        assertEquals(2, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "invincibility_potion").size());
        assertEquals(0, getEntities(res, "invisibility_potion").size());
        assertEquals(0, getEntities(res, "treasure").size());
        assertEquals(0, getEntities(res, "arrow").size());
        assertEquals(0, getEntities(res, "wood").size());
        assertEquals(1, getEntities(res, "key").size());
        assertEquals(2, getEntities(res, "door").size());
        assertEquals(1, getEntities(res, "switch").size());
        assertEquals(1, getEntities(res, "boulder").size());
        assertEquals(3, getEntities(res, "wall").size());
        assertEquals(1, getEntities(res, "exit").size());
        assertEquals(1, getEntities(res, "zombie_toast_spawner").size());

        res = assertDoesNotThrow(() -> {return dmc.tick(Direction.DOWN);});
        res = assertDoesNotThrow(() -> {return dmc.tick(Direction.DOWN);});
        // Blocked by lcoekd dooor
        res = assertDoesNotThrow(() -> {return dmc.tick(Direction.DOWN);});
        assertEquals(new Position(4, 4), getPlayer(res).get().getPosition());



    }

    @Test
    @DisplayName("Test placing bomb on deactivated switch does not detonate bomb.")
    public void placeBombAfterSwitchDectivate() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest", "c_bombTest");

        assertEquals(4, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "invincibility_potion").size());
        assertEquals(1, getEntities(res, "invisibility_potion").size());
        assertEquals(2, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "arrow").size());
        assertEquals(1, getEntities(res, "wood").size());
        assertEquals(2, getEntities(res, "key").size());
        assertEquals(2, getEntities(res, "door").size());
        assertEquals(3, getEntities(res, "switch").size());
        assertEquals(3, getEntities(res, "boulder").size());
        assertEquals(3, getEntities(res, "wall").size());
        assertEquals(1, getEntities(res, "exit").size());
        assertEquals(1, getEntities(res, "zombie_toast_spawner").size());

        // pick up 2 bombs
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        
        assertEquals(new Position(2, 0), getPlayer(res).get().getPosition());
        
        
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(2, 3), getPlayer(res).get().getPosition());
        
        // move boulder onto switch then off, deactivate switch
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        
        res = dmc.tick(Direction.UP);

        // place bomb cadinally adj to deactivated switch
        String bombId1 = getInventory(res, "bomb").get(0).getId();
        res = assertDoesNotThrow(() -> {return dmc.tick(bombId1);});
        // already used
        assertThrows(InvalidActionException.class, () -> dmc.tick(bombId1));

        // 1 bomb on inventory
        assertEquals(1, getInventory(res, "bomb").size());
        // no reduced entities on map except picked up bomb
        assertEquals(3, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "invincibility_potion").size());
        assertEquals(1, getEntities(res, "invisibility_potion").size());
        assertEquals(2, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "arrow").size());
        assertEquals(1, getEntities(res, "wood").size());
        assertEquals(2, getEntities(res, "key").size());
        assertEquals(2, getEntities(res, "door").size());
        assertEquals(3, getEntities(res, "switch").size());
        assertEquals(3, getEntities(res, "boulder").size());
        assertEquals(3, getEntities(res, "wall").size());
        assertEquals(1, getEntities(res, "exit").size());
        assertEquals(1, getEntities(res, "zombie_toast_spawner").size());

        res = assertDoesNotThrow(() -> {return dmc.tick(Direction.DOWN);});
        // pick up key
        res = assertDoesNotThrow(() -> {return dmc.tick(Direction.DOWN);});
        // Unlock door
        res = assertDoesNotThrow(() -> {return dmc.tick(Direction.DOWN);});
        assertEquals(new Position(4, 5), getPlayer(res).get().getPosition());
    }


    @Test
    @DisplayName("Test placing bomb on deactivated switch then activate switch detonates bomb.")
    public void placeBombAfterSwitchDectivateThenActivate() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest", "c_bombTest");

        assertEquals(4, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "invincibility_potion").size());
        assertEquals(1, getEntities(res, "invisibility_potion").size());
        assertEquals(2, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "arrow").size());
        assertEquals(1, getEntities(res, "wood").size());
        assertEquals(2, getEntities(res, "key").size());
        assertEquals(2, getEntities(res, "door").size());
        assertEquals(3, getEntities(res, "switch").size());
        assertEquals(3, getEntities(res, "boulder").size());
        assertEquals(3, getEntities(res, "wall").size());
        assertEquals(1, getEntities(res, "exit").size());
        assertEquals(1, getEntities(res, "zombie_toast_spawner").size());

        // pick up 2 bombs
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        
        assertEquals(new Position(2, 0), getPlayer(res).get().getPosition());
        
        
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(2, 3), getPlayer(res).get().getPosition());
        
        // move boulder onto switch then off, deactivate switch
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        
        res = dmc.tick(Direction.UP);

        // place bomb cadinally adj to deactivated switch
        String bombId1 = getInventory(res, "bomb").get(0).getId();
        res = assertDoesNotThrow(() -> {return dmc.tick(bombId1);});
        // already used
        assertThrows(InvalidActionException.class, () -> dmc.tick(bombId1));

        // 1 bomb on inventory
        assertEquals(1, getInventory(res, "bomb").size());
        // no reduced entities on map except picked up bomb
        assertEquals(3, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "invincibility_potion").size());
        assertEquals(1, getEntities(res, "invisibility_potion").size());
        assertEquals(2, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "arrow").size());
        assertEquals(1, getEntities(res, "wood").size());
        assertEquals(2, getEntities(res, "key").size());
        assertEquals(2, getEntities(res, "door").size());
        assertEquals(3, getEntities(res, "switch").size());
        assertEquals(3, getEntities(res, "boulder").size());
        assertEquals(3, getEntities(res, "wall").size());
        assertEquals(1, getEntities(res, "exit").size());
        assertEquals(1, getEntities(res, "zombie_toast_spawner").size());

        res = assertDoesNotThrow(() -> {return dmc.tick(Direction.RIGHT);});
        res = assertDoesNotThrow(() -> {return dmc.tick(Direction.RIGHT);});
        res = assertDoesNotThrow(() -> {return dmc.tick(Direction.DOWN);});
        // move boulder onto swtich, activate switch, detonate bomb
        res = assertDoesNotThrow(() -> {return dmc.tick(Direction.LEFT);});
        assertEquals(new Position(5, 3), getPlayer(res).get().getPosition());

        // still1 bomb on inventory
        assertEquals(1, getInventory(res, "bomb").size());
        // reduced entities on map
        assertEquals(2, getEntities(res, "bomb").size());
        assertEquals(1, getEntities(res, "invincibility_potion").size());
        assertEquals(0, getEntities(res, "invisibility_potion").size());
        assertEquals(0, getEntities(res, "treasure").size());
        assertEquals(0, getEntities(res, "arrow").size());
        assertEquals(0, getEntities(res, "wood").size());
        assertEquals(1, getEntities(res, "key").size());
        assertEquals(2, getEntities(res, "door").size());
        assertEquals(1, getEntities(res, "switch").size());
        assertEquals(1, getEntities(res, "boulder").size());
        assertEquals(3, getEntities(res, "wall").size());
        assertEquals(1, getEntities(res, "exit").size());
        assertEquals(1, getEntities(res, "zombie_toast_spawner").size());

    }

}
