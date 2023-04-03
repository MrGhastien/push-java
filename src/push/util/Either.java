package push.util;

import java.util.function.Consumer;

/**
 * Wraps an object that can be one of two types.
 * @param <L> The first possible type
 * @param <R> The second possible type
 */
public interface Either<L, R> {

    /**
     * Returns the contained object if its type is <code>L</code>. 
     * @throws UnsupportedOperationException if the type of the wrapped object is not <code>L</code>.
     */
    L getLeft();

    /**
     * Returns the contained object if its type is <code>R</code>. 
     * @throws UnsupportedOperationException if the type of the wrapped object is not <code>R</code>.
     */
    R getRight();

    /**
     * Runs the consumer 
     */
    void match(Consumer<L> caseL, Consumer<R> caseR);

    void ifLeft(Consumer<L> consumer);
    void ifRight(Consumer<R> consumer);

    boolean isLeft();
    boolean isRight();

    static <L,R> Either<L,R> left(L obj) {
        return new Left<>(obj);
    }

    static <L,R> Either<L,R> right(R obj) {
        return new Right<>(obj);
    }

    class Left<L,R> implements Either<L,R> {

        private L obj;

        private Left(L obj) {
            this.obj = obj;
        }

        @Override
        public L getLeft() {
            return obj;
        }

        @Override
        public R getRight() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void match(Consumer<L> caseL, Consumer<R> caseR) {
            caseL.accept(obj);
        }

        @Override
        public void ifLeft(Consumer<L> consumer) {
            consumer.accept(obj);
        }

        @Override
        public void ifRight(Consumer<R> consumer) {
            return;
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }
    }

    class Right<L,R> implements Either<L,R> {

        private R obj;

        private Right(R obj) {
            this.obj = obj;
        }

        @Override
        public L getLeft() {
            throw new UnsupportedOperationException();
        }

        @Override
        public R getRight() {
            return obj;
        }

        @Override
        public void match(Consumer<L> caseL, Consumer<R> caseR) {
            caseR.accept(obj);
        }

        @Override
        public void ifLeft(Consumer<L> consumer) {
            return;
        }

        @Override
        public void ifRight(Consumer<R> consumer) {
            consumer.accept(obj);
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }
    }
}
