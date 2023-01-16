package dungeonmania.entities.player;

import dungeonmania.entities.ItemEntity;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Inventory implements Cloneable {
    private HashMap<Integer, ItemEntity> items = new HashMap<>();

    /**
     * Add an item to the inventory.
     * @param itemEntity The item to be added.
     */
    public void addItem(ItemEntity itemEntity) {
        items.put(itemEntity.getId(), itemEntity);
    }

    /**
     * Get the item with the given id.
     * @param itemId The id of the item.
     * @return The item object.
     */
    public ItemEntity getItem(int itemId) {
        return items.get(itemId);
    }

    /**
     * Get the first item of a given class in the inventory.
     * @param itemClass The class of the item.
     * @return The first item of the given class.
     */
    public <T> T getFirstItem(Class<T> itemClass) {
        return items.values().stream()
                .filter(itemClass::isInstance)
                .findFirst()
                .map(itemClass::cast)
                .orElse(null);
    }

    /**
     * Get all items of a given class in the inventory.
     * @param itemClass The class of items.
     * @return A List of items of the given class.
     */
    public <T> List<T> getItemsByClass(Class<T> itemClass) {
        return items.values().stream()
                .filter(itemClass::isInstance)
                .map(itemClass::cast)
                .collect(Collectors.toList());
    }

    /**
     * Remove the item with the given id.
     * @param itemId The id of the item.
     */
    public void removeItem(int itemId) {
        items.remove(itemId);
    }

    /**
     * Remove an item from inventory.
     * @param item The item object.
     */
    public void removeItem(ItemEntity item) {
        items.remove(item.getId());
    }

    /**
     * Removes a given amount of items of a given type.
     * @param itemClass The class of items to be removed.
     * @param amount The amount of items to be removed.
     */
    public void removeItem(Class<?> itemClass, int amount) {
        for (int i = 0; i < amount; i++) {
            ItemEntity item = (ItemEntity) getFirstItem(itemClass);
            if (item == null)
                break;
            removeItem(item);
        }
    }
    
    public boolean hasItem(Class<?> itemClass) {
        return items.values().stream().anyMatch(itemClass::isInstance);
    }

    /**
     * Get the number of items of the given class.
     * @param itemClass The class of items.
     * @return The count of items.
     */
    public int countItems(Class<?> itemClass) {
        int count = 0;
        for (ItemEntity item : items.values()) {
            if (itemClass.isInstance(item)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get a list of ItemResponse objects for all items in this player's inventory.
     * @return The list of ItemResponse objects.
     */
    public List<ItemResponse> getResponse() {
        List<ItemResponse> responses = new ArrayList<>();

        for (ItemEntity item : items.values()) {
            responses.add(item.getItemResponse());
        }

        return responses;
    }

    public List<Pair<Class<? extends ItemEntity>, Integer>> checkAvailableMaterials(
            List<List<Pair<Class<? extends ItemEntity>, Integer>>> materials
    ) {
        boolean hasAllMaterials = true;
        List<Pair<Class<? extends ItemEntity>, Integer>> materialsToConsumed = new ArrayList<>();

        for (List<Pair<Class<? extends ItemEntity>, Integer>> material : materials) {
            boolean hasMaterial = false;
            for (Pair<Class<? extends ItemEntity>, Integer> materialCandidate : material) {
                if (countItems(materialCandidate.x) >= materialCandidate.y) {
                    hasMaterial = true;
                    materialsToConsumed.add(materialCandidate);
                    break;
                }
            }

            if (!hasMaterial) {
                hasAllMaterials = false;
                break;
            }
        }

        if (hasAllMaterials) {
            return materialsToConsumed;
        } else {
            return null;
        }
    }

    @Override
    public Inventory clone() {
        try {
            HashMap<Integer, ItemEntity> clonedItems = new HashMap<>();
            for (Map.Entry<Integer, ItemEntity> itemEntry : this.items.entrySet()) {
                clonedItems.put(itemEntry.getKey(), (ItemEntity) itemEntry.getValue().clone());
            }
            Inventory clone = (Inventory) super.clone();
            clone.items = clonedItems;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
