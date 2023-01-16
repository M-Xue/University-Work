package dungeonmania.entities.buildable;

import dungeonmania.entities.ItemEntity;
import dungeonmania.util.Pair;

import java.util.List;

public abstract class BuildableEntity extends ItemEntity {
    public BuildableEntity(int id, String type) {
        super(id, type);
    }

    /**
     * List of list of materials (with numbers) required
     * to build this entity.
     * AND over top level list
     * OR over second level list
     * Order of materials in second level list is important.
     * Earlier materials take precedence as candidate for 
     * crafting.
     * E.g. In this example, if player has sunstone and
     * treasure, sunstone will be used to craft buildable.
        List.of(
                List.of(
                        Pair.of(Wood.class, 1),
                        Pair.of(Arrow.class, 2)
                ),    
                List.of(
                        Pair.of(SunStone.class, 1),
                        Pair.of(Treasure.class, 1),
                        Pair.of(Key.class, 1)
                )
        );
     * @return
     */
    public abstract List<List<Pair<Class<? extends ItemEntity>, Integer>>> getMaterials();

    /**
     * Some buildables (midnight_armour) have complexity
     * conditions that determine whether it can be built.
     * They will override this method.
     * @return
     */
    public boolean canBuild() {
        return true;
    }
}