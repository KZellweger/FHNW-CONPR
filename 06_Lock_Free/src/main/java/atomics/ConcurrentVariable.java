package atomics;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public interface ConcurrentVariable<A> {
    A get();

    void set(A a);

    void update(Function<A, A> f);
}


final class AtomicVariable<A> implements ConcurrentVariable<A> {
    private final AtomicReference<A> reference = new AtomicReference<>();

    @Override
    public A get() {
        return reference.get();
    }

    @Override
    public void set(A a) {
        reference.set(a);
    }

    @Override
    public void update(Function<A, A> f) {
        while (true) {
            A oldValue = reference.get();
            A newValue = f.apply(oldValue);
            if (reference.compareAndSet(oldValue, newValue)) {
                break;
            }
        }
    }
}

final class LockVariable<A> implements ConcurrentVariable<A> {
    private volatile A value;
    private final Object lock = new Object();

    @Override
    public A get() {
        return value;
    }

    @Override
    public void set(A a) {
        synchronized (lock) {
            value = a;
        }
    }

    @Override
    public void update(Function<A, A> f) {
        synchronized (lock) {
            f.apply(value);
        }
    }
}

final class AsyncVariable<A> implements ConcurrentVariable<A> {

    //TODO: Implementation based on command queue and worker thread
    @Override
    public A get() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(A a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Function<A, A> f) {
        throw new UnsupportedOperationException();
    }
}