package dungeonmania.battle;

import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.entities.ItemEntity;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.response.models.RoundResponse;

public class Round {

    private final double deltaPlayerHealth;
    private final double deltaEnemyHealth;
    private final List<ItemEntity> weaponryUsed;

    public Round(double deltaPlayerHealth, double deltaEnemyHealth, List<ItemEntity> weaponryUsed)
    {
        this.deltaPlayerHealth = deltaPlayerHealth;
        this.deltaEnemyHealth = deltaEnemyHealth;
        this.weaponryUsed = weaponryUsed;
    }

    public RoundResponse getResponse() {
        List<ItemResponse> itemResponses = weaponryUsed.stream()
                .map(ItemEntity::getItemResponse)
                .collect(Collectors.toList());
        return new RoundResponse(deltaPlayerHealth, deltaEnemyHealth, itemResponses);
    }

    @Override
    public String toString() {
        return "Rounds{" +
                "deltaPlayerHealth=" + deltaPlayerHealth +
                ", deltaEnemyHealth=" + deltaEnemyHealth +
                ", weaponryUsed=" + weaponryUsed +
                '}';
    }
    
}
