package dungeonmania.util;

public class Pair<S, T> {
    public final S x;
    public final T y;

    public Pair(S x, T y) {
        this.x = x;
        this.y = y;
    }

    public static <S, T> Pair<S, T> of(S x, T y) {
        return new Pair<>(x, y);
    }
}
