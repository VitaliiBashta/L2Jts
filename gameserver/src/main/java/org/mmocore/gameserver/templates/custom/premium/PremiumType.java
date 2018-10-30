package org.mmocore.gameserver.templates.custom.premium;

import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;

/**
 * @author Mangol
 * @since 26.03.2016
 */
public enum PremiumType {
    xp("premium.xpName"),
    sp("premium.spName"),
    adena("premium.adenaName"),
    drop("premium.dropName"),
    spoil("premium.spoilName"),
    epaulette("premium.epauletteName"),
    enchantChance("premium.enchantChance"),
    attributeChance("premium.attributeChance"),
    craftChance("premium.craftChance");

    private final String name;

    PremiumType(String name) {
        this.name = name;
    }

    public String getName(final Player player) {
        return new CustomMessage(name).toString(player);
    }
}
