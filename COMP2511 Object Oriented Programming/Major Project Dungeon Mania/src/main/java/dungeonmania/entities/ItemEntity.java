package dungeonmania.entities;

import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Position;

public abstract class ItemEntity extends Entity {

    public ItemEntity(int id, String type) {
        super(id, type);
    }

    public ItemResponse getItemResponse() {
        return new ItemResponse(getIdString(), getType());
    }

    @Override
    public String toString() {
        return "Equip{" +
                "id='" + getIdString() + '\'' +
                ", type='" + getType() + '\'' +
                '}';
    }


}
