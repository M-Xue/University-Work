package dungeonmania;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;

public class TimeTravellingTests {
    @Test
    public void testRewindNegativeTicks() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_movementTest_testMovement", "c_movementTest_testMovement");

        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.UP);

        assertThrows(IllegalArgumentException.class, () -> dmc.rewind(-1));
    }

    @Test
    public void testRewindNotOccurred() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_movementTest_testMovement", "c_movementTest_testMovement");

        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.UP);

        assertThrows(IllegalArgumentException.class, () -> dmc.rewind(5));
    }

    @Test
    public void testRewind() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_TimeTravellingTests_advanced", "c_movementTest_testMovement");

        for (int i = 0; i < 8; i++) {
            dmc.tick(Direction.RIGHT);
        }
        DungeonResponse response = dmc.rewind(5);

        assertEquals(
                new Position(4, 1),
                TestUtils.getEntities(response, "older_player").get(0).getPosition()
        );
    }

    @Test
    @DisplayName("Test rewind player 1 tick, older and new player positions")
    public void testRewind2() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_TimeTravellingTests_advanced", "c_movementTest_testMovement");

        for (int i = 0; i < 3; i++) {
            res = dmc.tick(Direction.RIGHT);
        }
        
        // Old player appears one step to the left
        res = dmc.rewind(1);
        assertEquals(
                new Position(3, 1),
                TestUtils.getEntities(res, "older_player").get(0).getPosition()
        );
        assertEquals(
                new Position(4, 1),
                TestUtils.getPlayer(res).get().getPosition()
        );
        
        // Both old player and current player move one step right
        res = dmc.tick(Direction.RIGHT);
        assertEquals(
                new Position(4, 1),
                TestUtils.getEntities(res, "older_player").get(0).getPosition()
        );
        assertEquals(
                new Position(5, 1),
                TestUtils.getPlayer(res).get().getPosition()
        );

        // Old player disappears
        res = dmc.tick(Direction.RIGHT);
        assertEquals(
                0, TestUtils.getEntities(res, "older_player").size()
        );
        assertEquals(
                new Position(6, 1),
                TestUtils.getPlayer(res).get().getPosition()
        );
    }

    @Test
    @DisplayName("Test rewind player 5 ticks, older and new player positions")
    public void testRewind3() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_TimeTravellingTests_advanced", "c_movementTest_testMovement");

        for (int i = 0; i < 5; i++) {
            res = dmc.tick(Direction.RIGHT);
        }
        
        // Old player appears at start
        res = dmc.rewind(5);
        assertEquals(
                new Position(1, 1),
                TestUtils.getEntities(res, "older_player").get(0).getPosition()
        );
        assertEquals(
                new Position(6, 1),
                TestUtils.getPlayer(res).get().getPosition()
        );
        
        // Both old player and current player move five steps right
        for (int i = 1; i <= 5; i++) {
            res = dmc.tick(Direction.RIGHT);
            assertEquals(
                    new Position(1 + i, 1),
                    TestUtils.getEntities(res, "older_player").get(0).getPosition()
            );
            assertEquals(
                    new Position(6 + i, 1),
                    TestUtils.getPlayer(res).get().getPosition()
            );
        }

        // Old player disappears
        res = dmc.tick(Direction.RIGHT);
        assertEquals(
                0, TestUtils.getEntities(res, "older_player").size()
        );
        assertEquals(
                new Position(12, 1),
                TestUtils.getPlayer(res).get().getPosition()
        );
    }

    @Test
    public void testTimeTravellingPortal() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_TimeTravellingTests_advanced", "c_movementTest_testMovement");

        for (int i = 0; i < 15; i++) {
            dmc.tick(Direction.RIGHT);
        }

        for (int i = 0; i < 13; i++) {
            dmc.tick(Direction.DOWN);
        }

        DungeonResponse response = dmc.getDungeonResponseModel();

        assertEquals(
                new Position(1, 1),
                TestUtils.getEntities(response, "older_player").get(0).getPosition()
        );
    }

    @Test
    public void testUseBomb() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_TimeTravellingTest_bomb", "c_movementTest_testMovement");

        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);

        DungeonResponse response = dmc.getDungeonResponseModel();
        String bombId = TestUtils.getInventory(response, "bomb").get(0).getId();
        assertDoesNotThrow(() -> dmc.tick(bombId));

        response = dmc.getDungeonResponseModel();
        assertEquals(0, TestUtils.countEntityOfType(response, "boulder"));

        dmc.rewind(5);

        for (int i = 0; i < 6; i++) {
            dmc.tick(Direction.RIGHT);
        }

        response = dmc.getDungeonResponseModel();
        assertEquals(0, TestUtils.countEntityOfType(response, "boulder"));
    }
}
