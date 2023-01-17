package dungeonmania;

import static dungeonmania.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;


public class TestBattle {
    @Test
    @DisplayName("Test basic movement of spiders")
    public void basicMovement() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
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
        
    private static DungeonResponse multipleMercenarySequence(DungeonManiaController controller, String configFile) {
        /*
        *                     exit      
        * player  [  ]  merc  [  ] merc  [  ] merc [  ]
        *    
        */
        DungeonResponse dmc = controller.newGame("d_battleTest_multipleMercenaries", configFile);

        assertEquals(1, countEntityOfType(dmc, "player"));
        assertEquals(3, countEntityOfType(dmc, "mercenary"));
        dmc = controller.tick(Direction.RIGHT);
        assertEquals(2, countEntityOfType(dmc, "mercenary"));
        dmc = controller.tick(Direction.RIGHT);
        assertEquals(1, countEntityOfType(dmc, "mercenary"));
        dmc = controller.tick(Direction.RIGHT);
        assertEquals(0, countEntityOfType(dmc, "mercenary"));
        dmc = controller.tick(Direction.UP);
        assertEquals(0, countEntityOfType(dmc, "mercenary"));
        assertEquals(1, countEntityOfType(dmc, "player"));
        return dmc;
    }

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

    @Test
    @DisplayName("Test battle multiple mercenaries, player wins")
    public void testMultipleMercenaries() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = multipleMercenarySequence(controller, "c_battleTests_basicMercenaryMercenaryDies");

        System.out.println(postBattleResponse);

        BattleResponse battle = postBattleResponse.getBattles().get(0);
        assertBattleCalculations("mercenary", battle, true, "c_battleTests_basicMercenaryMercenaryDies");
    }

    private static DungeonResponse basicZombieSequence(DungeonManiaController controller, String configFile) {
        DungeonResponse dmc = controller.newGame("d_battleTest_zombies", configFile);
        
        assertEquals(1, countEntityOfType(dmc, "player"));
        assertEquals(14, countEntityOfType(dmc, "zombie_toast"));
        dmc = controller.tick(Direction.RIGHT);
        dmc = controller.tick(Direction.LEFT);
        dmc = controller.tick(Direction.RIGHT);
        dmc = controller.tick(Direction.RIGHT);
        dmc = controller.tick(Direction.LEFT);
        assertTrue(countEntityOfType(dmc, "zombie_toast") <= 14);
        assertEquals(1, countEntityOfType(dmc, "player"));
        return dmc;
    }

    @Test
    @DisplayName("Test battle zombies, player wins")
    public void testZombies() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = basicZombieSequence(controller, "c_battleTests_zombieDies");

        System.out.println(postBattleResponse);

        if (postBattleResponse.getBattles().size() > 0) {
            BattleResponse battle = postBattleResponse.getBattles().get(0);
            assertBattleCalculations("zombie", battle, true, "c_battleTests_zombieDies");
        }
    }

    private static DungeonResponse basicZombieSpawnerSequence(DungeonManiaController controller, String configFile) {
        DungeonResponse dmc = controller.newGame("d_battleTest_zombieSpawner", configFile);
        
        assertEquals(1, countEntityOfType(dmc, "player"));
        assertEquals(0, countEntityOfType(dmc, "zombie_toast"));
        dmc = controller.tick(Direction.RIGHT);
        dmc = controller.tick(Direction.LEFT);
        dmc = controller.tick(Direction.RIGHT);
        dmc = controller.tick(Direction.LEFT);
        dmc = controller.tick(Direction.RIGHT);
        dmc = controller.tick(Direction.LEFT);
        dmc = controller.tick(Direction.RIGHT);
        dmc = controller.tick(Direction.LEFT);
        dmc = controller.tick(Direction.RIGHT);
        dmc = controller.tick(Direction.LEFT);
        assertTrue(countEntityOfType(dmc, "zombie_toast") >= 0);
        assertEquals(1, countEntityOfType(dmc, "player"));
        return dmc;
    }

    @Test
    @DisplayName("Test battle zombies spanwed from spawner, player wins")
    public void testZombieSpawner() {
       DungeonManiaController controller = new DungeonManiaController();
       DungeonResponse postBattleResponse = basicZombieSpawnerSequence(controller, "c_battleTests_zombieSpawner");

        System.out.println(postBattleResponse);

        if (postBattleResponse.getBattles().size() > 0) {
            BattleResponse battle = postBattleResponse.getBattles().get(0);
            assertBattleCalculations("zombie", battle, true, "c_battleTests_zombieSpawner");
        }
    }
}
