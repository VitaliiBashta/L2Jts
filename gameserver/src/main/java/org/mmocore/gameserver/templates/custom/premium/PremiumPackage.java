package org.mmocore.gameserver.templates.custom.premium;

import org.mmocore.gameserver.object.Player;
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
public class PremiumPackage implements IPremium {
    private final Map<Integer, PremiumTime> times = new HashMap<>();
    private final Map<PremiumType, PremiumRates> premiumRates = new HashMap<>();
    private int id;
    private String icon;
    private String nameRU;
    private String nameEN;
    private boolean show;

    public PremiumPackage(int id, String icon, String nameRU, String nameEN, boolean show) {
        this.id = id;
        this.icon = icon;
        this.nameRU = nameRU;
        this.nameEN = nameEN;
        this.show = show;
    }

    public void addRate(final PremiumRates rates) {
        premiumRates.putIfAbsent(rates.getType(), rates);
    }

    public Optional<PremiumRates> getRate(final PremiumType premiumType) {
        return Optional.ofNullable(premiumRates.get(premiumType));
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

    public boolean isShow() {
        return show;
    }

    public int getId() {
        return id;
    }

    public String getIcon() {
        return icon;
    }

    public String getNameRU() {
        return nameRU;
    }

    public String getNameEN() {
        return nameEN;
    }

    public String getName(final Player player) {
        switch (player.getLanguage()) {
            case RUSSIAN:
                return getNameRU();
            case ENGLISH:
                return getNameEN();
            default:
                return "Not name";
        }
    }

    public Map<PremiumType, PremiumRates> getPremiumRates() {
        return premiumRates;
    }
}
