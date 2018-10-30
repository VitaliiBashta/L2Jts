package org.mmocore.gameserver.scripts.services.autoenchant;

import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.object.components.items.ItemInstance;

/**
 * Created by Hack
 * Date: 07.06.2016 1:55
 */
public class EnchantParams {
    // enchant serivce
    public ItemInstance targetItem;
    public ItemInstance upgradeItem;
    public boolean isUseCommonScrollWhenSafe = true;
    public int upgradeItemLimit = ServicesConfig.enchantServiceDefaultLimit;
    public int maxEnchant = ServicesConfig.enchantServiceDefaultEnchant;
    public int maxEnchantAtt = ServicesConfig.enchantServiceDefaultAttribute;
    public boolean isChangingUpgradeItemLimit = false;
    public boolean isChangingMaxEnchant = false;

    // enchant reuse time
    public long lastEnchant;
    public long lastAbuse;
}
