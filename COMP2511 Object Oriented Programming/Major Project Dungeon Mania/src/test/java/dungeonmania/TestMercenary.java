package dungeonmania;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dungeonmania.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestMercenary {
    @Test
    @DisplayName("Test mercenary basic battle")
    public void mercenaryBattle() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_battleTest_basicMercenary", "c_complexGoalsTest_andAll");
        DungeonResponse a = dmc.tick(Direction.DOWN);
        DungeonResponse b = dmc.tick(Direction.DOWN);
        DungeonResponse c = dmc.tick(Direction.DOWN);
        DungeonResponse d = dmc.tick(Direction.DOWN);
        DungeonResponse e = dmc.tick(Direction.DOWN);
        
        
    }
    
    @Test
    @DisplayName("Test mercenary init")
    public void mercenaryInit() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("2_doors", "c_complexGoalsTest_andAll");
        EntityResponse initPlayer = getPlayer(initDungonRes).get();
    }

    @Test
    @DisplayName("Test sun stone cannot bribe mercenaries. Bribe amount 1, bribe radius 1, merc health 1, merc attack 1.")
    public void sunStoneCannotBribeMercenaries() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bribeTest_mercenary", "c_bribeTest_mercenary");

        // Initially 5 sun_stone on map
        assertEquals(5, getEntities(res, "sun_stone").size());
        
        // pick up 1 sun stone
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "treasure").size());
        
        // 4 sun stone on map
        assertEquals(4, getEntities(res, "sun_stone").size());
        
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(0, 0), getPlayer(res).get().getPosition());
        
        assertEquals(new Position(0, 1), getEntities(res, "mercenary").get(0).getPosition());
        
        // Cannot bribe mercenary with just sun stone
        String mercenary1 = getEntities(res, "mercenary").get(0).getId();
        assertThrows(InvalidActionException.class, () -> dmc.interact(mercenary1));
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getEntities(res, "mercenary").size());
        
        // Battle and kill mercenary
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 1), getPlayer(res).get().getPosition());
        assertEquals(0, getEntities(res, "mercenary").size());
           
    }

    @Test
    @DisplayName("Test sceptre form allies with mercenaries without distance constraint, bribe_radius=1 mind_control_duration = 6, enemy_goal=1, goals (enemies and exit)")
    void sceptreAllyMercenary() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_allyTest_mercenary", "c_allyTest_mercenary");

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
        
        EntityResponse mercenary = getEntities(res, "mercenary").get(0);
        assertEquals(new Position(0, 6), mercenary.getPosition());
        
        // mind control mercenary
        res = assertDoesNotThrow(() -> {return dmc.interact(mercenary.getId());});
        
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 0), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 5), getEntities(res, "mercenary").get(0).getPosition());
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 1), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 4), getEntities(res, "mercenary").get(0).getPosition());
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 2), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 3), getEntities(res, "mercenary").get(0).getPosition());

        // player and allied mercenary cross paths, no battle
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 3), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 2), getEntities(res, "mercenary").get(0).getPosition());
        
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 4), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 3), getEntities(res, "mercenary").get(0).getPosition());

        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 5), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 4), getEntities(res, "mercenary").get(0).getPosition());

        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 6), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 5), getEntities(res, "mercenary").get(0).getPosition());
        
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 7), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 6), getEntities(res, "mercenary").get(0).getPosition());
        
        // No longer allied, battle
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(0, 6), getPlayer(res).get().getPosition());
        assertEquals(0, getEntities(res, "mercenary").size());
        
        // Defeated merceanry, enemies goal met
        assertFalse(getGoals(res).contains(":enemies"));
        assertTrue(getGoals(res).contains(":exit"));
        
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        // exit
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 10), getPlayer(res).get().getPosition());
        
        // all goals achieved, game won
        assertTrue(getGoals(res).length() == 0);

    }

    /**
     * Player with midnight armour battle mercenary
     */

    @Test
    @DisplayName("Test midnight armour attack bonus. Player wouldnt have defeated mercenary without it.")
    void midnightArmourAttackBonus() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battlesTests_midnightAttackMercenary", "c_battlesTests_midnightAttackMercenary");

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
        
        EntityResponse mercenary = getEntities(res, "mercenary").get(0);
        assertEquals(new Position(0, 7), getEntities(res, "mercenary").get(0).getPosition());
        
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 2), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 6), getEntities(res, "mercenary").get(0).getPosition());

        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 3), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 5), getEntities(res, "mercenary").get(0).getPosition());
        
        // Battle with mercenary and win bc midnight armour bonuses
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 4), getPlayer(res).get().getPosition());
        assertEquals(0, getEntities(res, "mercenary").size());
        
        assertFalse(getGoals(res).contains(":enemies"));
        assertTrue(getGoals(res).contains(":exit"));
        
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 5), getPlayer(res).get().getPosition());
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 6), getPlayer(res).get().getPosition());
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 7), getPlayer(res).get().getPosition());
        res = dmc.tick(Direction.DOWN);
        
        // Exit, game won.
        assertTrue(getGoals(res).length() == 0);

        
    }

    @Test
    @DisplayName("Test midnight armour defence bonus. Player wouldnt have defeated mercenary without it.")
    void midnightArmourDefenceBonus() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battlesTests_midnightDefenceMercenary", "c_battlesTests_midnightDefenceMercenary");

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
        
        EntityResponse mercenary = getEntities(res, "mercenary").get(0);
        assertEquals(new Position(0, 7), mercenary.getPosition());
        
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 2), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 6), getEntities(res, "mercenary").get(0).getPosition());

        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 3), getPlayer(res).get().getPosition());
        assertEquals(new Position(0, 5), getEntities(res, "mercenary").get(0).getPosition());
        
        // Battle with mercenary and win bc midnight armour bonuses
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 4), getPlayer(res).get().getPosition());
        assertEquals(0, getEntities(res, "mercenary").size());
        
        assertFalse(getGoals(res).contains(":enemies"));
        assertTrue(getGoals(res).contains(":exit"));
        
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 5), getPlayer(res).get().getPosition());
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 6), getPlayer(res).get().getPosition());
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(0, 7), getPlayer(res).get().getPosition());
        res = dmc.tick(Direction.DOWN);
        
        // Exit, game won.
        assertTrue(getGoals(res).length() == 0);
        
    }

    @Test
    public void testMercenaryPathing() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("pathing_merc_maze", "c_battleTests_basicMercenaryPlayerDies");
        DungeonResponse a = dmc.tick(Direction.LEFT);
        DungeonResponse b = dmc.tick(Direction.LEFT);
        DungeonResponse c = dmc.tick(Direction.LEFT);
        DungeonResponse d = dmc.tick(Direction.LEFT);
        DungeonResponse e = dmc.tick(Direction.LEFT);


        List<EntityResponse> merc = e.getEntities();
        for (EntityResponse f: merc) {
            if (f.getType().equals("mercenary")) {
                assertEquals(f.getPosition().getX(), 18);
                assertEquals(f.getPosition().getY(), 11);
            }
        }
    }

    @Test
    public void testMercenaryPortal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("pathing_merc", "c_battleTests_basicMercenaryPlayerDies");
        DungeonResponse a = dmc.tick(Direction.LEFT);
        DungeonResponse b = dmc.tick(Direction.LEFT);
        DungeonResponse c = dmc.tick(Direction.LEFT);
        DungeonResponse d = dmc.tick(Direction.LEFT);
        DungeonResponse e = dmc.tick(Direction.LEFT);


        List<EntityResponse> merc = e.getEntities();
        for (EntityResponse f: merc) {
            if (f.getType().equals("mercenary")) {
                assertNotEquals(f.getPosition().getX(), 2);
                assertNotEquals(f.getPosition().getY(), -1);
            }
        }
    }

    @Test
    public void testMercenaryPortalWall() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("pathing_merc_wall", "c_battleTests_basicMercenaryPlayerDies");
        DungeonResponse a = dmc.tick(Direction.LEFT);
        DungeonResponse b = dmc.tick(Direction.LEFT);
        DungeonResponse c = dmc.tick(Direction.LEFT);
        DungeonResponse d = dmc.tick(Direction.LEFT);
        DungeonResponse e = dmc.tick(Direction.LEFT);


        List<EntityResponse> merc = e.getEntities();
        for (EntityResponse f: merc) {
            if (f.getType().equals("mercenary")) {
                assertEquals(f.getPosition().getX(), 2);
                assertEquals(f.getPosition().getY(), -1);
            }
        }
    }

    @Test
    public void testMercenarySwamp() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("pathing_merc_swamp", "c_battleTests_basicMercenaryPlayerDies");
        DungeonResponse a = dmc.tick(Direction.LEFT);
        DungeonResponse b = dmc.tick(Direction.LEFT);
        DungeonResponse c = dmc.tick(Direction.LEFT);
        DungeonResponse d = dmc.tick(Direction.LEFT);
        DungeonResponse e = dmc.tick(Direction.LEFT);
        DungeonResponse g = dmc.tick(Direction.LEFT);

        List<EntityResponse> merc = c.getEntities();
        for (EntityResponse f: merc) {
            if (f.getType().equals("mercenary")) {
                assertNotEquals(f.getPosition(), new Position(1, 1));
            }
        }
    }

    @Test
    public void testMercenaryDoor() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("pathing_merc_closeddoor", "c_battleTests_basicMercenaryPlayerDies");
        DungeonResponse a = dmc.tick(Direction.LEFT);
        DungeonResponse b = dmc.tick(Direction.LEFT);

        List<EntityResponse> merc = b.getEntities();
        for (EntityResponse f: merc) {
            if (f.getType().equals("mercenary")) {
                assertEquals(f.getPosition().getX(), 2);
                assertEquals(f.getPosition().getY(), 0);
            }
        }
    }

    @Test
    public void testMercenaryImpossible() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("pathing_merc_impossible", "c_battleTests_basicMercenaryPlayerDies");
        DungeonResponse a = dmc.tick(Direction.LEFT);
        DungeonResponse b = dmc.tick(Direction.LEFT);
        DungeonResponse c = dmc.tick(Direction.LEFT);

        List<EntityResponse> merc = c.getEntities();
        for (EntityResponse f: merc) {
            if (f.getType().equals("mercenary")) {
                assertEquals(f.getPosition().getX(), 4);
                assertEquals(f.getPosition().getY(),3);
            }
        }

        List<EntityResponse> merc2 = b.getEntities();
        for (EntityResponse f: merc2) {
            if (f.getType().equals("mercenary")) {
                assertEquals(f.getPosition().getX(), 4);
                assertEquals(f.getPosition().getY(),3);
            }
        }
    }

    @Test
    public void testMercenaryImpossibleDoor() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("pathing_merc_impossibledoor", "c_battleTests_basicMercenaryPlayerDies");
        DungeonResponse a = dmc.tick(Direction.LEFT);
        DungeonResponse b = dmc.tick(Direction.LEFT);
        DungeonResponse c = dmc.tick(Direction.LEFT);

        List<EntityResponse> merc = c.getEntities();
        for (EntityResponse f: merc) {
            if (f.getType().equals("mercenary")) {
                assertEquals(f.getPosition().getX(), 4);
                assertEquals(f.getPosition().getY(),3);
            }
        }

        List<EntityResponse> merc2 = b.getEntities();
        for (EntityResponse f: merc2) {
            if (f.getType().equals("mercenary")) {
                assertEquals(f.getPosition().getX(), 4);
                assertEquals(f.getPosition().getY(),3);
            }
        }

    }

    @Test
    public void testMercenaryIntersection() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("pathing_merc_complex", "c_battleTests_basicMercenaryPlayerDies");
        for(int i = 0; i<10; i++) {
            dmc.tick(Direction.UP);
        }
        DungeonResponse a = dmc.tick(Direction.UP);
        DungeonResponse b = dmc.tick(Direction.UP);
        DungeonResponse c = dmc.tick(Direction.UP);


        List<EntityResponse> merc = c.getEntities();
        for (EntityResponse f: merc) {
            if (f.getType().equals("mercenary")) {
                assertEquals(f.getPosition().getX(), 3);
                assertEquals(f.getPosition().getY(),2);
            }
        }

    }
}
