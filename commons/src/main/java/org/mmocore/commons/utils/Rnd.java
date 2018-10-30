package org.mmocore.commons.utils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Rnd {
    private Rnd() {
    }

    public static double get() // get random number from 0 to 1
    {
        return ThreadLocalRandom.current().nextDouble();
    }

    /**
     * Gets a random number from 0(inclusive) to n(exclusive)
     *
     * @param n The superior limit (exclusive)
     * @return A number from 0 to n-1
     */
    public static int get(final int n) {
        return ThreadLocalRandom.current().nextInt(n);
    }

    public static long get(final long n) {
        return ThreadLocalRandom.current().nextLong(n);
    }

    public static double get(final double n) {
        return ThreadLocalRandom.current().nextDouble(n);
    }

    public static int get(final int min, final int max) // get random number from min to max (not max-1 !)
    {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static long get(final long min, final long max) // get random number from min to max (not max-1 !)
    {
        return ThreadLocalRandom.current().nextLong(min, max + 1);
    }

    public static double get(final double min, final double max) // get random number from min to max (not max-1 !)
    {
        return ThreadLocalRandom.current().nextDouble(min, max + 1);
    }

    public static int nextInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    public static double nextDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    public static double nextGaussian() {
        return ThreadLocalRandom.current().nextGaussian();
    }

    public static boolean nextBoolean() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    /**
     * Рандомайзер для подсчета шансов.<br>
     * Рекомендуется к использованию вместо Rnd.get()
     *
     * @param chance в процентах от 0 до 100
     * @return true в случае успешного выпадания.
     * <li>Если chance <= 0, вернет false
     * <li>Если chance >= 100, вернет true
     */
    public static boolean chance(final int chance) {
        return chance >= 1 && (chance > 99 || ThreadLocalRandom.current().nextInt(99) + 1 <= chance);
    }

    /**
     * Рандомайзер для подсчета шансов.<br>
     * Рекомендуется к использованию вместо Rnd.get() если нужны очень маленькие шансы
     *
     * @param chance в процентах от 0 до 100
     * @return true в случае успешного выпадания.
     * <li>Если chance <= 0, вернет false
     * <li>Если chance >= 100, вернет true
     */
    public static boolean chance(final double chance) {
        return ThreadLocalRandom.current().nextDouble() <= chance / 100.;
    }

    public static <E> E get(final E[] list) {
        return list[get(list.length)];
    }

    public static int get(final int[] list) {
        return list[get(list.length)];
    }

    public static <E> E get(final List<E> list) {
        return list.get(get(list.size()));
    }
}