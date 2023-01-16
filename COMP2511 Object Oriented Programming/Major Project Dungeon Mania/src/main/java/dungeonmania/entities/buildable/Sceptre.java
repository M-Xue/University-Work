package dungeonmania.entities.buildable;

import dungeonmania.entities.ItemEntity;
import dungeonmania.entities.collectable.Arrow;
import dungeonmania.entities.collectable.Key;
import dungeonmania.entities.collectable.SunStone;
import dungeonmania.entities.collectable.Treasure;
import dungeonmania.entities.collectable.Wood;
import dungeonmania.util.Pair;
import org.json.JSONObject;

import java.util.List;

public class Sceptre extends BuildableEntity {
    private final int mindControlDuration;

    public Sceptre(int id, JSONObject configJSON) {
        super(id, "sceptre");
        this.mindControlDuration = configJSON.getInt("mind_control_duration");
    }

    public Sceptre(int id, JSONObject entityJSON, JSONObject configJSON) {
        this(id, configJSON);
    }

    public int getMindControlDuration() {
        return mindControlDuration;
    }

    @Override
    public List<List<Pair<Class<? extends ItemEntity>, Integer>>> getMaterials() {
        return List.of(
                List.of(
                        Pair.of(Wood.class, 1),
                        Pair.of(Arrow.class, 2)
                ),    
                List.of(
                        Pair.of(SunStone.class, 2), // If choose sunstone out of this list, 2 are needed because the next list also requires a sunstone.
                        Pair.of(Treasure.class, 1),
                        Pair.of(Key.class, 1)
                ),
                List.of(
                        Pair.of(SunStone.class, 1)
                )
        );
    }

}

