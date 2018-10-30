package org.jts.dataparser.data.holder.transform;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.annotations.factory.IObjectFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by Mangol on 02.10.2015.
 */
public class TOptionsSex {
    private static final Pattern itemCheckPattern = Pattern.compile("item_check\\s*?=\\s*?\\{(\\w+);([\\S;]+)}");
    private static final Pattern itemNamePattern = Pattern.compile("\\[(\\S+?)]");
    @Element(start = "common_begin", end = "common_end", objectFactory = ItemCheckObjectFactory.class)
    public List<TCommon> common_begin;
    @Element(start = "combat_begin", end = "combat_end")
    public List<TCombat> combat_begin;

    public static class ItemCheckObjectFactory implements IObjectFactory<TCommon> {
        private Class<?> clazz;

        @Override
        public TCommon createObjectFor(final StringBuilder data) throws IllegalAccessException, InstantiationException {
            final TCommon item_check = (TCommon) clazz.newInstance();
            Matcher matcher = itemCheckPattern.matcher(data);
            if (matcher.find()) {
                final String name = matcher.group(1);
                final String item_name_str = matcher.group(2);
                matcher = itemNamePattern.matcher(item_name_str);
                final List<String> list = new ArrayList<>();
                while (matcher.find()) {
                    final String item_name = matcher.group(1);
                    list.add(item_name);
                }
                String[] item_name_arrays = new String[list.size()];
                item_name_arrays = list.toArray(item_name_arrays);
                item_check.item_check = Optional.of(new TItemCheck(name, item_name_arrays));
            }
            return item_check;
        }

        @Override
        public void setFieldClass(Class<?> clazz) {
            this.clazz = clazz;
        }
    }
}
