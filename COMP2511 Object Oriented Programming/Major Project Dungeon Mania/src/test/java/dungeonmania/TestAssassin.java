package dungeonmania;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static dungeonmania.TestUtils.*;

public class TestAssassin {

    @Test
    @DisplayName("Test sun stone cannot bribe assassins. Bribe amount 1, bribe radius 1, merc health 1, merc attack 1.")
    public void sunStoneCannotBribeMercenaries() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bribeTest_assassin", "c_bribeTest_assassin");

        // Initially 5 sun_stone on map
        assertEquals(5, getEntities(res, "sun_stone").size());
        
        // pick up 1 sun stone
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "treasure").size());
        
        // 4 sun stone on map
        assertEquals(4, getEntities(res,    "sun_stone").size());
        
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(0, 0), getPlayer(res).get().getPosition());
        
        assertEquals(new Position(0, 1), getEntities(res, "assassin").get(0).getPosition());
        
        // Cannot bribe assassin with just sun stone
        String assassin1 = getEntities(res, "assassin").get(0).getId();
        assertThrows(InvalidActionException.class, () -> dmc.interact(assassin1));
        assertEquals(1, getInventory(res, "sun_stone").size());
        
        // Assassin still alive
        assertEquals(1, getEntities(res, "assassin").size());
        
        
    }

    @Test
    @DisplayName("Test sceptre form allies with assassins without distance constraint.")
    void sceptreAllyAssassin() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_allyTest_assassin", "c_allyTest_assassin");
        // bribe_radius=1,
        // mind_control_duration=6
        // assassin_bribe_amount: 3,
        // assassin_bribe_fail_rate: 0,
        // assassin_recon_radius: 100,
        
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

        res = dmc.tick(Direction.LEFT);

        assertEquals(new Position(0, -1), getPlayer(res).get().getPosition());
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
        
        EntityResponse assassin = getEntities(res, "assassin").get(0);
        assertEquals(new Position(0, 6), assassin.getPosition());
        
        // mind control assassin
        res = assertDoesNotThrow(() -> {return dmc.interact(assassin.getId());});
        
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 0), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 5), getEntities(res, "assassin").get(0).getPosition());
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 1), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 4), getEntities(res, "assassin").get(0).getPosition());
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 2), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 3), getEntities(res, "assassin").get(0).getPosition());

        // player and allied assassin cross paths, no battle
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 3), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 2), getEntities(res, "assassin").get(0).getPosition());
        
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 4), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 3), getEntities(res, "assassin").get(0).getPosition());

        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 5), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 4), getEntities(res, "assassin").get(0).getPosition());

        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 6), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 5), getEntities(res, "assassin").get(0).getPosition());
        
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 7), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 6), getEntities(res, "assassin").get(0).getPosition());
        
        // No longer allied, battle
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        assertEquals(0, getEntities(res, "assassin").size());

    }


    /**
     * Player with midnight armour battle assassin
     */

    @Test
    @DisplayName("Test midnight armour attack bonus. Player wouldnt have defeated assassin without it.")
    void midnightArmourAttackBonus() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battlesTests_midnightAttackAssassin", "c_battlesTests_midnightAttackAssassin");

        assertEquals(5, getEntities(res, "sword").size());
        assertEquals(5, getEntities(res, "sun_stone").size());
        assertEquals(1, getEntities(res, "assassin").size());

        // Pick up sunstone
        res = dmc.tick(Direction.LEFT);
        // Pick up sword
        res = dmc.tick(Direction.DOWN);

        res = dmc.tick(Direction.RIGHT);

        assertEquals(new Position(0, 1), getPlayer(res).get().getPosition());
        
        assertEquals(4, getEntities(res, "sword").size());
        assertEquals(4, getEntities(res, "sun_stone").size());
        
        // Build midnight armour
        assertTrue(res.getBuildables().contains("midnight_armour"));
        res = assertDoesNotThrow(() -> {return dmc.build("midnight_armour");});
        assertEquals(0, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "midnight_armour").size());
        
        EntityResponse assassin = getEntities(res, "assassin").get(0);
        assertEquals(new Position(0, 7), assassin.getPosition());
        
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 2), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 6), getEntities(res, "assassin").get(0).getPosition());

        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 3), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 5), getEntities(res, "assassin").get(0).getPosition());
        
        // Battle with assassin and win bc midnight armour bonuses
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 4), getPlayer(res).get().getPosition());
        assertEquals(0, getEntities(res, "assassin").size());
        
        assertFalse(res.getGoals().contains(":enemies"));
        assertTrue(res.getGoals().contains(":exit"));
        
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 5), getPlayer(res).get().getPosition());
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 6), getPlayer(res).get().getPosition());
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 7), getPlayer(res).get().getPosition());
        res = dmc.tick(Direction.DOWN);
        
        // Exit, game won.
        assertTrue(res.getGoals().length() == 0);

        
    }

    @Test
    @DisplayName("Test midnight armour defence bonus. Player wouldnt have defeated assassin without it.")
    void midnightArmourDefenceBonus() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battlesTests_midnightDefenceAssassin", "c_battlesTests_midnightDefenceAssassin");

        assertEquals(5, getEntities(res, "sword").size());
        assertEquals(5, getEntities(res, "sun_stone").size());

        // Pick up sunstone
        res = dmc.tick(Direction.LEFT);
        // Pick up sword
        res = dmc.tick(Direction.DOWN);

        res = dmc.tick(Direction.RIGHT);

        assertEquals(new Position(0, 1), getPlayer(res).get().getPosition());
        
        assertEquals(4, getEntities(res, "sword").size());
        assertEquals(4, getEntities(res, "sun_stone").size());
        
        // Build midnight armour
        assertTrue(res.getBuildables().contains("midnight_armour"));
        res = assertDoesNotThrow(() -> {return dmc.build("midnight_armour");});
        assertEquals(0, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "midnight_armour").size());
        
        EntityResponse assassin = getEntities(res, "assassin").get(0);
        assertEquals(new Position(0, 7), assassin.getPosition());
        
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 2), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 6), getEntities(res, "assassin").get(0).getPosition());

        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 3), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 5), getEntities(res, "assassin").get(0).getPosition());
        
        // Battle with assassin and win bc midnight armour bonuses
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 4), getPlayer(res).get().getPosition());
        assertEquals(0, getEntities(res, "assassin").size());
        
        assertFalse(res.getGoals().contains(":enemies"));
        assertTrue(res.getGoals().contains(":exit"));
        
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 5), getPlayer(res).get().getPosition());
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 6), getPlayer(res).get().getPosition());
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 7), getPlayer(res).get().getPosition());
        res = dmc.tick(Direction.DOWN);
        
        // Exit, game won.
        assertTrue(res.getGoals().length() == 0);
        
    }



}
