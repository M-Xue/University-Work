package dungeonmania;

import static dungeonmania.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestLogicSwitches {

    //*********************************************************************************************************************/
    //* OR logic tests ****************************************************************************************************/
    //*********************************************************************************************************************/



    @Test
    public void testOrLogicSwitchLightbulbsBasic() {
        //* Uses OR logic */
        /**
            player | boulder | switch | lightbulb
        **/

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_orLightbulbsBasic", "c_logicTests_basic");

        EntityResponse offLightBulb = getEntitiesStream(initDungonRes, "light_bulb_off").findFirst().get();
        EntityResponse expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(3, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        
        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);
        EntityResponse onLightBulb = getEntitiesStream(newDungonRes, "light_bulb_on").findFirst().get();
        EntityResponse expectedOnLightBulb = new EntityResponse(onLightBulb.getId(), onLightBulb.getType(), new Position(3, 0), false);
        assertEquals(expectedOnLightBulb, onLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        /*
                   |         | lightbulb
            player | boulder | switch 
                   |         | lightbulb
        */
        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_orLightbulbsBasic2", "c_logicTests_basic");

        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 2);
        
        newDungonRes = dmc.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 2);


        /**
            player |      | boulder | switch | lightbulb
        **/

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_orLightbulbsBasic3", "c_logicTests_basic");

        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);
        
        dmc.tick(Direction.RIGHT);
        newDungonRes = dmc.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);

        /** 
            lightbulb |
            switch    |
            boulder   |
            player    |
        **/

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_orLightbulbsBasic4", "c_logicTests_basic");

        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);
        
        newDungonRes = dmc.tick(Direction.UP);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        
    }
    
    @Test
    public void testOrLogicSwitchLightbulbsBackwardsCompatability() {
        //* Uses OR logic */
        /**
            player | boulder | switch | lightbulb
        **/

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_orLightbulbsBackwardsCompatability", "c_logicTests_basic");

        EntityResponse offLightBulb = getEntitiesStream(initDungonRes, "light_bulb_off").findFirst().get();
        EntityResponse expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(3, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        
        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);
        EntityResponse onLightBulb = getEntitiesStream(newDungonRes, "light_bulb_on").findFirst().get();
        EntityResponse expectedOnLightBulb = new EntityResponse(onLightBulb.getId(), onLightBulb.getType(), new Position(3, 0), false);
        assertEquals(expectedOnLightBulb, onLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        /*
                   |         | lightbulb
            player | boulder | switch 
                   |         | lightbulb
        */
        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_orLightbulbsBackwardsCompatability2", "c_logicTests_basic");

        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 2);
        
        newDungonRes = dmc.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 2);


        /**
            player |      | boulder | switch | lightbulb
        **/

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_orLightbulbsBackwardsCompatability3", "c_logicTests_basic");

        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);
        
        dmc.tick(Direction.RIGHT);
        newDungonRes = dmc.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
    }

    @Test
    public void testOrLogicSwitchLightbulbsBasicSwitchOff() {
        //* Uses OR logic */
        /**
            player | boulder | switch | lightbulb
        **/

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_orLightbulbsBasic", "c_logicTests_basic");

        EntityResponse offLightBulb = getEntitiesStream(initDungonRes, "light_bulb_off").findFirst().get();
        EntityResponse expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(3, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        
        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);
        EntityResponse onLightBulb = getEntitiesStream(newDungonRes, "light_bulb_on").findFirst().get();
        EntityResponse expectedOnLightBulb = new EntityResponse(onLightBulb.getId(), onLightBulb.getType(), new Position(3, 0), false);
        assertEquals(expectedOnLightBulb, onLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        newDungonRes = dmc.tick(Direction.DOWN);

        offLightBulb = getEntitiesStream(initDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(3, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);

        /*
                   |         | lightbulb
            player | boulder | switch 
                   |         | lightbulb
        */
        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_orLightbulbsBasic2", "c_logicTests_basic");

        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 2);
        
        newDungonRes = dmc.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 2);

        newDungonRes = dmc.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 2);
    }

    @Test
    public void testOrLogicSwitchWiresBasic() {
        //* Uses OR logic */
        /**
            player | boulder | switch | wire | lightbulb
        **/
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_orWiresBasic", "c_logicTests_basic");

        EntityResponse offLightBulb = getEntitiesStream(initDungonRes, "light_bulb_off").findFirst().get();
        EntityResponse expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(4, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        
        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);
        EntityResponse onLightBulb = getEntitiesStream(newDungonRes, "light_bulb_on").findFirst().get();
        EntityResponse expectedOnLightBulb = new EntityResponse(onLightBulb.getId(), onLightBulb.getType(), new Position(4, 0), false);
        assertEquals(expectedOnLightBulb, onLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        newDungonRes = dmc.tick(Direction.RIGHT);
        
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);
        offLightBulb = getEntitiesStream(newDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(4, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);

        /*
                   |         |        |      | lightbulb
            player | boulder | switch | wire | wire
                   |         |        |      | lightbulb
        */
        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_orWiresBasic2", "c_logicTests_basic");

        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 2);
        
        newDungonRes = dmc.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 2);
        
        newDungonRes = dmc.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 2);
    }

    @Test
    public void testOrLogicSwitchWiresComplex() {
        //* Uses OR logic */
        /**
                      | lightbulb | wire      | wire      | wire      | lightbulb
                      |           |           | wire      | 
            player    | boulder   | switch    | wire      | lightbulb
                      |           |           | wire      | 
                      |           |           | wire      | lightbulb
                      |           | lightbulb | wire      | 
        **/
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_orWiresComplex", "c_logicTests_basic");
        
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 5);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        
        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);
 
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 5);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        newDungonRes = dmc.tick(Direction.RIGHT);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 5);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);
    }

    @Test
    public void testOrLogicSwitchDisconnectedWires() {
        //* Uses OR logic */
        /**
                      | lightbulb | wire      | wire      | wire      | lightbulb
                      |           |           |           | 
            player    | boulder   | switch    | wire      | lightbulb
                      |           |           | wire      | 
                      |           |           | wire      | lightbulb
        **/
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_orDisconnectedWires", "c_logicTests_basic");
        
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 4);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        
        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);
 
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 2);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 2);

        newDungonRes = dmc.tick(Direction.RIGHT);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 4);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);
    }

    @Test
    public void testOrLogicSwitchDoors() {
        //* Uses OR logic */
        /**
            boulder | switch 
            player  | s_door 
        **/

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_orSwitchDoors", "c_logicTests_basic");

        // assert player is in the right starting position
        EntityResponse initPlayer = getPlayer(initDungonRes).get();
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        EntityResponse updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player should be able to move because the switch door is unlocked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);
    }

    @Test
    public void testOrLogicSwitchDoorsWires() {
        //* Uses OR logic */
        /**
            switch  | wire
            boulder | wire
            player  | s_door 
        **/

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_orSwitchDoorWires", "c_logicTests_basic");

        // assert player is in the right starting position
        EntityResponse initPlayer = getPlayer(initDungonRes).get();
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        EntityResponse updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.UP);
        dmc.tick(Direction.DOWN);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player should be able to move because the switch door is unlocked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        /**
            wire   | switch  | wire
            wire   | boulder | wire
            s_door | player  | s_door 
        **/

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_orSwitchDoorWires2", "c_logicTests_basic");

        // assert player is in the right starting position
        initPlayer = getPlayer(initDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.LEFT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.UP);
        dmc.tick(Direction.DOWN);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player should be able to move because the switch door is unlocked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.LEFT);
        newDungonRes = dmc.tick(Direction.LEFT);

        // the player should be able to move because the switch door is unlocked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(-1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);
    }



    // //*********************************************************************************************************************/
    // //* AND logic tests ***************************************************************************************************/
    // //*********************************************************************************************************************/



    @Test
    public void testAndLogicSwitchLightbulbsBasic() {
        //* Uses AND logic */
        /*
                   | boulder | switch
            player |         | lightbulb
                   | boulder | switch
        */

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_andLightbulbsBasic", "c_logicTests_basic");

        EntityResponse offLightBulb = getEntitiesStream(initDungonRes, "light_bulb_off").findFirst().get();
        EntityResponse expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(2, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);

        dmc.tick(Direction.UP);
        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);

        offLightBulb = getEntitiesStream(newDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(2, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);


        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        newDungonRes = dmc.tick(Direction.RIGHT);

        EntityResponse onLightBulb = getEntitiesStream(newDungonRes, "light_bulb_on").findFirst().get();
        EntityResponse expectedOnLightBulb = new EntityResponse(onLightBulb.getId(), onLightBulb.getType(), new Position(2, 0), false);
        assertEquals(expectedOnLightBulb, onLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        /**
            player | boulder | switch | lightbulb
        **/

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_andLightbulbsBasic2", "c_logicTests_basic");

        offLightBulb = getEntitiesStream(initDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(3, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        
        newDungonRes = dmc.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);

        offLightBulb = getEntitiesStream(newDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(3, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);
    }



    @Test
    public void testAndLogicSwitchWiresBasic() {
        //* Uses AND logic */
        /*
                   | boulder | switch | wire | wire
            player |         |        |      | lightbulb
                   | boulder | switch | wire | wire
        */

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_andWiresBasic", "c_logicTests_basic");

        EntityResponse offLightBulb = getEntitiesStream(initDungonRes, "light_bulb_off").findFirst().get();
        EntityResponse expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(4, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);

        dmc.tick(Direction.UP);
        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);

        offLightBulb = getEntitiesStream(newDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(4, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);


        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        newDungonRes = dmc.tick(Direction.RIGHT);

        EntityResponse onLightBulb = getEntitiesStream(newDungonRes, "light_bulb_on").findFirst().get();
        EntityResponse expectedOnLightBulb = new EntityResponse(onLightBulb.getId(), onLightBulb.getType(), new Position(4, 0), false);
        assertEquals(expectedOnLightBulb, onLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        /*
            switch  | wire      | wire
            boulder | lightbulb | wire
            player  |           |
        */

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_andWiresBasic2", "c_logicTests_basic");

        offLightBulb = getEntitiesStream(initDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(1, -1), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);

        newDungonRes = dmc.tick(Direction.UP);

        onLightBulb = getEntitiesStream(newDungonRes, "light_bulb_on").findFirst().get();
        expectedOnLightBulb = new EntityResponse(onLightBulb.getId(), onLightBulb.getType(), new Position(1, -1), false);
        assertEquals(expectedOnLightBulb, onLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);
    }

    @Test
    public void testAndLogicSwitchMoreThan2ActivatableEntities() {
        //* Uses AND logic */
        /*
            switch  | wire      | wire
            boulder | lightbulb | wire
            player  | wire      |
            boulder | wire      |
            switch  | wire      |
        */

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_and2+ActiveEntities", "c_logicTests_basic");

        EntityResponse offLightBulb = getEntitiesStream(initDungonRes, "light_bulb_off").findFirst().get();
        EntityResponse expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(1, -1), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);

        DungeonResponse newDungonRes = dmc.tick(Direction.UP);

        offLightBulb = getEntitiesStream(newDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(1, -1), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);

        dmc.tick(Direction.DOWN);
        newDungonRes = dmc.tick(Direction.DOWN);

        EntityResponse onLightBulb = getEntitiesStream(newDungonRes, "light_bulb_on").findFirst().get();
        EntityResponse expectedOnLightBulb = new EntityResponse(onLightBulb.getId(), onLightBulb.getType(), new Position(1, -1), false);
        assertEquals(expectedOnLightBulb, onLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        /*
                    |         | lightbulb |      |
            switch  | wire    | wire      | wire | wire
            boulder |         |           |      | wire
            player  | boulder | switch    | wire | lightbulb
            boulder |         |           |      | wire
            switch  | wire    | wire      | wire | wire
        */
        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_and2+ActiveEntities2", "c_logicTests_basic");
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 2);

        newDungonRes = dmc.tick(Direction.UP);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 2);

        dmc.tick(Direction.DOWN);
        newDungonRes = dmc.tick(Direction.DOWN);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 2);

        dmc.tick(Direction.UP);
        newDungonRes = dmc.tick(Direction.RIGHT);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        onLightBulb = getEntitiesStream(newDungonRes, "light_bulb_on").findFirst().get();
        expectedOnLightBulb = new EntityResponse(onLightBulb.getId(), onLightBulb.getType(), new Position(4, 0), false);
        assertEquals(expectedOnLightBulb, onLightBulb);
        offLightBulb = getEntitiesStream(newDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(2, -3), false);
        assertEquals(expectedOffLightBulb, offLightBulb);

        /**
                      | lightbulb | wire      | wire      | wire      | lightbulb
                      |           |           | wire      | 
            player    | boulder   | switch    | wire      | lightbulb
                      |           |           | wire      | wire
                      |           |           | wire      | lightbulb
                      |           | lightbulb | wire      | 
        **/
        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_and2+ActiveEntities3", "c_logicTests_basic");
        
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 5);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        
        newDungonRes = dmc.tick(Direction.RIGHT);
 
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 2);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 3);

    }

    @Test
    public void testAndLogicSwitchDoorBasic() {
        //* Uses AND logic */
        /*
                   | boulder | switch
            player |         | s_door
                   | boulder | switch
        */

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_andSwitchDoorBasic", "c_logicTests_basic");

        // assert player is in the right starting position
        EntityResponse initPlayer = getPlayer(initDungonRes).get();
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);

        EntityResponse updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        newDungonRes =  dmc.tick(Direction.DOWN);


        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);

        newDungonRes =  dmc.tick(Direction.UP);

        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player should be able to move because the switch door is unlocked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(2, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        EntityResponse s_door = getEntitiesStream(initDungonRes, "switch_door").findFirst().get();
        EntityResponse expectedS_door = new EntityResponse(s_door.getId(), s_door.getType(), new Position(2, 0), false);
        assertEquals(expectedS_door, s_door);

        /**
            player | boulder | switch | s_door
        **/

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_andSwitchDoorBasic2", "c_logicTests_basic");

        // assert player is in the right starting position
        initPlayer = getPlayer(initDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        newDungonRes = dmc.tick(Direction.RIGHT);

        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(3, -1), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.DOWN);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(3, -1), false);
        assertEquals(expectedPlayer, updatedPlayer);


        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        newDungonRes = dmc.tick(Direction.DOWN);

        // assert player is in the right starting position
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        newDungonRes = dmc.tick(Direction.RIGHT);

        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(3, 1), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.UP);

        // the player shouldnt be able to move because the switch door is locked (because it is an AND condition)
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(3, 1), false);
        assertEquals(expectedPlayer, updatedPlayer);

        s_door = getEntitiesStream(initDungonRes, "switch_door").findFirst().get();
        expectedS_door = new EntityResponse(s_door.getId(), s_door.getType(), new Position(3, 0), false);
        assertEquals(expectedS_door, s_door);
    }

    @Test
    public void testAndLogicSwitchDoorsWires() {
        //* Uses AND logic */
        /**
            switch  | wire 
            boulder | wire 
            player  | s_door 
        **/

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_andSwitchDoorWires", "c_logicTests_basic");

        // assert player is in the right starting position
        EntityResponse initPlayer = getPlayer(initDungonRes).get();
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        EntityResponse updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.UP);
        dmc.tick(Direction.DOWN);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);


        /**
            switch  | wire   | wire
            boulder | s_door | wire
            player  | 
        **/

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_andSwitchDoorWires2", "c_logicTests_basic");

        // assert player is in the right starting position
        initPlayer = getPlayer(initDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // assert player is in the right position
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.UP);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.UP);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player should be able to move because the switch door is unlocked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, -1), false);
        assertEquals(expectedPlayer, updatedPlayer);


        EntityResponse s_door = getEntitiesStream(initDungonRes, "switch_door").findFirst().get();
        EntityResponse expectedS_door = new EntityResponse(s_door.getId(), s_door.getType(), new Position(1, -1), false);
        assertEquals(expectedS_door, s_door);

        /**
            wire   | switch  | wire
            wire   | boulder | wire
            s_door | player  | s_door 
        **/

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_andSwitchDoorWires3", "c_logicTests_basic");

        // assert player is in the right starting position
        initPlayer = getPlayer(initDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.LEFT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.UP);
        dmc.tick(Direction.DOWN);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.LEFT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);
    }

    @Test
    public void testAndLogicSwitchMoreThan2ActivatableEntitiesDoor() {
        /*
            switch  | wire   | wire
            boulder | s_door | wire
            player  | wire
            boulder | wire
            switch  | wire
        */

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_and2+ActiveEntitiesSwitchDoor", "c_logicTests_basic");

        // assert player is in the right starting position
        EntityResponse initPlayer = getPlayer(initDungonRes).get();
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        dmc.tick(Direction.UP);

        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        EntityResponse updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, -1), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player should be able to move because the switch door is unlocked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, -1), false);
        assertEquals(expectedPlayer, updatedPlayer);

        EntityResponse s_door = getEntitiesStream(initDungonRes, "switch_door").findFirst().get();
        EntityResponse expectedS_door = new EntityResponse(s_door.getId(), s_door.getType(), new Position(1, -1), false);
        assertEquals(expectedS_door, s_door);

        /*
            wire   | wire    | wire    | wire   | wire | wire
            wire   |         |         |        |      | wire
            wire   |         |         |        |      | wire
            wire   |         | switch  | wire   |      | wire
            wire   |         | boulder | wire   |      | wire
            switch | boulder | player  | s_door | wire | wire
                   |         | boulder | wire   |      |
                   |         | switch  | wire   |      |
        */

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_and2+ActiveEntitiesSwitchDoor2", "c_logicTests_basic");

        // assert player is in the right starting position
        initPlayer = getPlayer(initDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.RIGHT);
        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.UP);
        dmc.tick(Direction.DOWN);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.UP);
        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player should be able to move because the switch door is unlocked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        s_door = getEntitiesStream(initDungonRes, "switch_door").findFirst().get();
        expectedS_door = new EntityResponse(s_door.getId(), s_door.getType(), new Position(1, 0), false);
        assertEquals(expectedS_door, s_door);
    }



    //*********************************************************************************************************************/
    //* XOR logic tests ***************************************************************************************************/
    //*********************************************************************************************************************/



    @Test
    public void testXorLogicSwitchLightbulbsBasic() {
        //* Uses XOR logic */
        /*
                   | boulder | switch
            player |         | lightbulb
                   | boulder | switch
        */

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_xorLightbulbBasic", "c_logicTests_basic");

        EntityResponse offLightBulb = getEntitiesStream(initDungonRes, "light_bulb_off").findFirst().get();
        EntityResponse expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(2, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);

        dmc.tick(Direction.UP);
        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);

        EntityResponse onLightBulb = getEntitiesStream(newDungonRes, "light_bulb_on").findFirst().get();
        EntityResponse expectedOnLightBulb = new EntityResponse(onLightBulb.getId(), onLightBulb.getType(), new Position(2, 0), false);
        assertEquals(expectedOnLightBulb, onLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        newDungonRes = dmc.tick(Direction.RIGHT);

        offLightBulb = getEntitiesStream(newDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(2, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);

        /**
            player | boulder | switch | lightbulb
        **/

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_xorLightbulbBasic2", "c_logicTests_basic");

        offLightBulb = getEntitiesStream(initDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(3, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        
        newDungonRes = dmc.tick(Direction.RIGHT);
        onLightBulb = getEntitiesStream(newDungonRes, "light_bulb_on").findFirst().get();
        expectedOnLightBulb = new EntityResponse(onLightBulb.getId(), onLightBulb.getType(), new Position(3, 0), false);
        assertEquals(expectedOnLightBulb, onLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);
    }



    @Test
    public void testXorLogicSwitchWiresBasic() {
        //* Uses XOR logic */
        /*
                   | boulder | switch | wire | wire
            player |         |        |      | lightbulb
                   | boulder | switch | wire | wire
        */

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_xorWiresBasic", "c_logicTests_basic");

        EntityResponse offLightBulb = getEntitiesStream(initDungonRes, "light_bulb_off").findFirst().get();
        EntityResponse expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(4, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);

        dmc.tick(Direction.UP);
        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);
        
        EntityResponse onLightBulb = getEntitiesStream(newDungonRes, "light_bulb_on").findFirst().get();
        EntityResponse expectedOnLightBulb = new EntityResponse(onLightBulb.getId(), onLightBulb.getType(), new Position(4, 0), false);
        assertEquals(expectedOnLightBulb, onLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);
        
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        newDungonRes = dmc.tick(Direction.RIGHT);
        
        offLightBulb = getEntitiesStream(newDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(4, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);

        /*
            switch  | wire      
            boulder | lightbulb 
            player  |
        */

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_xorWiresBasic2", "c_logicTests_basic");

        offLightBulb = getEntitiesStream(initDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(1, -1), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);

        newDungonRes = dmc.tick(Direction.UP);

        onLightBulb = getEntitiesStream(newDungonRes, "light_bulb_on").findFirst().get();
        expectedOnLightBulb = new EntityResponse(onLightBulb.getId(), onLightBulb.getType(), new Position(1, -1), false);
        assertEquals(expectedOnLightBulb, onLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        /*
            switch  | wire      | wire
            boulder | lightbulb | wire
            player  |
        */

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_xorWiresBasic3", "c_logicTests_basic");

        offLightBulb = getEntitiesStream(initDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(1, -1), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);

        newDungonRes = dmc.tick(Direction.UP);

        offLightBulb = getEntitiesStream(newDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(1, -1), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);
    }

    @Test
    public void testXorLogicSwitchMoreThan2ActivatableEntities() {
        //* Uses XOR logic */
        /*
            switch  | wire
            boulder | lightbulb
            player  | wire
            boulder | wire
            switch  | wire
        */

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_xor2+Activatable", "c_logicTests_basic");

        EntityResponse offLightBulb = getEntitiesStream(initDungonRes, "light_bulb_off").findFirst().get();
        EntityResponse expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(1, -1), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);

        DungeonResponse newDungonRes = dmc.tick(Direction.UP);

        EntityResponse onLightBulb = getEntitiesStream(newDungonRes, "light_bulb_on").findFirst().get();
        EntityResponse expectedOnLightBulb = new EntityResponse(onLightBulb.getId(), onLightBulb.getType(), new Position(1, -1), false);
        assertEquals(expectedOnLightBulb, onLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        dmc.tick(Direction.DOWN);
        newDungonRes = dmc.tick(Direction.DOWN);

        offLightBulb = getEntitiesStream(newDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(1, -1), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);

        /*
                    |         | lightbulb |      |
            switch  | wire    | wire      | wire | wire
            boulder |         |           |      | wire
            player  | boulder | switch    | wire | lightbulb
            boulder |         |           |      | wire
            switch  | wire    | wire      | wire | wire
        */
        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_xor2+Activatable2", "c_logicTests_basic");
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 2);

        newDungonRes = dmc.tick(Direction.UP);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 2);

        dmc.tick(Direction.DOWN);
        newDungonRes = dmc.tick(Direction.DOWN);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);

        dmc.tick(Direction.UP);
        newDungonRes = dmc.tick(Direction.LEFT);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        onLightBulb = getEntitiesStream(newDungonRes, "light_bulb_on").findFirst().get();
        expectedOnLightBulb = new EntityResponse(onLightBulb.getId(), onLightBulb.getType(), new Position(2, -3), false);
        assertEquals(expectedOnLightBulb, onLightBulb);
        offLightBulb = getEntitiesStream(newDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(4, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);

        /**
                      | lightbulb | wire      | wire      | wire      | lightbulb
                      |           |           | wire      | 
            player    | boulder   | switch    | wire      | lightbulb
                      |           |           | wire      | wire
                      |           |           | wire      | lightbulb
                      |           | lightbulb | wire      | 
        **/
        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_xor2+Activatable3", "c_logicTests_basic");
        
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 5);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        
        newDungonRes = dmc.tick(Direction.RIGHT);
 
        assertEquals(2, getEntitiesStream(newDungonRes, "light_bulb_off").count());
        assertEquals(3, getEntitiesStream(newDungonRes, "light_bulb_on").count());
    }

    @Test
    public void testXorLogicSwitchDoorBasic() {
        //* Uses XOR logic */
        /*
                   | boulder | switch
            player |         | s_door
                   | boulder | switch
        */

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_xorSwitchDoorBasic", "c_logicTests_basic");

        // assert player is in the right starting position
        EntityResponse initPlayer = getPlayer(initDungonRes).get();
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);

        EntityResponse updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        newDungonRes =  dmc.tick(Direction.DOWN);


        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT); 

        // the player should be able to move because the switch door is unlocked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(2, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);

        newDungonRes =  dmc.tick(Direction.UP);

        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        EntityResponse s_door = getEntitiesStream(initDungonRes, "switch_door").findFirst().get();
        EntityResponse expectedS_door = new EntityResponse(s_door.getId(), s_door.getType(), new Position(2, 0), false);
        assertEquals(expectedS_door, s_door);

        /**
            player | boulder | switch | s_door
        **/

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_xorSwitchDoorBasic2", "c_logicTests_basic");

        // assert player is in the right starting position
        initPlayer = getPlayer(initDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        newDungonRes = dmc.tick(Direction.RIGHT);

        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(3, -1), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.DOWN);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(3, -1), false);
        assertEquals(expectedPlayer, updatedPlayer);


        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        newDungonRes = dmc.tick(Direction.DOWN);

        // assert player is in the right starting position
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        newDungonRes = dmc.tick(Direction.RIGHT);

        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(3, 1), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.UP);

        // the player should be able to move because the switch door is unlocked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(3, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        s_door = getEntitiesStream(initDungonRes, "switch_door").findFirst().get();
        expectedS_door = new EntityResponse(s_door.getId(), s_door.getType(), new Position(3, 0), false);
        assertEquals(expectedS_door, s_door);
    }

    @Test
    public void testXorLogicSwitchDoorsWires() {
        //* Uses XOR logic */
        /**
            switch  | wire 
            boulder | wire 
            player  | s_door 
        **/

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_xorSwitchDoorWires", "c_logicTests_basic");

        // assert player is in the right starting position
        EntityResponse initPlayer = getPlayer(initDungonRes).get();
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        EntityResponse updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.UP);
        dmc.tick(Direction.DOWN);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player should be able to move because the switch door is unlocked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        /**
            wire   | switch  | wire
            wire   | boulder | wire
            s_door | player  | s_door 
        **/

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_xorSwitchDoorWires2", "c_logicTests_basic");

        // assert player is in the right starting position
        initPlayer = getPlayer(initDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.LEFT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.UP);
        dmc.tick(Direction.DOWN);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player should be able to move because the switch door is unlocked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.LEFT);
        newDungonRes = dmc.tick(Direction.LEFT);

        // the player should be able to move because the switch door is unlocked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(-1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);
    }

    @Test
    public void testXorLogicSwitchMoreThan2ActivatableEntitiesDoor() {
        //* Uses XOR logic */
        /*
            switch  | wire
            boulder | s_door
            player  | wire
            boulder | wire
            switch  | wire
        */

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_xor2+ActivatableEntitiesSwitchDoor", "c_logicTests_basic");

        // assert player is in the right starting position
        EntityResponse initPlayer = getPlayer(initDungonRes).get();
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        dmc.tick(Direction.UP);

        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);

        // the player should be able to move because the switch door is unlocked
        EntityResponse updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, -1), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, -1), false);
        assertEquals(expectedPlayer, updatedPlayer);

        EntityResponse s_door = getEntitiesStream(initDungonRes, "switch_door").findFirst().get();
        EntityResponse expectedS_door = new EntityResponse(s_door.getId(), s_door.getType(), new Position(1, -1), false);
        assertEquals(expectedS_door, s_door);

        /*
            wire   | wire    | wire    | wire   | wire | wire
            wire   |         |         |        |      | wire
            wire   |         |         |        |      | wire
            wire   |         | switch  | wire   |      | wire
            wire   |         | boulder | wire   |      | wire
            switch | boulder | player  | s_door | wire | wire
                   |         | boulder | wire   |      |
                   |         | switch  | wire   |      |
        */

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_xor2+ActiveEntitiesSwitchDoor2", "c_logicTests_basic");

        // assert player is in the right starting position
        initPlayer = getPlayer(initDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.RIGHT);
        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player should be able to move because the switch door is unlocked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);


        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.DOWN);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.UP);
        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        s_door = getEntitiesStream(initDungonRes, "switch_door").findFirst().get();
        expectedS_door = new EntityResponse(s_door.getId(), s_door.getType(), new Position(1, 0), false);
        assertEquals(expectedS_door, s_door);
    }



    // //*********************************************************************************************************************/
    // //* CO_AND logic tests ************************************************************************************************/
    // //*********************************************************************************************************************/



    @Test
    public void testCo_AndLogicSwitch() {
        /*
            switch  | wire      | wire
            boulder | lightbulb | wire
            player  |           |
        */

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_co_andWiresBasic", "c_logicTests_basic");

        EntityResponse offLightBulb = getEntitiesStream(initDungonRes, "light_bulb_off").findFirst().get();
        EntityResponse expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(1, -1), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);

        DungeonResponse newDungonRes = dmc.tick(Direction.UP);

        EntityResponse onLightBulb = getEntitiesStream(newDungonRes, "light_bulb_on").findFirst().get();
        EntityResponse expectedOnLightBulb = new EntityResponse(onLightBulb.getId(), onLightBulb.getType(), new Position(1, -1), false);
        assertEquals(expectedOnLightBulb, onLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        /*
                   | boulder | switch
            player |         | lightbulb
                   | boulder | switch
        */

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_co_andLightbulbBasic", "c_logicTests_basic");

        offLightBulb = getEntitiesStream(initDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(2, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);

        dmc.tick(Direction.UP);
        newDungonRes = dmc.tick(Direction.RIGHT);

        offLightBulb = getEntitiesStream(newDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(2, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);


        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        newDungonRes = dmc.tick(Direction.RIGHT);

        offLightBulb = getEntitiesStream(newDungonRes, "light_bulb_off").findFirst().get();
        expectedOffLightBulb = new EntityResponse(offLightBulb.getId(), offLightBulb.getType(), new Position(2, 0), false);
        assertEquals(expectedOffLightBulb, offLightBulb);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);

        /*
            wire   | wire    | wire    | wire | wire | wire      |
            wire   |         |         |      |      | wire      |
            wire   |         | switch  | wire |      | wire      |
            wire   |         | boulder | wire |      | wire      | wire
            switch | boulder | player  | wire | wire | lightbulb | wire
                   |         | boulder |      |      | wire      |
                   |         | switch  | wire | wire | wire      |
        */

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_co_andWiresComplex", "c_logicTests_basic");
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);

        dmc.tick(Direction.DOWN);
        newDungonRes = dmc.tick(Direction.UP);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 1);

        dmc.tick(Direction.UP);
        newDungonRes = dmc.tick(Direction.DOWN);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 1);

        newDungonRes = dmc.tick(Direction.LEFT);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);
    }

    @Test
    public void testCo_AndLogicSwitchDoor() {

        /*
            switch  | wire   | wire
            boulder | s_door | wire
                    | player |
        */

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_co_andSwitchDoor", "c_logicTests_basic");

        // assert player is in the right starting position
        EntityResponse initPlayer = getPlayer(initDungonRes).get();
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        DungeonResponse newDungonRes = dmc.tick(Direction.UP);

        // the player shouldn't be able to move because the switch door is locked
        EntityResponse updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.LEFT);
        newDungonRes = dmc.tick(Direction.UP);
        
        // assert player is in the right position
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(-1, -1), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player should be able to move because the switch door is unlocked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, -1), false);
        assertEquals(expectedPlayer, updatedPlayer);

        /*
                   | boulder | switch
            player |         | s_door
                   | boulder | switch
        */

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_co_andSwitchDoor2", "c_logicTests_basic");

        // assert player is in the right starting position
        initPlayer = getPlayer(initDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        newDungonRes =  dmc.tick(Direction.DOWN);


        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);

        newDungonRes =  dmc.tick(Direction.UP);

        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        EntityResponse s_door = getEntitiesStream(initDungonRes, "switch_door").findFirst().get();
        EntityResponse expectedS_door = new EntityResponse(s_door.getId(), s_door.getType(), new Position(2, 0), false);
        assertEquals(expectedS_door, s_door);


        /*
            wire   | wire    | wire    | wire | wire | wire   |
            wire   |         |         |      |      | wire   |
            wire   |         | switch  | wire |      | wire   |
            wire   |         | boulder | wire |      | wire   | wire
            switch | boulder | player  | wire | wire | s_door | wire
                   |         | boulder |      |      | wire   |
                   |         | switch  | wire | wire | wire   |
        */

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_co_andSwitchDoorComplex", "c_logicTests_basic");

        // assert player is in the right starting position
        initPlayer = getPlayer(initDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        dmc.tick(Direction.RIGHT);
        newDungonRes = dmc.tick(Direction.RIGHT);

        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(2, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(2, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.DOWN);

        dmc.tick(Direction.RIGHT);
        newDungonRes = dmc.tick(Direction.RIGHT);

        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(2, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(2, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.UP);

        dmc.tick(Direction.RIGHT);
        newDungonRes = dmc.tick(Direction.RIGHT);

        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(2, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(2, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.RIGHT);

        dmc.tick(Direction.RIGHT);
        newDungonRes = dmc.tick(Direction.RIGHT);

        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(2, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player should be able to move because the switch door is unlocked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(3, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        s_door = getEntitiesStream(initDungonRes, "switch_door").findFirst().get();
        expectedS_door = new EntityResponse(s_door.getId(), s_door.getType(), new Position(3, 0), false);
        assertEquals(expectedS_door, s_door);
    }



    // //*********************************************************************************************************************/
    // //* Combined logic tests **********************************************************************************************/
    // //*********************************************************************************************************************/
    @Test
    public void testCombinedLogicSwitch() {
        /**
                   |                 |                    |      | wire            | 
                   | lightbulb (AND) | wire               | wire | wire            | lightbulb (CO_AND)
                   |                 |                    | wire |                 |
            player | boulder         | switch             | wire | lightbulb (XOR) |
                   |                 |                    | wire | wire            |
                   |                 | wire               | wire | lightbulb (AND) |
                   |                 | lightbulb (CO_AND) | wire |                 |
        **/
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_combinedLogic", "c_logicTests_basic");
        
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 5);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        
        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);
 
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 3);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 2);
    }

    @Test
    public void testCombinedLogicSwitchDoors() {
        
         /**
            wire         | switch        | wire            | 
            wire         | boulder       | wire            | wire
            s_door (AND) | player        | s_door (CO_AND) | wire
                         | s_door  (XOR) | wire            | wire
                         | wire          | wire            | wire
        **/

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_combinedLogicSwitchDoor", "c_logicTests_basic");

        // assert player is in the right starting position
        EntityResponse initPlayer = getPlayer(initDungonRes).get();
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);

        // the player shouldn't be able to move because the switch door is locked
        EntityResponse updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.LEFT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.DOWN);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.UP);
        dmc.tick(Direction.DOWN);

        newDungonRes = dmc.tick(Direction.RIGHT);

        // the player should be able to move because the switch door is unlocked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        dmc.tick(Direction.LEFT);
        newDungonRes = dmc.tick(Direction.LEFT);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);

        newDungonRes = dmc.tick(Direction.DOWN);

        // the player shouldn't be able to move because the switch door is locked
        updatedPlayer = getPlayer(newDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, updatedPlayer);
    }

    // //*********************************************************************************************************************/
    // //* Edge Cases ********************************************************************************************************/
    // //*********************************************************************************************************************/

    @Test
    public void testEdgeCases() {
        
        //* This edge case is to test what happens if you have 4 wires in a loop */
        /*
            switch  | wire | wire | lightbulb (OR)
            boulder | wire | wire | 
            player  |      |      |
        */

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_edgeCase1", "c_logicTests_basic");

        // assert player is in the right starting position
        EntityResponse initPlayer = getPlayer(initDungonRes).get();
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);


        DungeonResponse newDungonRes = dmc.tick(Direction.UP);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);
        
        newDungonRes = dmc.tick(Direction.UP);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 1);

        /*
            switch  | wire    | wire   | lightbulb (XOR) | wire
            boulder |         |        | wire            | wire
            player  | boulder | switch | wire            |
        */
        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_edgeCase2", "c_logicTests_basic");

        initPlayer = getPlayer(initDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);

        newDungonRes = dmc.tick(Direction.UP);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);
        
        dmc.tick(Direction.DOWN); 
        newDungonRes = dmc.tick(Direction.RIGHT);

        assertEquals(1, getEntitiesStream(newDungonRes, "light_bulb_off").count());
        assertEquals(0, getEntitiesStream(newDungonRes, "light_bulb_on").count());


        /*
            player | boulder      | switch (CO_AND) | switch (OR) | switch (XOR) | lightbulb
                   |              |                 | switch (OR) | switch (OR)  |
                   |              | switch (OR)     | switch (OR) | switch (AND) | lightbulb
                   | lightbulb    | switch (CO_AND) | switch (OR) |              |
        */

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_edgeCase3", "c_logicTests_basic");

        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 3);

        newDungonRes = dmc.tick(Direction.RIGHT);

        assertEquals(1, getEntitiesStream(newDungonRes, "light_bulb_off").count());
        assertEquals(2, getEntitiesStream(newDungonRes, "light_bulb_on").count());

        //* Edge cases for blowing up wires in the middle of a logic gate system */
        /*
                     | lighbulb (OR) |                    |                 |           |      |                 | 
             switch  | wire          | wire               | wire            | wire      | wire | wire            | lightbulb (OR)
             boulder |               | lightbulb (CO_AND) | wire            | bomb (OR) | wire | lightbulb (AND) |
           x player  | (bomb spawn)  | wire               | wire            |           |      |                 |
             boulder |               | wire               | lightbulb (XOR) |           |      |                 |
             switch  | wire          | wire               |                 |           |      |                 |
            
        */

        DungeonManiaController dmc1 = new DungeonManiaController();
        initDungonRes = dmc1.newGame("d_logicTest_edgeCase4", "c_logicTests_basic"); //!! Bomb radius should be 1

        initPlayer = getPlayer(initDungonRes).get();
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 5);

        newDungonRes = dmc1.tick(Direction.UP);

        assertEquals(4, getEntitiesStream(newDungonRes, "light_bulb_on").count());
        assertEquals(1, getEntitiesStream(newDungonRes, "light_bulb_off").count());
        
        dmc1.tick(Direction.DOWN); 
        newDungonRes = dmc1.tick(Direction.DOWN); 
        
        assertEquals(1, getEntitiesStream(newDungonRes, "light_bulb_off").count());
        assertEquals(4, getEntitiesStream(newDungonRes, "light_bulb_on").count());

        dmc1.tick(Direction.UP);
        dmc1.tick(Direction.RIGHT);  
        dmc1.tick(Direction.RIGHT);  
        dmc1.tick(Direction.RIGHT);  
        dmc1.tick(Direction.RIGHT);  
        newDungonRes = dmc1.tick(Direction.UP);  

        String bombId1 = getInventory(newDungonRes, "bomb").get(0).getId(); 
        newDungonRes = assertDoesNotThrow(() -> {return dmc1.tick(bombId1);});
        
        assertEquals(3, getEntitiesStream(newDungonRes, "light_bulb_off").count());
        assertEquals(2, getEntitiesStream(newDungonRes, "light_bulb_on").count());

        /*
                     |              | bomb (OR)
             switch  | wire         | wire               
             boulder |              | wire
           x player  | (bomb spawn) | lightbulb (OR)               
             boulder |              | wire             
             switch  | wire         | wire              
        */

        DungeonManiaController dmc2 = new DungeonManiaController();
        initDungonRes = dmc2.newGame("d_logicTest_edgeCase5", "c_logicTests_basic"); //!! Bomb radius should be 1

        assertEquals(0, getEntitiesStream(initDungonRes, "light_bulb_on").count());
        assertEquals(1, getEntitiesStream(initDungonRes, "light_bulb_off").count());

        newDungonRes = dmc2.tick(Direction.UP); 

        assertEquals(0, getEntitiesStream(newDungonRes, "light_bulb_off").count());
        assertEquals(1, getEntitiesStream(newDungonRes, "light_bulb_on").count());

        dmc2.tick(Direction.DOWN); 
        newDungonRes = dmc2.tick(Direction.DOWN); 

        assertEquals(0, getEntitiesStream(newDungonRes, "light_bulb_off").count());
        assertEquals(1, getEntitiesStream(newDungonRes, "light_bulb_on").count());

        dmc2.tick(Direction.UP); 
        dmc2.tick(Direction.RIGHT);
        dmc2.tick(Direction.UP); 
        dmc2.tick(Direction.UP); 
        dmc2.tick(Direction.UP); 
        newDungonRes = dmc2.tick(Direction.RIGHT);
        
        String bombId2 = getInventory(newDungonRes, "bomb").get(0).getId(); 
        newDungonRes = assertDoesNotThrow(() -> {return dmc2.tick(bombId2);});

        assertEquals(0, getEntitiesStream(newDungonRes, "light_bulb_off").count());
        assertEquals(1, getEntitiesStream(newDungonRes, "light_bulb_on").count());
    }

    // //*********************************************************************************************************************/
    // //* Switch As Wire Cases **********************************************************************************************/
    // //*********************************************************************************************************************/

    @Test
    public void testSwitchAsWire() {
        
        /*
            switch (OR) | switch (OR) | switch (OR) | lightbulb (OR)
            boulder     | switch (OR) | switch (OR) | 
            player      |             |             |
        */

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_orSwitch", "c_logicTests_basic");

        // assert player is in the right starting position
        EntityResponse initPlayer = getPlayer(initDungonRes).get();
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        assertEquals(expectedPlayer, initPlayer);

        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);


        DungeonResponse newDungonRes = dmc.tick(Direction.UP);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);
        
        newDungonRes = dmc.tick(Direction.UP);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 1);

        /**
                   |                |                |              | wire           | 
                   | lightbulb (OR) | wire           | wire         | wire           | lightbulb (OR)
                   |                |                | wire         |                |
            player | boulder        | switch (OR)    | switch (AND) | lightbulb (OR) |
                   |                |                | wire         | wire           |
                   |                | wire           | wire         | lightbulb (OR) |
                   |                | lightbulb (OR) | wire         |                |
        **/
        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_switch2", "c_logicTests_basic");
        
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 5);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        
        newDungonRes = dmc.tick(Direction.RIGHT);
 
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 5);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);

        newDungonRes = dmc.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 5);

        newDungonRes = dmc.tick(Direction.RIGHT);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 5);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);

        /**
                   | lightbulb    |                 |      |              | lightbulb
                   | switch (AND) | wire            | wire | wire         | switch (CO_AND)
                   |              |                 | wire |              |
            player | boulder      | switch (CO_AND) | wire | switch (XOR) | lightbulb
                   |              |                 | wire | wire         |
                   |              | wire            | wire | switch (AND) | lightbulb
                   | lightbulb    | switch (CO_AND) | wire |              |
        **/

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_switch3", "c_logicTests_basic");
        
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 5);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        
        newDungonRes = dmc.tick(Direction.RIGHT);
 
        assertEquals(3, getEntitiesStream(newDungonRes, "light_bulb_off").count());
        assertEquals(2, getEntitiesStream(newDungonRes, "light_bulb_on").count());

        newDungonRes = dmc.tick(Direction.RIGHT);

        assertEquals(5, getEntitiesStream(newDungonRes, "light_bulb_off").count());
        assertEquals(0, getEntitiesStream(newDungonRes, "light_bulb_on").count());

        newDungonRes = dmc.tick(Direction.RIGHT);
        assertEquals(2, getEntitiesStream(newDungonRes, "light_bulb_off").count());
        assertEquals(3, getEntitiesStream(newDungonRes, "light_bulb_on").count());

        /*
                   | boulder | switch          |
            player |         | switch (CO_AND) | lightbulb
                   | boulder | switch          |
        */

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_switch4", "c_logicTests_basic");

        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);

        dmc.tick(Direction.UP);
        newDungonRes = dmc.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);

        newDungonRes = dmc.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);

        /**        |         |             |      |              |
            player | boulder | switch (OR) | wire | switch (XOR) | lightbulb
                   |         |             | wire | wire         |
                   |         |             | wire | switch (AND) | lightbulb
        **/

        dmc = new DungeonManiaController();
        initDungonRes = dmc.newGame("d_logicTest_switchXor", "c_logicTests_basic");
        
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 2);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        
        newDungonRes = dmc.tick(Direction.RIGHT);
 
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);

        newDungonRes = dmc.tick(Direction.RIGHT);

        assertEquals(2, getEntitiesStream(newDungonRes, "light_bulb_off").count());
        assertEquals(0, getEntitiesStream(newDungonRes, "light_bulb_on").count());

        newDungonRes = dmc.tick(Direction.RIGHT);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 2);
    }

    // //*********************************************************************************************************************/
    // //* Bombs Tests *******************************************************************************************************/
    // //*********************************************************************************************************************/

    @Test
    public void testLogicBombOr() {
        
        
        //* Activating the wire and then placing down the bomb */
        /*
            switch  | wire      |  
            boulder | wire      | lightbulb (OR)
            player  | bomb (OR) |
        */

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_bombOr1", "c_logicTests_bomb5");

        assertTrue(getEntitiesStream(initDungonRes, "wire").count() == 2);
        assertTrue(getEntitiesStream(initDungonRes, "switch").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "boulder").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);

        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.LEFT);
        DungeonResponse newDungonRes = dmc.tick(Direction.UP);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        String bombId1 = getInventory(newDungonRes, "bomb").get(0).getId();

        dmc.tick(Direction.DOWN); 
        newDungonRes = dmc.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        newDungonRes = assertDoesNotThrow(() -> {return dmc.tick(bombId1);});  

        
        assertTrue(getEntitiesStream(newDungonRes, "wire").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "switch").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "boulder").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        //* Placing the bomb down first and then activating the wire */
        /*
            switch  | wire      |  
            boulder | wire      | lightbulb (OR)       
            player  | bomb (OR) |  
        */

        DungeonManiaController dmc2 = new DungeonManiaController();
        initDungonRes = dmc2.newGame("d_logicTest_bombOr1", "c_logicTests_bomb5");

        assertTrue(getEntitiesStream(initDungonRes, "wire").count() == 2);
        assertTrue(getEntitiesStream(initDungonRes, "switch").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "boulder").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);

        newDungonRes = dmc2.tick(Direction.RIGHT); 
        String bombId2 = getInventory(newDungonRes, "bomb").get(0).getId();
        newDungonRes = assertDoesNotThrow(() -> {return dmc2.tick(bombId2);});

        dmc2.tick(Direction.LEFT);
        newDungonRes = dmc2.tick(Direction.UP);

        assertTrue(getEntitiesStream(newDungonRes, "wire").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "switch").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "boulder").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0); 
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);
    }


    @Test
    public void testLogicBombAnd() {
        
        //* Placing the bomb down first and then activating the wire */
        /*
            switch  | wire       | lightbulb (OR)
            boulder | wire       | wire
            player  | bomb (AND) | wire
        */
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_bombAnd1", "c_logicTests_bomb5");

        assertTrue(getEntitiesStream(initDungonRes, "wire").count() == 4);
        assertTrue(getEntitiesStream(initDungonRes, "switch").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "boulder").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);

        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);
        String bombId1 = getInventory(newDungonRes, "bomb").get(0).getId(); 
        newDungonRes = assertDoesNotThrow(() -> {return dmc.tick(bombId1);});

        dmc.tick(Direction.LEFT);
        newDungonRes = dmc.tick(Direction.UP);

        assertTrue(getEntitiesStream(newDungonRes, "wire").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "switch").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "boulder").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0); 
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0); 
       

        //* Activating the wire and then placing down the bomb */
        /*
            switch  | wire       | lightbulb (OR)
            boulder | wire       | wire
            player  | bomb (AND) | wire
        */

        DungeonManiaController dmc2 = new DungeonManiaController();
        initDungonRes = dmc2.newGame("d_logicTest_bombAnd1", "c_logicTests_bomb5");

        assertTrue(getEntitiesStream(initDungonRes, "wire").count() == 4);
        assertTrue(getEntitiesStream(initDungonRes, "switch").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "boulder").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);

        dmc2.tick(Direction.RIGHT);
        dmc2.tick(Direction.LEFT);
        newDungonRes = dmc2.tick(Direction.UP);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        String bombId2 = getInventory(newDungonRes, "bomb").get(0).getId();

        dmc2.tick(Direction.DOWN); 
        newDungonRes = dmc2.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        newDungonRes = assertDoesNotThrow(() -> {return dmc2.tick(bombId2);});  

        assertTrue(getEntitiesStream(newDungonRes, "wire").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "switch").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "boulder").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);
        

        //* Placing the bomb down first and then activating the wire */
        /*
            switch  | wire       | 
            boulder | wire       | lightbulb (OR)
            player  | bomb (AND) | 
        */
        DungeonManiaController dmc3 = new DungeonManiaController();
        initDungonRes = dmc3.newGame("d_logicTest_bombAnd2", "c_logicTests_bomb5");

        assertTrue(getEntitiesStream(initDungonRes, "wire").count() == 2);
        assertTrue(getEntitiesStream(initDungonRes, "switch").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "boulder").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);

        newDungonRes = dmc3.tick(Direction.RIGHT);
        String bombId3 = getInventory(newDungonRes, "bomb").get(0).getId(); 
        newDungonRes = assertDoesNotThrow(() -> {return dmc3.tick(bombId3);}); 

        dmc3.tick(Direction.LEFT);
        newDungonRes = dmc3.tick(Direction.UP);

        assertTrue(getEntitiesStream(newDungonRes, "wire").count() == 2);
        assertTrue(getEntitiesStream(newDungonRes, "switch").count() == 1); 
        assertTrue(getEntitiesStream(newDungonRes, "boulder").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0); 
        

        //* Activating the wire and then placing down the bomb */
        /*
            switch  | wire       | 
            boulder | wire       | lightbulb (OR)
            player  | bomb (AND) | 
        */
        DungeonManiaController dmc4 = new DungeonManiaController();
        initDungonRes = dmc4.newGame("d_logicTest_bombAnd2", "c_logicTests_bomb5");

        assertTrue(getEntitiesStream(initDungonRes, "wire").count() == 2);
        assertTrue(getEntitiesStream(initDungonRes, "switch").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "boulder").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);

        dmc4.tick(Direction.RIGHT);
        dmc4.tick(Direction.LEFT);
        newDungonRes = dmc4.tick(Direction.UP);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        String bombId4 = getInventory(newDungonRes, "bomb").get(0).getId();

        dmc4.tick(Direction.DOWN); 
        newDungonRes = dmc4.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        newDungonRes = assertDoesNotThrow(() -> {return dmc4.tick(bombId4);});  

        assertTrue(getEntitiesStream(newDungonRes, "wire").count() == 2);
        assertTrue(getEntitiesStream(newDungonRes, "switch").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "boulder").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);


        /* 
                   | boulder | switch
            player |         | bomb (AND)
                   | boulder | switch
        */

        DungeonManiaController dmc5 = new DungeonManiaController();
        initDungonRes = dmc5.newGame("d_logicTest_bombAnd3", "c_logicTests_bomb5");

        dmc5.tick(Direction.RIGHT);
        newDungonRes = dmc5.tick(Direction.RIGHT);
        String bombId5 = getInventory(newDungonRes, "bomb").get(0).getId(); 
        assertDoesNotThrow(() -> {return dmc5.tick(bombId5);}); 
        dmc5.tick(Direction.LEFT);
        dmc5.tick(Direction.LEFT);


        assertTrue(getEntitiesStream(initDungonRes, "switch").count() == 2);
        assertTrue(getEntitiesStream(initDungonRes, "boulder").count() == 2);

        dmc5.tick(Direction.UP);
        dmc5.tick(Direction.RIGHT);
        dmc5.tick(Direction.LEFT);
        newDungonRes = dmc5.tick(Direction.DOWN);

        assertTrue(getEntitiesStream(newDungonRes, "switch").count() == 2);
        assertTrue(getEntitiesStream(newDungonRes, "boulder").count() == 2);

        dmc5.tick(Direction.DOWN);
        newDungonRes = dmc5.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(newDungonRes, "switch").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "boulder").count() == 0); 
    }

    @Test
    public void testLogicBombXor() {
        //* Placing the bomb down first and then activating the wire */
        /*
            switch  | wire       | lightbulb (OR)
            boulder | wire       | wire
            player  | bomb (XOR) | wire
        */
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_bombXor1", "c_logicTests_bomb5");

        assertTrue(getEntitiesStream(initDungonRes, "wire").count() == 4);
        assertTrue(getEntitiesStream(initDungonRes, "switch").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "boulder").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);

        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);
        String bombId1 = getInventory(newDungonRes, "bomb").get(0).getId(); 
        newDungonRes = assertDoesNotThrow(() -> {return dmc.tick(bombId1);});

        dmc.tick(Direction.LEFT);
        newDungonRes = dmc.tick(Direction.UP);

        assertTrue(getEntitiesStream(newDungonRes, "wire").count() == 4);
        assertTrue(getEntitiesStream(newDungonRes, "switch").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "boulder").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0); 

        //* Activating the wire and then placing down the bomb */
        /*
            switch  | wire       | lightbulb (OR)
            boulder | wire       | wire
            player  | bomb (XOR) | wire
        */
        DungeonManiaController dmc2 = new DungeonManiaController();
        initDungonRes = dmc2.newGame("d_logicTest_bombXor1", "c_logicTests_bomb5");

        assertTrue(getEntitiesStream(initDungonRes, "wire").count() == 4);
        assertTrue(getEntitiesStream(initDungonRes, "switch").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "boulder").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);

        dmc2.tick(Direction.RIGHT);
        dmc2.tick(Direction.LEFT);
        newDungonRes = dmc2.tick(Direction.UP);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        String bombId2 = getInventory(newDungonRes, "bomb").get(0).getId();

        dmc2.tick(Direction.DOWN); 
        newDungonRes = dmc2.tick(Direction.LEFT);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        newDungonRes = assertDoesNotThrow(() -> {return dmc2.tick(bombId2);});  

        assertTrue(getEntitiesStream(newDungonRes, "wire").count() == 4);
        assertTrue(getEntitiesStream(newDungonRes, "switch").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "boulder").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1); 
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        //* Placing the bomb down first and then activating the wire */
        /*
            switch  | wire       | 
            boulder | wire       | lightbulb (OR)
            player  | bomb (XOR) | 
        */

        DungeonManiaController dmc3 = new DungeonManiaController();
        initDungonRes = dmc3.newGame("d_logicTest_bombXor2", "c_logicTests_bomb5");

        assertTrue(getEntitiesStream(initDungonRes, "wire").count() == 2);
        assertTrue(getEntitiesStream(initDungonRes, "switch").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "boulder").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);

        newDungonRes = dmc3.tick(Direction.RIGHT);
        String bombId3 = getInventory(newDungonRes, "bomb").get(0).getId(); 
        newDungonRes = assertDoesNotThrow(() -> {return dmc3.tick(bombId3);}); 

        dmc3.tick(Direction.LEFT);
        newDungonRes = dmc3.tick(Direction.UP);

        assertEquals(0,getEntitiesStream(newDungonRes, "wire").count());
        assertEquals(0,getEntitiesStream(newDungonRes, "switch").count());
        assertEquals(0,getEntitiesStream(newDungonRes, "boulder").count());
        assertEquals(0,getEntitiesStream(newDungonRes, "light_bulb_on").count());
        assertEquals(0,getEntitiesStream(newDungonRes, "light_bulb_off").count());


        //* Activating the wire and then placing down the bomb */
        /*
            switch  | wire       | 
            boulder | wire       | lightbulb (OR)
            player  | bomb (XOR) | 
        */
        DungeonManiaController dmc4 = new DungeonManiaController();
        initDungonRes = dmc4.newGame("d_logicTest_bombXor2", "c_logicTests_bomb5");

        assertTrue(getEntitiesStream(initDungonRes, "wire").count() == 2);
        assertTrue(getEntitiesStream(initDungonRes, "switch").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "boulder").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);

        dmc4.tick(Direction.RIGHT);
        dmc4.tick(Direction.LEFT);
        newDungonRes = dmc4.tick(Direction.UP);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        String bombId4 = getInventory(newDungonRes, "bomb").get(0).getId();

        dmc4.tick(Direction.DOWN); 
        newDungonRes = dmc4.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        newDungonRes = assertDoesNotThrow(() -> {return dmc4.tick(bombId4);});  

        assertEquals(0,getEntitiesStream(newDungonRes, "wire").count());
        assertEquals(0,getEntitiesStream(newDungonRes, "switch").count());
        assertEquals(0,getEntitiesStream(newDungonRes, "boulder").count());
        assertEquals(0,getEntitiesStream(newDungonRes, "light_bulb_on").count());
        assertEquals(0,getEntitiesStream(newDungonRes, "light_bulb_off").count());
    }

    @Test
    public void testLogicBombCo_and() {
        

        //* Placing the bomb down first and then activating the wire */
        /*
            switch  | wire          | lightbulb (OR)
            boulder | wire          | wire
            player  | bomb (CO_AND) | wire
        */
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_logicTest_bombCo_and1", "c_logicTests_bomb5");

        assertTrue(getEntitiesStream(initDungonRes, "wire").count() == 4);
        assertTrue(getEntitiesStream(initDungonRes, "switch").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "boulder").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);

        DungeonResponse newDungonRes = dmc.tick(Direction.RIGHT);
        String bombId1 = getInventory(newDungonRes, "bomb").get(0).getId(); 
        newDungonRes = assertDoesNotThrow(() -> {return dmc.tick(bombId1);});

        dmc.tick(Direction.LEFT);
        newDungonRes = dmc.tick(Direction.UP);

        assertTrue(getEntitiesStream(newDungonRes, "wire").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "switch").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "boulder").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0); 
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0); 


        //* Activating the wire and then placing down the bomb */
        /*
            switch  | wire          | lightbulb (OR)
            boulder | wire          | wire
            player  | bomb (CO_AND) | wire
        */
        DungeonManiaController dmc2 = new DungeonManiaController();
        initDungonRes = dmc2.newGame("d_logicTest_bombCo_and1", "c_logicTests_bomb5");

        assertTrue(getEntitiesStream(initDungonRes, "wire").count() == 4);
        assertTrue(getEntitiesStream(initDungonRes, "switch").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "boulder").count() == 1);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(initDungonRes, "light_bulb_off").count() == 1);

        dmc2.tick(Direction.RIGHT);
        dmc2.tick(Direction.LEFT);
        newDungonRes = dmc2.tick(Direction.UP);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        dmc2.tick(Direction.DOWN); 
        newDungonRes = dmc2.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 1);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0);

        String bombId2 = getInventory(newDungonRes, "bomb").get(0).getId();
        newDungonRes = assertDoesNotThrow(() -> {return dmc2.tick(bombId2);});  

        assertTrue(getEntitiesStream(newDungonRes, "wire").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "switch").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "boulder").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_on").count() == 0);
        assertTrue(getEntitiesStream(newDungonRes, "light_bulb_off").count() == 0); 


        //* Placing the bomb down first and then activating */
        /*
                   | boulder | switch
            player |         | bomb (CO_AND)
                   | boulder | switch
        */

        DungeonManiaController dmc3 = new DungeonManiaController();
        initDungonRes = dmc3.newGame("d_logicTest_bombCo_and2", "c_logicTests_bomb5");

        assertTrue(getEntitiesStream(initDungonRes, "switch").count() == 2);
        assertTrue(getEntitiesStream(initDungonRes, "boulder").count() == 2);
        
        dmc3.tick(Direction.RIGHT);
        newDungonRes = dmc3.tick(Direction.RIGHT);
        String bombId3 = getInventory(newDungonRes, "bomb").get(0).getId(); 
        newDungonRes = assertDoesNotThrow(() -> {return dmc3.tick(bombId3);}); 

        dmc3.tick(Direction.LEFT);
        dmc3.tick(Direction.LEFT);
        dmc3.tick(Direction.UP);
        dmc3.tick(Direction.RIGHT);
        dmc3.tick(Direction.LEFT);
        dmc3.tick(Direction.DOWN);
        dmc3.tick(Direction.DOWN);
        newDungonRes = dmc3.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(initDungonRes, "switch").count() == 2);
        assertTrue(getEntitiesStream(initDungonRes, "boulder").count() == 2);


        //* Activating the switches and then placing down the bomb */
        /*
                   | boulder | switch
            player |         | bomb (CO_AND)
                   | boulder | switch
        */
        DungeonManiaController dmc4 = new DungeonManiaController();
        initDungonRes = dmc4.newGame("d_logicTest_bombCo_and2", "c_logicTests_bomb5");

        assertTrue(getEntitiesStream(initDungonRes, "switch").count() == 2);
        assertTrue(getEntitiesStream(initDungonRes, "boulder").count() == 2);
        
        dmc4.tick(Direction.RIGHT);
        newDungonRes = dmc4.tick(Direction.RIGHT);
        String bombId4 = getInventory(newDungonRes, "bomb").get(0).getId(); 
        newDungonRes = assertDoesNotThrow(() -> {return dmc4.tick(bombId4);}); 

        dmc4.tick(Direction.LEFT); 
        dmc4.tick(Direction.LEFT);
        dmc4.tick(Direction.UP);
        dmc4.tick(Direction.RIGHT);
        dmc4.tick(Direction.LEFT);
        dmc4.tick(Direction.DOWN);
        dmc4.tick(Direction.DOWN);
        newDungonRes = dmc4.tick(Direction.RIGHT);

        assertTrue(getEntitiesStream(initDungonRes, "switch").count() == 2);
        assertTrue(getEntitiesStream(initDungonRes, "boulder").count() == 2);
    }
}
