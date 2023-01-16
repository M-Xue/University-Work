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
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestCollectables {
    
    @Test
    @DisplayName("Test collect treasure")
    public void collectTreasure() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");

        // Initially 5 treasure on map
        assertEquals(5, getEntities(res, "treasure").size());
        
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        
        // pick up 5 treasure
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // No more treasure on map
        assertEquals(0, getEntities(res, "treasure").size());

        assertEquals(5, getInventory(res, "treasure").size());

        assertEquals(new Position(8, 0), getPlayer(res).get().getPosition());
    }

    @Test
    @DisplayName("Test use treasure throws IllegalArgumentException")
    public void useTreasure() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");
        
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        
        // pick up 5 treasure
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(5, getInventory(res, "treasure").size());

        assertEquals(new Position(8, 0), getPlayer(res).get().getPosition());

        String treasureId = getInventory(res, "treasure").get(0).getId();

        assertThrows(IllegalArgumentException.class, () -> dmc.tick(treasureId));
    }

    @Test
    @DisplayName("Test collect sword, potions and keys. If holding a key, cannot pick up another key.")
    public void collectKeys() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");

        // Pick up potions, sword and a key, crossing over second key.
        for (int i = 0; i < 7; i++) {
            res = dmc.tick(Direction.RIGHT);
        }

        assertEquals(0, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "invincibility_potion").size());
        assertEquals(1, getInventory(res, "invisibility_potion").size());
        assertEquals(1, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "key").size());

        assertEquals(new Position(8, 1), getPlayer(res).get().getPosition());
    }

    @Test
    @DisplayName("Test use key throws IllegalArgumentException.")
    public void useKey() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");

        // Pick up potions, sword and a key, crossing over second key.
        for (int i = 0; i < 7; i++) {
            res = dmc.tick(Direction.RIGHT);
        }

        // One key left on map
        assertEquals(1, getEntities(res, "key").size());

        assertEquals(0, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "invincibility_potion").size());
        assertEquals(1, getInventory(res, "invisibility_potion").size());
        assertEquals(1, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "key").size());

        assertEquals(new Position(8, 1), getPlayer(res).get().getPosition());


        String keyId = getInventory(res, "key").get(0).getId();

        assertThrows(IllegalArgumentException.class, () -> dmc.tick(keyId));

    }

    @Test
    @DisplayName("Test use sword throws IllegalArgumentException.")
    public void useSword() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");

        // Pick up potions, sword and a key, crossing over second key.
        for (int i = 0; i < 7; i++) {
            res = dmc.tick(Direction.RIGHT);
        }

        // One key left on map
        assertEquals(1, getEntities(res, "key").size());

        assertEquals(0, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "invincibility_potion").size());
        assertEquals(1, getInventory(res, "invisibility_potion").size());
        assertEquals(1, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "key").size());

        assertEquals(new Position(8, 1), getPlayer(res).get().getPosition());


        String swordId = getInventory(res, "sword").get(0).getId();

        assertThrows(IllegalArgumentException.class, () -> dmc.tick(swordId));

    }

    @Test
    @DisplayName("Test collect wood.")
    public void collectWood() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");

        // Four wood on map
        assertEquals(4, getEntities(res, "wood").size());

        for (int i = 0; i < 8; i++) {
            res = dmc.tick(Direction.DOWN);
        }
        
        // Pick up four wood
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(new Position(4, 10), getPlayer(res).get().getPosition());
        // No more wood left on map
        assertEquals(4, getInventory(res, "wood").size());
        assertEquals(0, getEntities(res, "wood").size());


    }

    @Test
    @DisplayName("Test use wood throws IllegalArgumentException.")
    public void useWood() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");

        // Four wood on map
        assertEquals(4, getEntities(res, "wood").size());

        for (int i = 0; i < 8; i++) {
            res = dmc.tick(Direction.DOWN);
        }
        
        // Pick up two wood
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(new Position(2, 10), getPlayer(res).get().getPosition());
        // No more wood left on map
        assertEquals(2, getInventory(res, "wood").size());
        assertEquals(2, getEntities(res, "wood").size());

        String woodId = getInventory(res, "wood").get(1).getId();

        assertThrows(IllegalArgumentException.class, () -> dmc.tick(woodId));

    }

    @Test
    @DisplayName("Test collect arrow.")
    public void collectArrow() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");

        // Eight arrows on map
        assertEquals(8, getEntities(res, "arrow").size());

        for (int i = 0; i < 9; i++) {
            res = dmc.tick(Direction.DOWN);
        }
        
        // Pick up four arrow
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);

        assertEquals(new Position(1, 12), getPlayer(res).get().getPosition());
        // No more arrow left on map
        assertEquals(8, getInventory(res, "arrow").size());
        assertEquals(0, getEntities(res, "arrow").size());


    }

    @Test
    @DisplayName("Test use arrow throws IllegalArgumentException.")
    public void useArrow() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");

        // Eight arrow on map
        assertEquals(8, getEntities(res, "arrow").size());

        for (int i = 0; i < 9; i++) {
            res = dmc.tick(Direction.DOWN);
        }
        
        // Pick up two arrow
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(new Position(2, 11), getPlayer(res).get().getPosition());
        // Six arrow left on map
        assertEquals(2, getInventory(res, "arrow").size());
        assertEquals(6, getEntities(res, "arrow").size());

        String arrowId = getInventory(res, "arrow").get(1).getId();

        assertThrows(IllegalArgumentException.class, () -> dmc.tick(arrowId));

    }

    @Test
    @DisplayName("Test use invincibility_potion.")
    public void useInvincibilityPotion() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");

        // One invin potion on map
        assertEquals(1, getEntities(res, "invincibility_potion").size());
        
        // Pick up potions
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(new Position(3, 1), getPlayer(res).get().getPosition());
        
        // No invin potion left on map
        assertEquals(0, getEntities(res, "invincibility_potion").size());
        // One invin potion in inventory
        assertEquals(1, getInventory(res, "invincibility_potion").size());
        
        String invinPotionId = getInventory(res, "invincibility_potion").get(0).getId();
        
        res = assertDoesNotThrow(() -> {return dmc.tick(invinPotionId);});

        // No invin potion left on map
        assertEquals(0, getEntities(res, "invincibility_potion").size());
        // No invin potion in inventory
        assertEquals(0, getInventory(res, "invincibility_potion").size());

    }

    @Test
    @DisplayName("Test use invisibility_potion.")
    public void useInvisibilityPotion() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");

        // One invis potion on map
        assertEquals(1, getEntities(res, "invisibility_potion").size());
        
        // Pick up potions
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(new Position(3, 1), getPlayer(res).get().getPosition());
        
        // No invis potion left on map
        assertEquals(0, getEntities(res, "invisibility_potion").size());
        // One invis potion in inventory
        assertEquals(1, getInventory(res, "invisibility_potion").size());
        // No invin potion left on map
        assertEquals(0, getEntities(res, "invincibility_potion").size());
        // One invin potion in inventory
        assertEquals(1, getInventory(res, "invincibility_potion").size());
        
        String invisPotionId = getInventory(res, "invisibility_potion").get(0).getId();
        
        res = assertDoesNotThrow(() -> {return dmc.tick(invisPotionId);});

        // No invis potion left on map
        assertEquals(0, getEntities(res, "invisibility_potion").size());
        // No invis potion in inventory
        assertEquals(0, getInventory(res, "invisibility_potion").size());
        // No invin potion left on map
        assertEquals(0, getEntities(res, "invincibility_potion").size());
        // One invin potion in inventory
        assertEquals(1, getInventory(res, "invincibility_potion").size());

    }

    @Test
    @DisplayName("Test use bomb.")
    public void useBomb() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");

        // Four bomb on map
        assertEquals(4, getEntities(res, "bomb").size());
        
        for (int i = 0; i < 8; i++) {
            res = dmc.tick(Direction.DOWN);
        }
        for (int i = 0; i < 4; i++) {
            res = dmc.tick(Direction.RIGHT);
        }

        // Pick up two bombs
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(new Position(6, 10), getPlayer(res).get().getPosition());
        
        // Two bombs left on map
        assertEquals(2, getEntities(res, "bomb").size());
        // Two bombs in inventory
        assertEquals(2, getInventory(res, "bomb").size());
        
        String bombId1 = getInventory(res, "bomb").get(0).getId();
        String bombId2 = getInventory(res, "bomb").get(1).getId();
        
        // Place down two bombs
        res = assertDoesNotThrow(() -> {return dmc.tick(bombId1);});
        res = dmc.tick(Direction.UP);
        res = assertDoesNotThrow(() -> {return dmc.tick(bombId2);});

        // Four bombs left on map
        assertEquals(4, getEntities(res, "bomb").size());
        // No bombs in inventory
        assertEquals(0, getInventory(res, "bomb").size());

    }

    @Test
    @DisplayName("Test use not in inventory.")
    public void useNotInInventory() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests", "c_buildablesTests");

        assertEquals(1, getEntities(res, "invincibility_potion").size());
        assertEquals(1, getEntities(res, "invisibility_potion").size());
        assertEquals(4, getEntities(res, "bomb").size());
        
        String invinPotionId = getEntities(res, "invincibility_potion").get(0).getId();
        String invisPotionId = getEntities(res, "invisibility_potion").get(0).getId();
        String bombId = getEntities(res, "bomb").get(0).getId();

        // Cannot use not in inventory.
        assertThrows(InvalidActionException.class, () -> dmc.tick(invinPotionId));
        assertThrows(InvalidActionException.class, () -> dmc.tick(invisPotionId));
        assertThrows(InvalidActionException.class, () -> dmc.tick(bombId));
        
        // Items still on map.
        assertEquals(1, getEntities(res, "invincibility_potion").size());
        assertEquals(1, getEntities(res, "invisibility_potion").size());
        assertEquals(4, getEntities(res, "bomb").size());
    }

    @Test
    @DisplayName("Test player collect sun stone.")
    public void collectSunStone() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_collectablesTests", "c_collectablesTests");

        // Initially 5 sun_stone on map
        assertEquals(5, getEntities(res, "sun_stone").size());
        
        // pick up 1 sun stone
        res = dmc.tick(Direction.LEFT);

        // 4 sun stone on map
        assertEquals(4, getEntities(res, "sun_stone").size());

        // 1 sun stone in inventory
        assertEquals(1, getInventory(res, "sun_stone").size());

        assertEquals(new Position(-1, 0), getPlayer(res).get().getPosition());
    }


    @Test
    @DisplayName("Test player collect multiple sun stones.")
    public void collectSunStoneMultiple() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_collectablesTests", "c_collectablesTests");

        // Initially 5 sun_stone on map
        assertEquals(5, getEntities(res, "sun_stone").size());
        
        // pick up 1 sun stone
        res = dmc.tick(Direction.LEFT);

        // 4 sun stone on map
        assertEquals(4, getEntities(res, "sun_stone").size());

        // 1 sun stone in inventory
        assertEquals(1, getInventory(res, "sun_stone").size());

        assertEquals(new Position(-1, 0), getPlayer(res).get().getPosition());

        // pick up 2nd sun stone
        res = dmc.tick(Direction.LEFT);

        // 3 sun stone on map
        assertEquals(3, getEntities(res, "sun_stone").size());

        // 2 sun stone in inventory
        assertEquals(2, getInventory(res, "sun_stone").size());

        assertEquals(new Position(-2, 0), getPlayer(res).get().getPosition());
    }


    @Test
    @DisplayName("Test sun stone unlock doors, retained.")
    public void collectSunStoneUnlockDoor() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_collectablesTests", "c_collectablesTests");

        // Initially 5 sun_stone on map
        assertEquals(5, getEntities(res, "sun_stone").size());
        
        // pick up 1 sun stone
        res = dmc.tick(Direction.LEFT);

        // 4 sun stone on map
        assertEquals(4, getEntities(res, "sun_stone").size());
        assertEquals(4, getEntities(res, "door").size());
        
        // 1 sun stone in inventory
        assertEquals(1, getInventory(res, "sun_stone").size());
        
        assertEquals(new Position(-1, 0), getPlayer(res).get().getPosition());
        
        // Don't pick up key 3
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(-4, 1), getPlayer(res).get().getPosition());
        
        // Unlock door 3 without key 3, just sun stone
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(-4, 2), getPlayer(res).get().getPosition());
        // Player still has sunstone
        assertEquals(1, getInventory(res, "sun_stone").size());
        // 4->3 locked doors, but both locked and unlocked doors "startwith" "door"
        assertEquals(4, getEntities(res, "door").size());
        // 0->1 unlocked doors
        assertEquals(1, getEntities(res, "door_open").size());
        
        res = dmc.tick(Direction.DOWN);
        // Unlock door 4 without key 4, just sun stone
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(-4, 4), getPlayer(res).get().getPosition());

        // 3->2 locked doors, but all 4 locked and unlocked doors "startwith" "door"
        assertEquals(4, getEntities(res, "door").size());
        // 1->2 unlocked doors
        assertEquals(2, getEntities(res, "door_open").size());
        assertEquals(4, getEntities(res, "key").size());
    }

    @Test
    @DisplayName("Test sun stone can build shield (interchangeable with treasure), retained.")
    public void collectSunStoneBuildShield() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_collectablesTests", "c_collectablesTests");

        // Initially 5 sun_stone on map
        assertEquals(5, getEntities(res, "sun_stone").size());
        
        // pick up 1 sun stone
        res = dmc.tick(Direction.LEFT);
        
        // 4 sun stone on map
        assertEquals(4, getEntities(res, "sun_stone").size());
        
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        // Pick up 2 wood
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // 1 sunstone 2 wood in inventory
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(2, getInventory(res, "wood").size());
        assertEquals(new Position(2, -1), getPlayer(res).get().getPosition());
        
        // Build shield with 2 wood 1 sunstone. Sunstone retained.
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "shield").size());
     
    }

    @Test
    @DisplayName("Test sun stone can build multiple shields (interchangeable with treasure), retained.")
    public void collectSunStoneBuildMultipleShields() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_collectablesTests", "c_collectablesTests");

        // Initially 5 sun_stone on map
        assertEquals(5, getEntities(res, "sun_stone").size());
        
        // pick up 1 sun stone
        res = dmc.tick(Direction.LEFT);
        
        // 4 sun stone on map
        assertEquals(4, getEntities(res, "sun_stone").size());
        
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        // Pick up 2 wood
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        
        // 1 sunstone 2 wood in inventory
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(2, getInventory(res, "wood").size());
        assertEquals(new Position(2, -1), getPlayer(res).get().getPosition());
        
        // Build shield with 2 wood 1 sunstone. Sunstone retained.
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "shield").size());
        
        // Pick up 2 more wood
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        
        // 1 sunstone 2 wood in inventory
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(2, getInventory(res, "wood").size());
        assertEquals(new Position(4, -1), getPlayer(res).get().getPosition());
        
        // Build shield with 2 wood 1 sunstone. Sunstone retained.
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(2, getInventory(res, "shield").size());
    }
    
    @Test
    @DisplayName("Test sun stone use sun stone to build shield before treasure or key (assumed behaviour).")
    public void collectSunStoneBuildShieldBeforeTreasure() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_collectablesTests", "c_collectablesTests");

        // Initially 5 sun_stone on map
        assertEquals(5, getEntities(res, "sun_stone").size());
        
        // pick up 1 sun stone
        res = dmc.tick(Direction.LEFT);
        
        // 4 sun stone on map
        assertEquals(4, getEntities(res, "sun_stone").size());
        
        res = dmc.tick(Direction.RIGHT);
        // Pick up a treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(7, getEntities(res, "treasure").size());
        assertEquals(1, getInventory(res, "treasure").size());

        // Pick up 2 wood
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        
        // 1 sunstone 2 wood in inventory
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(2, getInventory(res, "wood").size());
        assertEquals(new Position(2, -1), getPlayer(res).get().getPosition());
        
        // Build shield with 2 wood 1 sunstone instad of treasure. Sunstone retained.
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "shield").size());
        
        // Pick up 2 more wood
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        
        // 1 sunstone 2 wood in inventory
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(2, getInventory(res, "wood").size());
        assertEquals(new Position(4, -1), getPlayer(res).get().getPosition());
        
        // Build shield with 2 wood 1 sunstone instead of wood. Sunstone retained.
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(2, getInventory(res, "shield").size());
    }

    @Test
    @DisplayName("Test sun stone counts to treasure goal. treasure_goal = 5")
    public void sunstoneTreasureGoal() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_goalTest_treasureExitSunstone", "c_goalTest_treasureExitSunstone");

        // Initially 5 sun_stone on map
        assertEquals(5, getEntities(res, "sun_stone").size());
        
        // pick up 5 sun stone and fulfil treasure goal
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        
        assertTrue(!res.getGoals().contains(":treasure"));
        assertTrue(res.getGoals().contains(":exit"));
        assertEquals(0, getEntities(res, "sun_stone").size());
        assertEquals(5, getInventory(res, "sun_stone").size());
        assertEquals(new Position(-5, 0), getPlayer(res).get().getPosition());
        
        // Exit
        res = dmc.tick(Direction.LEFT);

        // Game has been won, DungeonResponse.goals is empty string.
        assertTrue(res.getGoals().length() == 0);
        
    }

    @Test
    @DisplayName("Test sun stone counts to treasure goal, spent on building shield, but retained so treasure goal stil met. Treasure goal = 1")
    public void sunStoneBuildShieldRetainSunStoneForTreasureGoal() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_goalTest_treasureSunstoneBuild", "c_goalTest_treasureSunstoneBuild");

        // Initially 5 sun_stone, 2 wood on map
        assertEquals(5, getEntities(res, "sun_stone").size());
        assertEquals(2, getEntities(res, "wood").size());
        
        // pick up 1 sun stone
        res = dmc.tick(Direction.LEFT);
        // 4 sun stone on map
        assertEquals(4, getEntities(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        
        res = dmc.tick(Direction.DOWN);
        
        // Pick up 2 wood
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(-3, 1), getPlayer(res).get().getPosition());
        assertEquals(0, getEntities(res, "wood").size());
        assertEquals(2, getInventory(res, "wood").size());

        // Build shield, consumes 2 wood but not the sun stone
        res = assertDoesNotThrow(() -> {return dmc.build("shield");});
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "shield").size());

        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        // exit
        res = dmc.tick(Direction.UP);
        
        // game won, all goals met
        assertTrue(res.getGoals().length() == 0);

    }

    @Test
    @DisplayName("Test sun stone cannot use as item.")
    void sunstoneCannotUse() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_collectablesTests", "c_collectablesTests");

        // Initially 5 sun_stone on map
        assertEquals(5, getEntities(res, "sun_stone").size());

        // Use sunstone from map throws both expections (which one first is implementation specific)
        String sunstone2 = getEntities(res, "sun_stone").get(2).getId();
        assertThrows(InvalidActionException.class, () -> dmc.tick(sunstone2));
        
        // Pick up 1 sunstone
        res = dmc.tick(Direction.LEFT);
        assertEquals(4, getEntities(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "sun_stone").size());

        // Use sunstone from inventory throws IllegalArgumentException
        String sunstone1 = getInventory(res, "sun_stone").get(0).getId();
        assertThrows(IllegalArgumentException.class, () -> dmc.tick(sunstone1));

    }

    @Test
    @DisplayName("Test sun stone cannot build.")
    void sunstoneCannotBuild() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_collectablesTests", "c_collectablesTests");

        // Initially 5 sun_stone on map
        assertEquals(5, getEntities(res, "sun_stone").size());
        
        // Pick up 1 sunstone
        res = dmc.tick(Direction.LEFT);
        assertEquals(4, getEntities(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "sun_stone").size());

        // Build sunstone as item throws IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> dmc.build("sun_stone"));

    }

    @Test
    @DisplayName("Test sun stone cannot interact.")
    void sunstoneCannotInteract() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_collectablesTests", "c_collectablesTests");

        // Initially 5 sun_stone on map
        assertEquals(5, getEntities(res, "sun_stone").size());

        // Interact sunstone on map throws IllegalArgumentException
        String sunstone1 = getEntities(res, "sun_stone").get(2).getId();
        assertThrows(IllegalArgumentException.class, () -> dmc.interact(sunstone1));
        
        // Pick up 1 sunstone
        res = dmc.tick(Direction.LEFT);
        assertEquals(4, getEntities(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        
        // Interact sunstone in inventory throws IllegalArgumentException
        String sunstone2 = getInventory(res, "sun_stone").get(0).getId();
        assertThrows(IllegalArgumentException.class, () -> dmc.interact(sunstone2));

    }


}
