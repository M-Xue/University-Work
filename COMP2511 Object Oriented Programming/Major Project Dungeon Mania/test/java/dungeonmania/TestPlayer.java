package dungeonmania;

import static dungeonmania.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestPlayer {
    @Test
    @DisplayName("Test successful player movement in all directions")
    public void testMovementValid() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_movementTest_testMovement", "c_movementTest_testMovement");

        // Down
        Position expectedPlayerPosition = new Position(1, 2);
        DungeonResponse actualDungonRes = dmc.tick(Direction.DOWN);
        Position actualPlayerPosition = getPlayer(actualDungonRes).get().getPosition();
        assertEquals(expectedPlayerPosition, actualPlayerPosition);

        // Up
        expectedPlayerPosition = new Position(1, 1);
        actualDungonRes = dmc.tick(Direction.UP);
        actualPlayerPosition = getPlayer(actualDungonRes).get().getPosition();
        assertEquals(expectedPlayerPosition, actualPlayerPosition);

        // Right
        expectedPlayerPosition = new Position(2, 1);
        actualDungonRes = dmc.tick(Direction.RIGHT);
        actualPlayerPosition = getPlayer(actualDungonRes).get().getPosition();
        assertEquals(expectedPlayerPosition, actualPlayerPosition);

        // Left 
        expectedPlayerPosition = new Position(1, 1);
        actualDungonRes = dmc.tick(Direction.LEFT);
        actualPlayerPosition = getPlayer(actualDungonRes).get().getPosition();
        assertEquals(expectedPlayerPosition, actualPlayerPosition);
    }

    @Test
    @DisplayName("Test that the player can move down")
    public void testMovementDown() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_movementTest_testMovement", "c_movementTest_testMovement");
        EntityResponse initPlayer = getPlayer(initDungonRes).get();

        // create the expected result
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 2), false);

        // move player downwards
        DungeonResponse actualDungonRes = dmc.tick(Direction.DOWN);
        EntityResponse actualPlayer = getPlayer(actualDungonRes).get();

        // assert after movement
        assertEquals(expectedPlayer, actualPlayer);
    }

    @Test
    @DisplayName("Test that the player can move up")
    public void testMovementUp() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_movementTest_testMovement", "c_movementTest_testMovement");
        EntityResponse initPlayer = getPlayer(initDungonRes).get();

        // create the expected result
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);

        // move player upwards
        DungeonResponse actualDungonRes = dmc.tick(Direction.UP);
        EntityResponse actualPlayer = getPlayer(actualDungonRes).get();

        // assert after movement
        assertEquals(expectedPlayer, actualPlayer);
    }

    @Test
    @DisplayName("Test that the player can move right")
    public void testMovementRight() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_movementTest_testMovement", "c_movementTest_testMovement");
        EntityResponse initPlayer = getPlayer(initDungonRes).get();

        // create the expected result
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(2, 1), false);

        // move player to the right
        DungeonResponse actualDungonRes = dmc.tick(Direction.RIGHT);
        EntityResponse actualPlayer = getPlayer(actualDungonRes).get();

        // assert after movement
        assertEquals(expectedPlayer, actualPlayer);
    }

    @Test
    @DisplayName("Test that the player can move left")
    public void testMovementLeft() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_movementTest_testMovement", "c_movementTest_testMovement");
        EntityResponse initPlayer = getPlayer(initDungonRes).get();

        // create the expected result
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 1), false);

        // move player to the left
        DungeonResponse actualDungonRes = dmc.tick(Direction.LEFT);
        EntityResponse actualPlayer = getPlayer(actualDungonRes).get();

        // assert after movement
        assertEquals(expectedPlayer, actualPlayer);
    }
}
