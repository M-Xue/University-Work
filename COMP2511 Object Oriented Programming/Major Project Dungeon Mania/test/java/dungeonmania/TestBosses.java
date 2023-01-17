package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static dungeonmania.TestUtils.*;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestBosses {
    // ASSASSIN

    @Test
    @DisplayName("Test battle assassin - player loses")
    public void testBattleAssassinPlayerLoses() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = genericAssassinSequence(controller, "c_battleTests_basicAssassinPlayerDies");
        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertBattleCalculations("assassin", battle, false, "c_battleTests_basicAssassinPlayerDies");
    }

    private static DungeonResponse genericAssassinSequence(DungeonManiaController controller, String configFile) {
        /*
        *  exit   wall  wall  wall
        * player  [  ]  assa  wall
        *  wall   wall  wall  wall
        */
        DungeonResponse initialResponse = controller.newGame("d_battleTest_basicAssassin", configFile);
        int mercenaryCount = countEntityOfType(initialResponse, "assassin");
    
        assertEquals(1, countEntityOfType(initialResponse, "player"));
        assertEquals(1, mercenaryCount);
        return controller.tick(Direction.RIGHT);
    }

    @Test
    @DisplayName("Test battle assassin - player wins")
    public void testBattleAssassinPlayerWins() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = genericAssassinSequence(controller, "c_battleTests_basicAssassinAssassinDies");
        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertBattleCalculations("mercenary", battle, true, "c_battleTests_basicAssassinAssassinDies");
    } 

    @Test
    @DisplayName("Test battle assassin - player wins")
    public void testBattleMultipleAssassins() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = multipleAssassinSequence(controller, "c_battleTests_basicAssassinAssassinDies");
        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertBattleCalculations("assassin", battle, true, "c_battleTests_basicAssassinAssassinDies");
    } 

    @Test
    @DisplayName("Test successful bribing")
    public void testSuccessfulAssassinBribing() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_assassinTest_movementAssassinWithCoin", "c_movementTest_movementAssassin");

        // collect coin 
        DungeonResponse res = dmc.tick(Direction.RIGHT);

        // bribe and make assassin an ally
        assertDoesNotThrow(() -> {
            dmc.interact(getEntities(res, "assassin").get(0).getId());
        });
    } 

    @Test
    @DisplayName("Test unsuccessful bribing")
    public void testUnsuccessfulAssassinBribing() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_assassinTest_movementAssassinWithCoin", "c_movementTest_movementAssassin");

        DungeonResponse res = dmc.tick(Direction.DOWN);

        // not enough money to bribe
        assertThrows(InvalidActionException.class, () -> {
            dmc.interact(getEntities(res, "assassin").get(0).getId());
        });
    } 

    // HYDRA

    @Test
    @DisplayName("Test battle hydra - player wins")
    public void testBattleHydraLoses() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = basicHydraSequence(controller, "c_battleTests_hydraDies");
        if (postBattleResponse.getBattles().size() > 0) {
            BattleResponse battle = postBattleResponse.getBattles().get(0);
            assertBattleCalculations("hydra", battle, true, "c_battleTests_hydraDies");
        }
    } 

    private static DungeonResponse basicHydraSequence(DungeonManiaController controller, String configFile) {
        DungeonResponse dmc = controller.newGame("d_battleTest_hydras", configFile);
        
        assertEquals(1, countEntityOfType(dmc, "player"));
        assertEquals(14, countEntityOfType(dmc, "hydra"));
        dmc = controller.tick(Direction.RIGHT);
        dmc = controller.tick(Direction.LEFT);
        dmc = controller.tick(Direction.RIGHT);
        dmc = controller.tick(Direction.RIGHT);
        dmc = controller.tick(Direction.LEFT);
        assertTrue(countEntityOfType(dmc, "hydra") <= 14);
        assertEquals(1, countEntityOfType(dmc, "player"));
        return dmc;
    }

    @Test
    @DisplayName("Test hydra movement")
    public void testHydraMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_hydra_movementHydra", "c_hydra_movementHydra");
        Position oldPos = getEntities(res, "hydra").get(0).getPosition();

        Position newPos;
        for (int i = 0; i <= 25; ++i) {
            res = dmc.tick(Direction.UP);
            newPos = getEntities(res, "hydra").get(0).getPosition();
            assertNotEquals(oldPos, newPos);
            assertTrue(Position.isAdjacent(oldPos, newPos));
            oldPos = newPos;
        }
    } 

    // utils

    private void assertBattleCalculations(String enemyType, BattleResponse battle, boolean enemyDies, String configFilePath) {
        List<RoundResponse> rounds = battle.getRounds();
        double playerHealth = Double.parseDouble(getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(getValueFromConfigFile(enemyType + "_attack", configFilePath));

        for (RoundResponse round : rounds) {
            assertEquals(-(enemyAttack / 10), round.getDeltaCharacterHealth(), 0.001);
            assertEquals(-(playerAttack / 5), round.getDeltaEnemyHealth(), 0.001);
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();
        }

        if (enemyDies) {
            assertTrue(enemyHealth <= 0);
        } else {
            assertTrue(playerHealth <= 0);
        }
    }

    private static DungeonResponse multipleAssassinSequence(DungeonManiaController controller, String configFile) {
        /*
        *                     exit      
        * player  [  ]  assassin  [  ] assassin  [  ] assassin [  ]
        *    
        */
        DungeonResponse dmc = controller.newGame("d_battleTest_multipleAssassins", configFile);

        assertEquals(1, countEntityOfType(dmc, "player"));
        assertEquals(3, countEntityOfType(dmc, "assassin"));
        dmc = controller.tick(Direction.RIGHT);
        assertEquals(2, countEntityOfType(dmc, "assassin"));
        dmc = controller.tick(Direction.RIGHT);
        assertEquals(1, countEntityOfType(dmc, "assassin"));
        dmc = controller.tick(Direction.RIGHT);
        assertEquals(0, countEntityOfType(dmc, "assassin"));
        dmc = controller.tick(Direction.UP);
        assertEquals(0, countEntityOfType(dmc, "assassin"));
        assertEquals(1, countEntityOfType(dmc, "player"));
        return dmc;
    }
}
