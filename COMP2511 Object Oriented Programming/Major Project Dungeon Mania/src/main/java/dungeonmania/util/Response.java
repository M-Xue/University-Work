package dungeonmania.util;

import dungeonmania.response.models.*;

/**
 * A helper class to print out responses.
 */
public class Response {
    public static void print(EntityResponse response) {
        System.out.printf("id: %s\n", response.getId());
        System.out.printf("type: %s\n", response.getType());
        System.out.printf("position: %s\n", response.getPosition());
        System.out.printf("isInteractable: %s\n", response.isInteractable());
    }

    public static void print(ItemResponse response) {
        System.out.printf("id: %s\n", response.getId());
        System.out.printf("type: %s\n", response.getType());
    }

    public static void print(RoundResponse response) {
        System.out.printf("deltaPlayerHealth: %.2f\n", response.getDeltaCharacterHealth());
        System.out.printf("deltaEnemyHealth: %.2f\n", response.getDeltaEnemyHealth());
        System.out.println();

        System.out.println("weaponryUsed:");
        for (ItemResponse item : response.getWeaponryUsed()) {
            print(item);
        }
    }

    public static void print(BattleResponse response) {
        System.out.printf("enemy: %s\n", response.getEnemy());
        System.out.printf("initialPlayerHealth: %.2f\n", response.getInitialPlayerHealth());
        System.out.printf("initialEnemyHealth: %.2f\n", response.getInitialEnemyHealth());
        System.out.println();

        System.out.println("rounds:");
        for (RoundResponse round : response.getRounds()) {
            print(round);
        }
    }

    public static void print(DungeonResponse response) {
        System.out.printf("dungeonId: %s\n", response.getDungeonId());
        System.out.printf("dungeonName: %s\n", response.getDungeonName());
        System.out.println();

        System.out.println("entities:");
        for (EntityResponse entity : response.getEntities()) {
            print(entity);
            System.out.println();
        }

        System.out.println("inventory:");
        for (ItemResponse item : response.getInventory()) {
            print(item);
            System.out.println();
        }

        System.out.println("battles:");
        for (BattleResponse battle : response.getBattles()) {
            print(battle);
            System.out.println();
        }

        System.out.println("buildables:");
        for (String buildable : response.getBuildables()) {
            System.out.println(buildable);
        }
        System.out.println();

        System.out.printf("goals: %s\n", response.getGoals());
    }
}
