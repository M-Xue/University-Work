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
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestMidnightArmour {

    @Test
    @DisplayName("Test build midnight armour. Must not have zombies currently in the dungeon")
    void buildMidnightArmour() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests_midnightNoZombies", "c_buildablesTests_midnightNoZombies");

        assertEquals(5, getEntities(res, "sword").size());
        assertEquals(5, getEntities(res, "sun_stone").size());

        // Pick up sunstone
        res = dmc.tick(Direction.LEFT);
        // Pick up sword
        res = dmc.tick(Direction.DOWN);

        assertEquals(new Position(-1, 1), getPlayer(res).get().getPosition());

        assertEquals(4, getEntities(res, "sword").size());
        assertEquals(4, getEntities(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "midnight_armour").size());

        // Build midnight armour
        System.out.println("Building midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armourBuilding midnight armour");
        System.out.println(res.getBuildables());
        assertTrue(res.getBuildables().contains("midnight_armour"));
        res = assertDoesNotThrow(() -> {
            return dmc.build("midnight_armour");
        });
        assertEquals(0, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "midnight_armour").size());

        // Cannot build midnight armour again, no sword
        assertFalse(res.getBuildables().contains("midnight_armour"));
        assertThrows(InvalidActionException.class, () -> dmc.build("midnight_armour"));
        assertEquals(1, getInventory(res, "midnight_armour").size());

    }

    @Test
    @DisplayName("Test cannot build midnight armour with zombies currently in the dungeon")
    void cannotBuildMidnightArmourZombies() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests_midnightZombies", "c_buildablesTests_midnightZombies");

        assertEquals(5, getEntities(res, "sword").size());
        assertEquals(5, getEntities(res, "sun_stone").size());

        // Pick up sunstone
        res = dmc.tick(Direction.LEFT);
        // Pick up sword
        res = dmc.tick(Direction.DOWN);

        assertEquals(new Position(-1, 1), getPlayer(res).get().getPosition());

        assertEquals(4, getEntities(res, "sword").size());
        assertEquals(4, getEntities(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "midnight_armour").size());

        // Cannot build midnight armour in presnece of zombies
        assertFalse(res.getBuildables().contains("midnight_armour"));
        assertThrows(InvalidActionException.class, () -> dmc.build("midnight_armour"));
        assertEquals(1, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "midnight_armour").size());

    }

    @Test
    @DisplayName("Test cannot use midnight armour")
    void cannotUseMidnightArmour() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests_midnightNoZombies", "c_buildablesTests_midnightNoZombies");

        assertEquals(5, getEntities(res, "sword").size());
        assertEquals(5, getEntities(res, "sun_stone").size());

        // Pick up sunstone
        res = dmc.tick(Direction.LEFT);
        // Pick up sword
        res = dmc.tick(Direction.DOWN);

        assertEquals(new Position(-1, 1), getPlayer(res).get().getPosition());

        assertEquals(4, getEntities(res, "sword").size());
        assertEquals(4, getEntities(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "midnight_armour").size());

        // Build midnight armour
        assertTrue(res.getBuildables().contains("midnight_armour"));
        res = assertDoesNotThrow(() -> {
            return dmc.build("midnight_armour");
        });
        assertEquals(0, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "midnight_armour").size());

        // Cannot use midnight armour
        String midnightArmour = getInventory(res, "midnight_armour").get(0).getId();
        assertThrows(IllegalArgumentException.class, () -> dmc.tick(midnightArmour));

        assertEquals(1, getInventory(res, "midnight_armour").size());
    }

    @Test
    @DisplayName("Test cannot interact with midnight armour")
    void cannotInteractMidnightArmour() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests_midnightNoZombies", "c_buildablesTests_midnightNoZombies");

        assertEquals(5, getEntities(res, "sword").size());
        assertEquals(5, getEntities(res, "sun_stone").size());

        // Pick up sunstone
        res = dmc.tick(Direction.LEFT);
        // Pick up sword
        res = dmc.tick(Direction.DOWN);

        assertEquals(new Position(-1, 1), getPlayer(res).get().getPosition());

        assertEquals(4, getEntities(res, "sword").size());
        assertEquals(4, getEntities(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "midnight_armour").size());

        // Build midnight armour
        assertTrue(res.getBuildables().contains("midnight_armour"));
        res = assertDoesNotThrow(() -> {
            return dmc.build("midnight_armour");
        });
        assertEquals(0, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "midnight_armour").size());

        // Cannot interact with midnight armour
        String midnightArmour = getInventory(res, "midnight_armour").get(0).getId();
        assertThrows(IllegalArgumentException.class, () -> dmc.interact(midnightArmour));

        assertEquals(1, getInventory(res, "midnight_armour").size());
    }

    @Test
    @DisplayName("Test build midnight armour insufficient materials: 0/1 sword, 1 sunstone")
    void cannotBuildMidnightArmourInsufficientMaterials1() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests_midnightNoZombies", "c_buildablesTests_midnightNoZombies");

        assertEquals(5, getEntities(res, "sword").size());
        assertEquals(5, getEntities(res, "sun_stone").size());

        // Pick up sunstone
        res = dmc.tick(Direction.LEFT);

        assertEquals(new Position(-1, 0), getPlayer(res).get().getPosition());

        assertEquals(5, getEntities(res, "sword").size());
        assertEquals(4, getEntities(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "sun_stone").size());

        // Cannot Build midnight armour, no sword
        assertFalse(res.getBuildables().contains("midnight_armour"));
        assertThrows(InvalidActionException.class, () -> dmc.build("midnight_armour"));
        assertEquals(0, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "midnight_armour").size());

    }

    @Test
    @DisplayName("Test build midnight armour insufficient materials: 1 sword, 0/1 sunstone")
    void cannotBuildMidnightArmourInsufficientMaterials2() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildablesTests_midnightNoZombies", "c_buildablesTests_midnightNoZombies");

        assertEquals(5, getEntities(res, "sword").size());
        assertEquals(5, getEntities(res, "sun_stone").size());

        res = dmc.tick(Direction.DOWN);
        // Pick up sword
        res = dmc.tick(Direction.LEFT);

        assertEquals(new Position(-1, 1), getPlayer(res).get().getPosition());

        assertEquals(4, getEntities(res, "sword").size());
        assertEquals(5, getEntities(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "sword").size());
        assertEquals(0, getInventory(res, "sun_stone").size());

        // Cannot Build midnight armour, no sunsotne
        assertFalse(res.getBuildables().contains("midnight_armour"));
        assertThrows(InvalidActionException.class, () -> dmc.build("midnight_armour"));
        assertEquals(1, getInventory(res, "sword").size());
        assertEquals(0, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "midnight_armour").size());

    }

}
