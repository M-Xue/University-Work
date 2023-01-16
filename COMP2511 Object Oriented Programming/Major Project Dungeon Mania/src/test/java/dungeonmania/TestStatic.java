package dungeonmania;

import static dungeonmania.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestStatic {
    @Test
    @DisplayName("Testing wall stops movement")
    public void testWall() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_wallTest_simple", "c_movementTest_testMovementDown");
        EntityResponse initPlayer = getPlayer(initDungonRes).get();

        // assert player is in correct initial position
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        DungeonResponse newDungonRes = dmc.tick(Direction.DOWN);
        EntityResponse newPlayer = getPlayer(newDungonRes).get();

        // assert player is in correct initial position
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, newPlayer);

        newDungonRes = dmc.tick(Direction.UP);
        newPlayer = getPlayer(newDungonRes).get();

        // assert player is in correct initial position
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, newPlayer);

        newDungonRes = dmc.tick(Direction.LEFT);
        newPlayer = getPlayer(newDungonRes).get();

        // assert player is in correct initial position
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, newPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);
        newPlayer = getPlayer(newDungonRes).get();

        // assert player is in correct initial position
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, newPlayer);



        //***************************************************************************** */
        //* SIMPLE LINE MAZE MAP */
        //***************************************************************************** */

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_wallTest_simple2", "c_movementTest_testMovementDown");
        initPlayer = getPlayer(initDungonRes).get();

        // assert player is in correct initial position
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        newDungonRes = dmc.tick(Direction.DOWN);
        newPlayer = getPlayer(newDungonRes).get();
        
        // assert player is in correct initial position
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, newPlayer);

        newDungonRes = dmc.tick(Direction.UP);
        newPlayer = getPlayer(newDungonRes).get();

        // assert player is in correct initial position
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, newPlayer);

        newDungonRes = dmc.tick(Direction.LEFT);
        newPlayer = getPlayer(newDungonRes).get();

        // assert player is in correct initial position
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, newPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);
        newPlayer = getPlayer(newDungonRes).get();

        // assert player can move into open tile
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1,0), false);
        assertEquals(expectedPlayer, newPlayer);

        newDungonRes = dmc.tick(Direction.DOWN);
        newPlayer = getPlayer(newDungonRes).get();

        // assert player is in correct position
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1,0), false);
        assertEquals(expectedPlayer, newPlayer);

        newDungonRes = dmc.tick(Direction.UP);
        newPlayer = getPlayer(newDungonRes).get();

        // assert player is in correct position
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1,0), false);
        assertEquals(expectedPlayer, newPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);
        newPlayer = getPlayer(newDungonRes).get();

        // assert player can move into open tile
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(2,0), false);
        assertEquals(expectedPlayer, newPlayer);

        newDungonRes = dmc.tick(Direction.DOWN);
        newPlayer = getPlayer(newDungonRes).get();

        // assert player is in correct position
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(2,0), false);
        assertEquals(expectedPlayer, newPlayer);

        newDungonRes = dmc.tick(Direction.UP);
        newPlayer = getPlayer(newDungonRes).get();

        // assert player is in correct position
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(2,0), false);
        assertEquals(expectedPlayer, newPlayer);

        assertTrue(getGoals(newDungonRes).contains(":exit"));

        newDungonRes = dmc.tick(Direction.RIGHT);
        newPlayer = getPlayer(newDungonRes).get();

        // assert player can move into open tile
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(3,0), false);
        assertEquals(expectedPlayer, newPlayer);

        assertEquals("", getGoals(newDungonRes));
    }

    @Test
    @DisplayName("Testing single portal")
    public void testSinglePortal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_portalTest_single", "c_movementTest_testMovementDown");
        EntityResponse initPlayer = getPlayer(initDungonRes).get();

        // assert player is in correct initial position
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);
        EntityResponse newPlayer = getPlayer(newDungonRes).get();
        EntityResponse expectedPlayerDown = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(99, 100), false);
        EntityResponse expectedPlayerUp = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(99, 98), false);
        EntityResponse expectedPlayerRight = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(100, 99), false);
        EntityResponse expectedPlayerLeft = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(98, 99), false);
        assertTrue(newPlayer.equals(expectedPlayerDown) || newPlayer.equals(expectedPlayerUp) || newPlayer.equals(expectedPlayerRight) || newPlayer.equals(expectedPlayerLeft));

        //** BLOCKED PORTAL */
        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_portalTest_blocked", "c_movementTest_testMovementDown");
        initPlayer = getPlayer(initDungonRes).get();

        // assert player is in correct initial position
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);
        newPlayer = getPlayer(newDungonRes).get();
        assertEquals(expectedPlayer, newPlayer);
    }

    @Test
    @DisplayName("Testing multiple portals")
    public void testMultiplePortals() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_portalTest_multiple", "c_movementTest_testMovementDown");
        EntityResponse initPlayer = getPlayer(initDungonRes).get();

        // assert player is in correct initial position
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);
        EntityResponse newPlayer = getPlayer(newDungonRes).get();
        EntityResponse expectedPlayerDown = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(99, 100), false);
        EntityResponse expectedPlayerUp = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(99, 98), false);
        EntityResponse expectedPlayerRight = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(100, 99), false);
        EntityResponse expectedPlayerLeft = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(98, 99), false);
        assertTrue(newPlayer.equals(expectedPlayerDown) || newPlayer.equals(expectedPlayerUp) || newPlayer.equals(expectedPlayerRight) || newPlayer.equals(expectedPlayerLeft));

        //* TEST MULTI PORTAL TRAVEL */
        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_portalTest_multiTravel", "c_movementTest_testMovementDown");
        initPlayer = getPlayer(initDungonRes).get();

        // assert player is in correct initial position
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);
        newPlayer = getPlayer(newDungonRes).get();
        expectedPlayerDown = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(50, 51), false);
        expectedPlayerUp = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(50, 49), false);
        expectedPlayerRight = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(51, 50), false);
        expectedPlayerLeft = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(49, 50), false);
        assertTrue(newPlayer.equals(expectedPlayerDown) || newPlayer.equals(expectedPlayerUp) || newPlayer.equals(expectedPlayerRight) || newPlayer.equals(expectedPlayerLeft));
    }
}
