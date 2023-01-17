package dungeonmania.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.dungeon.Dungeon;
import dungeonmania.entities.player.Player;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.entities.ItemEntity;
import dungeonmania.entities.LivingEntity;
import dungeonmania.util.Pair;

public class Battle {
    private final Player player;
    private final LivingEntity enemy;
    private final double initialPlayerHealth;
    private final double initialEnemyHealth;
    private final List<Round> rounds = new ArrayList<>();

    public Battle(Player player, LivingEntity enemy) {
        this.player = player;
        this.enemy = enemy;
        this.initialPlayerHealth = player.getHealth();
        this.initialEnemyHealth = enemy.getHealth();
        doBattle();
    }

    private void doBattle() {
        while (!player.isDead() && !enemy.isDead()) {
            Pair<Double, List<ItemEntity>> playerAttackResponse = player.attack();
            Pair<Double, List<ItemEntity>> enemyAttackResponse = enemy.attack();

            List<ItemEntity> itemsUsed = new ArrayList<>(playerAttackResponse.y);

            double playerAttack = playerAttackResponse.x;
            double enemyAttack = enemyAttackResponse.x;

            Pair<Double, List<ItemEntity>> playerOnDamagedResponse = player.onDamaged(enemyAttack);
            Pair<Double, List<ItemEntity>> enemyOnDamagedResponse = enemy.onDamaged(playerAttack);

            itemsUsed.addAll(playerOnDamagedResponse.y);

            double deltaPlayerHealth = playerOnDamagedResponse.x;
            double deltaEnemyHealth = enemyOnDamagedResponse.x;

            rounds.add(new Round(deltaPlayerHealth, deltaEnemyHealth, itemsUsed));
        }

        Dungeon dungeon = player.getDungeon();
        if (enemy.isDead()) {
            this.player.incrementEnemiesKilled();
            dungeon.removeEntity(enemy);
        }
        if (player.isDead()) {
            dungeon.removeEntity(player);
            if (enemy instanceof Player) {
                ((Player) enemy).incrementEnemiesKilled();
            }
        }
    }

    public BattleResponse getResponse() {
        List<RoundResponse> roundResponses = rounds.stream().map(round -> round.getResponse())
                .collect(Collectors.toList());
        return new BattleResponse(
                enemy.getType(),
                roundResponses,
                initialPlayerHealth,
                initialEnemyHealth
        );
    }

    @Override
    public String toString() {
        return "Battle{" +
                "player=" + player +
                ", enemy=" + enemy +
                ", iPHealth=" + initialPlayerHealth +
                ", iEHealth=" + initialEnemyHealth +
                ", rounds=" + rounds +
                '}';
    }

}
