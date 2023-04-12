package push.util;

import java.util.function.Supplier;

/**
 * Encapsulates an object available by running a given function.
 *
 * <p>This is used for commands with command substitution as arguments.
 * Some arguments might be plain text (so already available), whereas others are the output of another command.
 * Those commands need to be run first to get the final arguments of the command.</p>
 *
 * @implNote C'est un peu overkill mais au moins c'est généraliste.
 * @param <T> The type of the encapsulated object.
 */
public class Lazy<T> {

    private final Supplier<T> getter;
    private T val;
    private boolean present;

    private Lazy(Supplier<T> getter, T val, boolean lazy) {
        this.getter = getter;
        this.val = val;
        this.present = lazy;
    }

    public T get() {
        if(!isPresent()) {
            val = getter.get();
            present = true;
        }
        return val;
    }

    public boolean isPresent() {
        return present;
    }

    /**
     * Encapsulate an already available object.
     * @param val The object to encapsulate.
     * @return The MaybeLazy wrapper.
     * @param <T> The type of the encapsulated object.
     */
    public static <T> Lazy<T> ofPresent(T val) {
        return new Lazy<>(null, val, true);
    }

    /**
     * Encapsulate an object available through a getter.
     * @param getter The object's getter.
     * @return The MaybeLazy wrapper.
     * @param <T> The type of the encapsulated object.
     */
    public static <T> Lazy<T> of(Supplier<T> getter) {
        return new Lazy<>(getter, null, false);
    }

}
