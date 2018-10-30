package org.mmocore.gameserver.templates.custom.premium;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.mmocore.gameserver.templates.custom.premium.component.IPremium;
import org.mmocore.gameserver.templates.custom.premium.component.PremiumRates;
import org.mmocore.gameserver.templates.custom.premium.component.PremiumTime;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Mangol
 * @since 26.03.2016
 */
public final class PremiumPersonalSetting implements IPremium {
    private final Map<Integer, PremiumTime> times = new HashMap<>();
    private final Table<PremiumType, Double, PremiumRates> premiumRates = HashBasedTable.create();

    public PremiumPersonalSetting() {
        registerType();
    }

    private void registerType() {
		/*
		for(final PremiumType type : PremiumType.values())
		{
			premiumRates.put(type, -1, null);
		}
		*/
    }

    @Override
    public void addTime(final PremiumTime time) {
        times.putIfAbsent(time.getId(), time);
    }

    @Override
    public Optional<PremiumTime> getTime(final int id) {
        return Optional.ofNullable(times.get(id));
    }

    @Override
    public Map<Integer, PremiumTime> getTimes() {
        return times;
    }

    /**
     * Добовляет персональные настройки ПА.
     * Очень специфичный затык, т.к. нужно зарегистрировать все типа в методе registerType
     *
     * @param rates
     */
    public void addRate(final PremiumRates rates) {
        premiumRates.put(rates.getType(), rates.getValue(), rates);
    }

    public Optional<PremiumRates> getRate(final PremiumType premiumType, final int id) {
        return Optional.ofNullable(premiumRates.get(premiumType, id));
    }

    public Table<PremiumType, Double, PremiumRates> getRates() {
        return premiumRates;
    }

    public boolean isPremiumContains(final PremiumType type, final double value) {
        return premiumRates.contains(type, value);
    }
}
