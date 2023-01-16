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


public class TestGoals {
    @Test
    @DisplayName("Test exit goal can be completed")
    public void testExitGoalSimple() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_goalTest_exitGoal", "c_movementTest_testMovementDown");
        EntityResponse initPlayer = getPlayer(initDungonRes).get();

        // assert player and exit are in correct initial positions
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        EntityResponse exit = getEntitiesStream(initDungonRes, "exit").findFirst().get();
        EntityResponse expectedExit = new EntityResponse(exit.getId(), exit.getType(), new Position(0, 1), false);
        assertEquals(expectedExit, exit);

        // assert exit goal is active
        assertTrue(getGoals(initDungonRes).contains(":exit"));

        // move player downward
        DungeonResponse newDungonRes = dmc.tick(Direction.DOWN);
        EntityResponse updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 1), false);
        assertEquals(expectedPlayer, updatedPlayer);

        // check exit goal is complete
        assertEquals("", getGoals(newDungonRes));
    }

    @Test
    @DisplayName("Test boulder goal can be completed")
    public void testBoulderGoalSimple() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_goalTest_boulderGoal", "c_movementTest_testMovementDown");
        EntityResponse initPlayer = getPlayer(initDungonRes).get();

        // assert entities are in correct initial positions
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        EntityResponse boulder = getEntitiesStream(initDungonRes, "boulder").findFirst().get();
        EntityResponse expectedBoulder = new EntityResponse(boulder.getId(), boulder.getType(), new Position(0, 1), false);
        assertEquals(expectedBoulder, boulder);

        EntityResponse switch_ = getEntitiesStream(initDungonRes, "switch").findFirst().get();
        EntityResponse expectedSwitch = new EntityResponse(switch_.getId(), switch_.getType(), new Position(0, 2), false);
        assertEquals(expectedSwitch, switch_);

        // assert boulder goal is active
        assertTrue(getGoals(initDungonRes).contains(":boulders"));

        // move player downward
        DungeonResponse newDungonRes = dmc.tick(Direction.DOWN);
        EntityResponse updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 1), false);
        assertEquals(expectedPlayer, updatedPlayer);

        // check boulder goal is complete
        assertEquals("", getGoals(newDungonRes));
    }

    @Test
    @DisplayName("Test exit AND boulder conjunction goals can be completed")
    public void testAndConjunctionGoals() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_goalTest_conjunctionExitAndBoulderGoal", "c_movementTest_testMovementDown");
        EntityResponse initPlayer = getPlayer(initDungonRes).get();

        // assert boulder and exit goals are active
        assertTrue(getGoals(initDungonRes).contains(":boulders"));
        assertTrue(getGoals(initDungonRes).contains(":exit"));

        // move player downward
        DungeonResponse newDungonRes = dmc.tick(Direction.DOWN);

        // assert boulder goal is complete but exit goal is active
        assertFalse(getGoals(newDungonRes).contains(":boulders"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        // move player to exit
        newDungonRes = dmc.tick(Direction.RIGHT);

        // checking player is on exit
        EntityResponse updatedPlayer = getPlayer(newDungonRes).get();
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 1), false);
        assertEquals(expectedPlayer, updatedPlayer);
        EntityResponse exit = getEntitiesStream(initDungonRes, "exit").findFirst().get();
        EntityResponse expectedExit = new EntityResponse(exit.getId(), exit.getType(), new Position(1, 1), false);
        assertEquals(expectedExit, exit);

        // check conjunction goal is complete
        assertEquals("", getGoals(newDungonRes));

        //* Check exit isnt complete before boulder is complete ************************************************************************************/
        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_goalTest_conjunctionExitAndBoulderGoal", "c_movementTest_testMovementDown");
        // assert boulder and exit goals are active
        assertTrue(getGoals(initDungonRes).contains(":boulders"));
        assertTrue(getGoals(initDungonRes).contains(":exit"));
        initPlayer = getPlayer(initDungonRes).get();

        // go onto the exit
        dmc.tick(Direction.RIGHT);
        newDungonRes = dmc.tick(Direction.DOWN);

        // checking player is on exit
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 1), false);
        assertEquals(expectedPlayer, updatedPlayer);
        exit = getEntitiesStream(initDungonRes, "exit").findFirst().get();
        expectedExit = new EntityResponse(exit.getId(), exit.getType(), new Position(1, 1), false);
        assertEquals(expectedExit, exit);

        // assert boulder and exit goals are STILL active
        assertTrue(getGoals(newDungonRes).contains(":boulders"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        // go back to original position
        dmc.tick(Direction.UP);
        newDungonRes = dmc.tick(Direction.LEFT);
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        // assert boulder and exit goals are active
        assertTrue(getGoals(newDungonRes).contains(":boulders"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        // move player downward
        newDungonRes = dmc.tick(Direction.DOWN);

        // assert boulder goal is complete but exit goal is active
        assertFalse(getGoals(newDungonRes).contains(":boulders"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        // move player to exit
        newDungonRes = dmc.tick(Direction.RIGHT);
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 1), false);
        assertEquals(expectedPlayer, updatedPlayer);

        // check conjunction goal is complete
        assertEquals("", getGoals(newDungonRes));
    }

    @Test
    @DisplayName("Test exit OR boulder goals can be completed")
    public void testORGoals() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_goalTest_exitOrBoulderGoal", "c_movementTest_testMovementDown");
        EntityResponse initPlayer = getPlayer(initDungonRes).get();

        //* Finish boulder goal first **************************************************************************************************************/
        // assert boulder and exit goals are active
        assertTrue(getGoals(initDungonRes).contains(":boulders"));
        assertTrue(getGoals(initDungonRes).contains(":exit"));

        // move player downward
        DungeonResponse newDungonRes = dmc.tick(Direction.DOWN);

        // assert boulder goal is complete and hence dungeon is complete
        assertEquals("", getGoals(newDungonRes));

        //* Finish exit goal first *****************************************************************************************************************/
        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_goalTest_exitOrBoulderGoal", "c_movementTest_testMovementDown");
        initPlayer = getPlayer(initDungonRes).get();
        dmc.tick(Direction.RIGHT);
        newDungonRes = dmc.tick(Direction.DOWN);

        // Check player is on exit
        EntityResponse updatedPlayer = getPlayer(newDungonRes).get();
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 1), false);
        assertEquals(expectedPlayer, updatedPlayer);

        // check goal is complete
        assertEquals("", getGoals(newDungonRes));
    }

    @Test
    @DisplayName("Test multiple boulders for boulder goals can be completed")
    public void testMultipleBouldersGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_goalTest_multipleBoulderGoal", "c_movementTest_testMovementDown");

        assertTrue(getGoals(initDungonRes).contains(":boulders"));
        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT); // moving the first boulder on a switch
        assertTrue(getGoals(initDungonRes).contains(":boulders"));
        newDungonRes = dmc.tick(Direction.DOWN); // moving the second boulder on a switch
        assertEquals("", getGoals(newDungonRes));
        newDungonRes = dmc.tick(Direction.DOWN); // moving the second boulder off the switch
        assertTrue(getGoals(newDungonRes).contains(":boulders"));
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        newDungonRes = dmc.tick(Direction.UP); // moving the second boulder back on the switch
        assertEquals("", getGoals(newDungonRes));
    }

    @Test
    @DisplayName("Test treasure goal")
    public void testTreasureGoal() {
        //* BASIC */
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_goalTest_treasureTest", "c_goalTest_treasureTest");

        assertTrue(getGoals(initDungonRes).contains(":treasure"));
        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);
        assertTrue(getGoals(newDungonRes).contains(":treasure"));
        newDungonRes = dmc.tick(Direction.RIGHT);
        assertTrue(getGoals(newDungonRes).contains(":treasure"));
        newDungonRes = dmc.tick(Direction.RIGHT); // get the treasure
        assertEquals("", getGoals(newDungonRes));

        //* uncompleting treasure goal after using it to building */
        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_goalTest_treasureTest", "c_goalTest_treasureTest");
        assertTrue(getGoals(initDungonRes).contains(":treasure"));
        dmc.tick(Direction.RIGHT); // get the treasure
        dmc.tick(Direction.RIGHT); // get the treasure
        newDungonRes = dmc.tick(Direction.RIGHT); // get the treasure
        assertEquals("", getGoals(newDungonRes));
        dmc.tick(Direction.RIGHT); // get wood
        dmc.tick(Direction.RIGHT); // get wood
        assertEquals("", getGoals(newDungonRes));
        try {
            dmc.build("shield");
        } catch (Exception e) {
            assertTrue(false);
        }
        newDungonRes = dmc.getDungeonResponseModel();
        assertTrue(getGoals(newDungonRes).contains(":treasure"));
        newDungonRes = dmc.tick(Direction.RIGHT); // get the treasure
        assertEquals("", getGoals(newDungonRes));
    }

    @Test
    @DisplayName("Test enemies goal")
    public void testEnemiesGoal() {
        //* BASIC */
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_goalTest_enemiesSimple", "c_goalTest_enemiesSimple");

        assertTrue(getGoals(initDungonRes).contains(":enemies"));
        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT); // kill spider
        assertEquals("", getGoals(newDungonRes));

        //* Ensuring toasters need to break too */
        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_goalTest_enemiesToasterSpawnSimple", "c_goalTest_enemiesSimple");

        assertTrue(getGoals(initDungonRes).contains(":enemies"));
        newDungonRes = dmc.tick(Direction.RIGHT); // kill spider
        assertTrue(getGoals(newDungonRes).contains(":enemies"));
        newDungonRes = dmc.tick(Direction.RIGHT); // pick up sword
        EntityResponse spawner = getEntitiesStream(newDungonRes, "zombie_toast_spawner").findFirst().get();
        try {
            dmc.interact(spawner.getId());
        } catch (Exception e) {
            assertTrue(false);
        }
        newDungonRes = dmc.getDungeonResponseModel();
        assertEquals("", getGoals(newDungonRes));

        //* Killing 2 enemies and a toaster */
        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_goalTest_enemiesMultipleToasterSpawnSimple", "c_goalTest_enemiesMultipleToasterSpawnSimple");

        assertTrue(getGoals(initDungonRes).contains(":enemies"));
        newDungonRes = dmc.tick(Direction.RIGHT); // kill spider
        assertTrue(getGoals(newDungonRes).contains(":enemies"));
        newDungonRes = dmc.tick(Direction.RIGHT); // pick up sword
        spawner = getEntitiesStream(initDungonRes, "zombie_toast_spawner").findFirst().get();
        try {
            dmc.interact(spawner.getId());
        } catch (Exception e) {
            assertTrue(false);
        }

        dmc.tick(Direction.RIGHT); 
        newDungonRes = dmc.tick(Direction.RIGHT); // kill spider
        assertEquals("", getGoals(newDungonRes));
    }

    @Test
    @DisplayName("Test super goal")
    public void testSuperGoal() {
        //************************************************************ */
        //* AND subgoal: boulder AND (treasure AND exit) */
        //************************************************************ */

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_goalTest_superGoalAndSimple", "c_goalTest_simple");
        
        assertTrue(getGoals(initDungonRes).contains(":boulders"));
        assertTrue(getGoals(initDungonRes).contains(":treasure"));
        assertTrue(getGoals(initDungonRes).contains(":exit"));

        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT); // pushing boulder on switch

        assertFalse(getGoals(newDungonRes).contains(":boulders"));
        assertTrue(getGoals(newDungonRes).contains(":treasure"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));


        newDungonRes = dmc.tick(Direction.DOWN);

        assertFalse(getGoals(newDungonRes).contains(":boulders"));
        assertTrue(getGoals(newDungonRes).contains(":treasure"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        newDungonRes = dmc.tick(Direction.DOWN); // collecting treasure

        assertFalse(getGoals(newDungonRes).contains(":boulders"));
        assertFalse(getGoals(newDungonRes).contains(":treasure"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        newDungonRes = dmc.tick(Direction.UP); // going to exit
        assertEquals("", getGoals(newDungonRes));


        //************************************************************ */
        //* OR subgoal: boulder OR (treasure AND exit) */
        //************************************************************ */
        
        dmc = new DungeonManiaController();
        initDungonRes =  dmc.newGame("d_goalTest_superGoalOrSimple", "c_goalTest_simple");
        
        assertTrue(getGoals(initDungonRes).contains(":boulders"));
        assertTrue(getGoals(initDungonRes).contains(":treasure"));
        assertTrue(getGoals(initDungonRes).contains(":exit"));

        newDungonRes = dmc.tick(Direction.RIGHT); // push boulder onto switch

        assertFalse(getGoals(newDungonRes).contains(":boulders"));
        assertFalse(getGoals(newDungonRes).contains(":treasure"));
        assertFalse(getGoals(newDungonRes).contains(":exit"));
        assertEquals("", getGoals(newDungonRes));

        newDungonRes = dmc.tick(Direction.RIGHT); // push boulder off switch

        assertTrue(getGoals(newDungonRes).contains(":boulders"));
        assertTrue(getGoals(newDungonRes).contains(":treasure"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        dmc.tick(Direction.LEFT);
        newDungonRes = dmc.tick(Direction.DOWN);

        assertTrue(getGoals(newDungonRes).contains(":boulders"));
        assertTrue(getGoals(newDungonRes).contains(":treasure"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        newDungonRes = dmc.tick(Direction.DOWN);

        assertTrue(getGoals(newDungonRes).contains(":boulders"));
        assertFalse(getGoals(newDungonRes).contains(":treasure"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        newDungonRes = dmc.tick(Direction.UP);
        assertEquals("", getGoals(newDungonRes));

        //************************************************************ */
        //* Two subgoals: (enemies AND boulder) AND (treasure AND exit) */
        //************************************************************ */
        
        dmc = new DungeonManiaController();
        initDungonRes= dmc.newGame("d_goalTest_superGoal_andComplex", "c_goalTest_simple");

        assertTrue(getGoals(initDungonRes).contains(":enemies"));
        assertTrue(getGoals(initDungonRes).contains(":boulders"));
        assertTrue(getGoals(initDungonRes).contains(":treasure"));
        assertTrue(getGoals(initDungonRes).contains(":exit"));

        newDungonRes = dmc.tick(Direction.LEFT); // kill spider

        assertFalse(getGoals(newDungonRes).contains(":enemies"));
        assertTrue(getGoals(newDungonRes).contains(":boulders"));
        assertTrue(getGoals(newDungonRes).contains(":treasure"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        dmc.tick(Direction.RIGHT);
        newDungonRes = dmc.tick(Direction.RIGHT); // pushing boulder on switch
        assertFalse(getGoals(newDungonRes).contains(":enemies"));
        assertFalse(getGoals(newDungonRes).contains(":boulders"));
        assertTrue(getGoals(newDungonRes).contains(":treasure"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        newDungonRes = dmc.tick(Direction.DOWN);

        assertFalse(getGoals(newDungonRes).contains(":enemies"));
        assertFalse(getGoals(newDungonRes).contains(":boulders"));
        assertTrue(getGoals(newDungonRes).contains(":treasure"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        newDungonRes = dmc.tick(Direction.DOWN);

        assertFalse(getGoals(newDungonRes).contains(":enemies"));
        assertFalse(getGoals(newDungonRes).contains(":boulders"));
        assertFalse(getGoals(newDungonRes).contains(":treasure"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        newDungonRes = dmc.tick(Direction.UP);
        assertEquals("", getGoals(newDungonRes));

        //************************************************************ */
        //* Two subgoals: (enemies AND boulder) OR (treasure AND exit) */
        //************************************************************ */

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_goalTest_superGoal_orComplex", "c_goalTest_simple");

        assertTrue(getGoals(initDungonRes).contains(":enemies"));
        assertTrue(getGoals(initDungonRes).contains(":boulders"));
        assertTrue(getGoals(initDungonRes).contains(":treasure"));
        assertTrue(getGoals(initDungonRes).contains(":exit"));

        newDungonRes = dmc.tick(Direction.LEFT); // kill spider

        assertFalse(getGoals(newDungonRes).contains(":enemies"));
        assertTrue(getGoals(newDungonRes).contains(":boulders"));
        assertTrue(getGoals(newDungonRes).contains(":treasure"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        dmc.tick(Direction.RIGHT);
        newDungonRes = dmc.tick(Direction.RIGHT); // push boulder onto switch

        assertFalse(getGoals(newDungonRes).contains(":enemies"));
        assertFalse(getGoals(newDungonRes).contains(":boulders"));
        assertFalse(getGoals(newDungonRes).contains(":treasure"));
        assertFalse(getGoals(newDungonRes).contains(":exit"));
        assertEquals("", getGoals(newDungonRes));

        newDungonRes = dmc.tick(Direction.RIGHT); // push boulder off switch

        assertFalse(getGoals(newDungonRes).contains(":enemies"));
        assertTrue(getGoals(newDungonRes).contains(":boulders"));
        assertTrue(getGoals(newDungonRes).contains(":treasure"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        dmc.tick(Direction.LEFT);
        newDungonRes = dmc.tick(Direction.DOWN);

        assertFalse(getGoals(newDungonRes).contains(":enemies"));
        assertTrue(getGoals(newDungonRes).contains(":boulders"));
        assertTrue(getGoals(newDungonRes).contains(":treasure"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        newDungonRes = dmc.tick(Direction.DOWN);

        assertFalse(getGoals(newDungonRes).contains(":enemies"));
        assertTrue(getGoals(newDungonRes).contains(":boulders"));
        assertFalse(getGoals(newDungonRes).contains(":treasure"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        newDungonRes = dmc.tick(Direction.UP);
        assertEquals("", getGoals(newDungonRes));

        //************************************************************ */
        //* Two subgoals: (enemies OR boulder) AND (treasure AND exit) */
        //************************************************************ */

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_goalTest_superGoal_andSubGoalOrComplex", "c_goalTest_simple");

        assertTrue(getGoals(initDungonRes).contains(":enemies"));
        assertTrue(getGoals(initDungonRes).contains(":boulders"));
        assertTrue(getGoals(initDungonRes).contains(":treasure"));
        assertTrue(getGoals(initDungonRes).contains(":exit"));

        newDungonRes = dmc.tick(Direction.RIGHT); // push boulder onto switch

        assertFalse(getGoals(newDungonRes).contains(":enemies"));
        assertFalse(getGoals(newDungonRes).contains(":boulders"));
        assertTrue(getGoals(newDungonRes).contains(":treasure"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        newDungonRes = dmc.tick(Direction.DOWN);

        assertFalse(getGoals(newDungonRes).contains(":enemies"));
        assertFalse(getGoals(newDungonRes).contains(":boulders"));
        assertTrue(getGoals(newDungonRes).contains(":treasure"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        newDungonRes = dmc.tick(Direction.DOWN); // collect treasure

        assertFalse(getGoals(newDungonRes).contains(":enemies"));
        assertFalse(getGoals(newDungonRes).contains(":boulders"));
        assertFalse(getGoals(newDungonRes).contains(":treasure"));
        assertTrue(getGoals(newDungonRes).contains(":exit"));

        newDungonRes = dmc.tick(Direction.UP);
        assertEquals("", getGoals(newDungonRes));

        //************************************************************ */
        //* Two subgoals: (enemies OR boulder) OR (treasure AND exit) */
        //************************************************************ */

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_goalTest_superGoal_orSubGoalOrComplex", "c_goalTest_simple");

        assertTrue(getGoals(initDungonRes).contains(":enemies"));
        assertTrue(getGoals(initDungonRes).contains(":boulders"));
        assertTrue(getGoals(initDungonRes).contains(":treasure"));
        assertTrue(getGoals(initDungonRes).contains(":exit"));

        newDungonRes = dmc.tick(Direction.RIGHT); // push boulder onto switch
        assertEquals("", getGoals(newDungonRes));
    }
}
