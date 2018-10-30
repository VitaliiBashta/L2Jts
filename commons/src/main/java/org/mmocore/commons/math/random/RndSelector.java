package org.mmocore.commons.math.random;

import org.mmocore.commons.utils.Rnd;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public final class RndSelector<E> {
    private final int totalWeight;
    private final RndNode<E>[] nodes;
    @SafeVarargs
    private RndSelector(final RndNode<E>... rndNodes) {
        totalWeight = Stream.of(rndNodes).mapToInt(RndNode::getWeight).sum();
        nodes = rndNodes;
    }

    @SafeVarargs
    public static <T> RndSelector<T> of(final RndNode<T>... rndNodes) {
        Arrays.sort(rndNodes);
        return new RndSelector<>(rndNodes);
    }

    /**
     * Вернет один из елементов или null, null возможен только если сумма весов всех элементов меньше maxWeight
     */
    public E chance(final int maxWeight) {
        if (maxWeight <= 0)
            return null;

        final int r = Rnd.get(maxWeight);
        int weight = 0;
        for (final RndNode<E> node : nodes) {
            weight += node.getWeight();
            if (weight > r)
                return node.getValue();
        }

        return null;
    }

    /**
     * Вернет один из елементов
     */
    public E select() {
        return chance(totalWeight);
    }

    public static class RndNode<T> implements Comparable<RndNode<T>> {
        private final T value;
        private final int weight;

        private RndNode(final T value, final int weight) {
            this.value = value;
            this.weight = weight;
        }

        public static <T> RndNode<T> of(final T value, final int weight) {
            return new RndNode<>(value, weight);
        }

        public T getValue() {
            return value;
        }

        public int getWeight() {
            return weight;
        }

        @Override
        public int compareTo(final RndNode<T> o) {
            return Integer.compare(weight, o.getWeight());
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            final RndNode<T> rndNode = (RndNode<T>) o;
            return weight == rndNode.weight;
        }

        @Override
        public int hashCode() {
            return Objects.hash(weight);
        }
    }
}