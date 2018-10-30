package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.holder.enchantoption.EnchantOption;
import org.mmocore.commons.data.AbstractHolder;

import java.util.List;

/**
 * @author : Camelion
 * @date : 27.08.12 2:01
 */
public class EnchantOptionHolder extends AbstractHolder {
    private static EnchantOptionHolder ourInstance = new EnchantOptionHolder();
    @Element(start = "enchant_option_begin", end = "enchant_option_end")
    public List<EnchantOption> enchantOptions;

    private EnchantOptionHolder() {
    }

    public static EnchantOptionHolder getInstance() {
        return ourInstance;
    }

    @Override
    public int size() {
        return enchantOptions.size();
    }

    public List<EnchantOption> getEnchantOptions() {
        return enchantOptions;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }
}