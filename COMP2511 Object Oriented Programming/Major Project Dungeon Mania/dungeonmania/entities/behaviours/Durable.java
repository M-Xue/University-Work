package dungeonmania.entities.behaviours;

public interface Durable {
    int getDurability();

    void setDurability(int durability);

    default void incrementDurability(int value) {
        setDurability(getDurability() + value);
    }
}
