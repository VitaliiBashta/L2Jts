package org.mmocore.commons.utils.concurrent.atomic;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Атомарный, неперекрываемый флаг состояния.
 *
 * @author G1ta0
 * <p>
 */
public class AtomicState {
    private static final AtomicIntegerFieldUpdater<AtomicState> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(AtomicState.class, "value");

    private volatile int value;

    public AtomicState(final boolean initialValue) {
        value = initialValue ? 1 : 0;
    }

    public AtomicState() {
    }

    public final boolean get() {
        return value != 0;
    }

    private boolean getBool(int value) {
        if (value < 0) {
            //[Hack]: И действительно, давайте экзепшн кидать блять, если пацан уже в данном стейте. Why not? ¯\_(ツ)_/¯
//			throw new IllegalStateException();
            value = 0;
        }
        return value > 0;
    }

    public final boolean setAndGet(final boolean newValue) {
        return newValue ? getBool(stateUpdater.incrementAndGet(this)) : getBool(stateUpdater.decrementAndGet(this));
    }

    public final boolean getAndSet(final boolean newValue) {
        return newValue ? getBool(stateUpdater.getAndIncrement(this)) : getBool(stateUpdater.getAndDecrement(this));
    }

}
