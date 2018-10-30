package org.mmocore.gameserver.data.xml.holder.custom;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.templates.custom.premium.PremiumPackage;
import org.mmocore.gameserver.templates.custom.premium.PremiumPersonalSetting;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Mangol
 * @since 26.03.2016
 */
public class PremiumHolder extends AbstractHolder {
    private static final PremiumHolder INSTANCE = new PremiumHolder();
    private final Map<Integer, PremiumPackage> premiumPackages = new HashMap<>();
    private PremiumPersonalSetting personalSetting;

    public static PremiumHolder getInstance() {
        return INSTANCE;
    }

    public void addPremiumPackage(final PremiumPackage premiumPackage) {
        premiumPackages.putIfAbsent(premiumPackage.getId(), premiumPackage);
    }

    public Optional<PremiumPackage> getPremiumPackage(final int id) {
        return Optional.ofNullable(premiumPackages.get(id));
    }

    public Map<Integer, PremiumPackage> getPremiumPackages() {
        return premiumPackages;
    }

    public PremiumPersonalSetting getPersonalSetting() {
        return personalSetting;
    }

    public void setPersonalSetting(final PremiumPersonalSetting personalSetting) {
        this.personalSetting = personalSetting;
    }

    @Override
    public void clear() {
        premiumPackages.clear();
        personalSetting = null;
    }

    @Override
    public int size() {
        return premiumPackages.size();
    }
}
