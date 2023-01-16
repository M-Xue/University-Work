package dungeonmania;

import static dungeonmania.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestStaticEntities {
    // Boulder
    @Test
    @DisplayName("Test moving boulder into empty space")
    public void testMovingBoulderIntoEmptySpace() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_staticEntities_basicOperations", "c_staticEntities_basicOperations");
        Position oldBoulderPos = getEntities(res, "boulder").get(0).getPosition();
        Position oldPlayerPos = getEntities(res, "player").get(0).getPosition();

        // player (1, 1), boulder (2, 1), wall (4, 1)
        res = dmc.tick(Direction.RIGHT);
        Position newBoulderPos = getEntities(res, "boulder").get(0).getPosition();
        Position newPlayerPos = getEntities(res, "player").get(0).getPosition();

        assertNotEquals(newBoulderPos, oldBoulderPos);
        assertNotEquals(newPlayerPos, oldPlayerPos);
        assertEquals(newBoulderPos, new Position(3, 1));
        assertEquals(newPlayerPos, new Position(2, 1));
    }

    @Test
    @DisplayName("Test trying moving boulder into a wall")
    public void testTryMovingBoulderIntoWall() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_staticEntities_basicOperations", "c_staticEntities_basicOperations");

        // player (1, 1), boulder (2, 1), wall (4, 1)
        res = dmc.tick(Direction.RIGHT);
        Position oldBoulderPos = getEntities(res, "boulder").get(0).getPosition();
        Position oldPlayerPos = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.RIGHT);
        Position newBoulderPos = getEntities(res, "boulder").get(0).getPosition();
        Position newPlayerPos = getEntities(res, "player").get(0).getPosition();


        assertEquals(newBoulderPos, oldBoulderPos);
        assertEquals(newPlayerPos, oldPlayerPos);
        assertEquals(newBoulderPos, new Position(3, 1));
        assertEquals(newPlayerPos, new Position(2, 1));
    }

    @Test
    @DisplayName("Test trying moving boulder into a boulder")
    public void testTryMovingBoulderIntoBoulder() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_staticEntities_twoBoulders", "c_staticEntities_basicOperations");
        Position oldBoulderPos1 = getEntities(res, "boulder").get(0).getPosition();
        Position oldBoulderPos2 = getEntities(res, "boulder").get(1).getPosition();
        Position oldPlayerPos = getEntities(res, "player").get(0).getPosition();
        
        // player (1, 1), boulder (2, 1), boulder (3, 1)
        res = dmc.tick(Direction.RIGHT);
        Position newBoulderPos1 = getEntities(res, "boulder").get(0).getPosition();
        Position newBoulderPos2 = getEntities(res, "boulder").get(1).getPosition();
        Position newPlayerPos = getEntities(res, "player").get(0).getPosition();

        assertEquals(newBoulderPos1, oldBoulderPos1);
        assertEquals(newBoulderPos2, oldBoulderPos2);
        assertEquals(newPlayerPos, oldPlayerPos);
    }

    // Zombie Toast Spawner
    @Test
    @DisplayName("Test spawning zombies")
    public void testSpawningZombieToasts() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_staticEntities_basicOperations", "c_staticEntities_basicOperations");
        
        // spawn rate 1
        for (int i = 0; i < 10; i++) {
            res = dmc.tick(Direction.RIGHT);
            assertEquals(getEntities(res, "zombie_toast").size(), i + 1);
        }
    }

    @Test
    @DisplayName("Test destroying spawner without weapon")
    public void testDestroyingSpawnerInvalid() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_staticEntities_destroyZombieSpawnerInvalid", "c_staticEntities_basicOperations");
        
        assertThrows(InvalidActionException.class, () -> {
            dmc.interact(getEntities(res, "zombie_toast_spawner").get(0).getId());
        });
    }
}   
