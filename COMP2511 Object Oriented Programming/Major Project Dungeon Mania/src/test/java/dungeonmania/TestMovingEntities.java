package dungeonmania;

import static dungeonmania.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestMovingEntities {
    // Mercenary
    @Test
    @DisplayName("Test basic movement of mercenary in enemy mode towards the player")
    public void testMercenaryEnemyMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_movementMercenary", "c_movementTest_movementMercenary");

        // player (6, 1), mercenary (0, 1)
        int x = 0;
        int y = 1;
        for (int i = 0; i <= 5; i++) {
            res = dmc.tick(Direction.RIGHT);
            assertEquals(new Position(++x, y), getEntities(res, "mercenary").get(0).getPosition());
        }
    }

    @Test
    @DisplayName("Test movement of mercenary in enemy mode with obstacles towards the player")
    public void testMercenaryEnemyMovementWithObstacles() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_movementMercenaryWithObstacles", "c_movementTest_movementMercenary");

        // player (6, 4), mercenary (3, 4)
        // walls x: 0-10 y: 3
        int x = 3;
        int y = 4;

        // move player to pass the edge of the walls
        // mercenary just follows
        for (int i = 0; i < 5; i++) {
            res = dmc.tick(Direction.RIGHT);
            assertEquals(new Position(++x, y), getEntities(res, "mercenary").get(0).getPosition());
        }

        // move player to other side of the wall
        // mercenary moves along the wall
        for (int i = 0; i < 2; i++) {
            res = dmc.tick(Direction.UP);
            assertEquals(new Position(++x, y), getEntities(res, "mercenary").get(0).getPosition());
        }

        // player starts to move on other side of the wall, while mercenary is on the other side
        // player (11, 2), mercenary (10,4)
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(++x, y), getEntities(res, "mercenary").get(0).getPosition());
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(x, --y), getEntities(res, "mercenary").get(0).getPosition());
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(x, --y), getEntities(res, "mercenary").get(0).getPosition());

        // move player along the other side of the wall
        // mercenary follows
        for (int i = 0; i <= 4; i++) {
            res = dmc.tick(Direction.LEFT);
            assertEquals(new Position(--x, y), getEntities(res, "mercenary").get(0).getPosition());
        }
    }

    @Test
    @DisplayName("Test successful bribing of the mercenary")
    public void testMercenaryBribingValid() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_movementMercenaryWithCoin", "c_movementTest_movementMercenary");

        // collect coin 
        DungeonResponse res = dmc.tick(Direction.RIGHT);

        // bribe and make mercenary an ally
        assertDoesNotThrow(() -> {
            dmc.interact(getEntities(res, "mercenary").get(0).getId());
        });
    }

    @Test
    @DisplayName("Test unsuccessful bribing of the mercenary")
    public void testMercenaryBribingInvalid() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_mercenaryTest_movementMercenaryWithCoin", "c_movementTest_movementMercenary");

        DungeonResponse res = dmc.tick(Direction.DOWN);

        // not enough money to bribe
        assertThrows(InvalidActionException.class, () -> {
            dmc.interact(getEntities(res, "mercenary").get(0).getId());
        });
    }

    // Spiders
    @Test
    @DisplayName("Test basic movement of spiders")
    public void testBasicSpiderMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_spiderTest_basicMovement", "c_spiderTest_basicMovement");
        Position pos = getEntities(res, "spider").get(0).getPosition();

        List<Position> movementTrajectory = new ArrayList<Position>();
        int x = pos.getX();
        int y = pos.getY();
        int nextPositionElement = 0;
        movementTrajectory.add(new Position(x  , y-1));
        movementTrajectory.add(new Position(x+1, y-1));
        movementTrajectory.add(new Position(x+1, y));
        movementTrajectory.add(new Position(x+1, y+1));
        movementTrajectory.add(new Position(x  , y+1));
        movementTrajectory.add(new Position(x-1, y+1));
        movementTrajectory.add(new Position(x-1, y));
        movementTrajectory.add(new Position(x-1, y-1));

        // Assert Circular Movement of Spider
        for (int i = 0; i <= 20; ++i) {
            res = dmc.tick(Direction.UP);
            assertEquals(movementTrajectory.get(nextPositionElement), getEntities(res, "spider").get(0).getPosition());
            
            nextPositionElement++;
            if (nextPositionElement == 8){
                nextPositionElement = 0;
            }
        }
    }

    @Test
    @DisplayName("Test movement of spiders when they encounter a boulder")
    public void testSpiderMovementWithBoulder() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_spiderTest_movementWithBoulder", "c_spiderTest_basicMovement");
        Position pos = getEntities(res, "spider").get(0).getPosition();

        // spider (5,5), boulder (5,6)
        List<Position> movementTrajectory = new ArrayList<Position>();
        int x = pos.getX();
        int y = pos.getY();
        int nextPositionElement = 0;
        movementTrajectory.add(new Position(x  , y-1));
        movementTrajectory.add(new Position(x+1, y-1));
        movementTrajectory.add(new Position(x+1, y));
        movementTrajectory.add(new Position(x+1, y+1));
        movementTrajectory.add(new Position(x+1, y));
        movementTrajectory.add(new Position(x+1, y-1));
        movementTrajectory.add(new Position(x  , y-1));
        movementTrajectory.add(new Position(x-1, y-1));
        movementTrajectory.add(new Position(x-1, y));
        movementTrajectory.add(new Position(x-1, y+1));
        movementTrajectory.add(new Position(x-1, y));
        movementTrajectory.add(new Position(x-1, y-1));
 
        // Assert Movement of Spider when avoiding boulder
        for (int i = 0; i <= 25; ++i) {
            res = dmc.tick(Direction.UP);
            assertEquals(movementTrajectory.get(nextPositionElement), getEntities(res, "spider").get(0).getPosition());
            
            nextPositionElement++;
            if (nextPositionElement == 12){
                nextPositionElement = 0;
            }
        }
    }

    // Zombies
    @Test
    @DisplayName("Test basic zombie toast movement")
    public void testBasicZombieToastMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_zombieToast_movementZombie", "c_zombieToastTest_movementZombie");
        Position oldPos = getEntities(res, "zombie_toast").get(0).getPosition();

        Position newPos;
        for (int i = 0; i <= 25; ++i) {
            res = dmc.tick(Direction.UP);
            newPos = getEntities(res, "zombie_toast").get(0).getPosition();
            assertNotEquals(oldPos, newPos);
            assertTrue(Position.isAdjacent(oldPos, newPos));
            oldPos = newPos;
        }
    }
}
