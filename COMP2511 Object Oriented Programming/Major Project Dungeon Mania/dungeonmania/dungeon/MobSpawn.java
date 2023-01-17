package dungeonmania.dungeon;

import dungeonmania.entities.Boulder;
import dungeonmania.entities.moving.Spider;
import dungeonmania.util.Position;
import org.json.JSONObject;

import java.util.Random;

public class MobSpawn {
    private final Dungeon dungeon;
    private final int spiderSpawnRate;
    private int spiderSpawnCounter = 0;

    public MobSpawn(Dungeon dungeon, JSONObject configJSON) {
        this.dungeon = dungeon;
        this.spiderSpawnRate = configJSON.getInt("spider_spawn_rate");
    }

    public void tick() {
        if (spiderSpawnRate != 0) {
            spiderSpawnCounter++;
            if (spiderSpawnCounter >= spiderSpawnRate) {
                spiderSpawnCounter = 0;

                Random randomizer = dungeon.getRandomizer();

                Position position;
                do {
                    position = new Position(randomizer.nextInt(25), randomizer.nextInt(25));
                } while (dungeon.hasEntityOnTile(position, Boulder.class));

                dungeon.addEntity(new Spider(dungeon.generateEntityId(), position, dungeon.getConfigJSON()));
            }
        }
    }
}
