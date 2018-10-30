package org.mmocore.gameserver.templates.client;

import org.mmocore.gameserver.data.StringHolder;
import org.mmocore.gameserver.utils.Language;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author VISTALL
 * @date 20:45/02.09.2011
 */
public class ItemNameLine {
    private final int _itemId;
    private final int _color;
    private final String _augmentName;
    private final String _description;
    private final String _add_name;
    private String _name;
    private Map<String, ItemNameLine> function = null;

    public ItemNameLine(final Language lang, final int itemId, final String name, final String add_name, final String description, final int color) {
        _itemId = itemId;
        _color = color;
        _description = description;
        _add_name = add_name;
        _name = name;
        _augmentName = String.format(StringHolder.getInstance().getString("augmented.s1", lang), name);
    }

    public String getAddName() {
        return _add_name;
    }

    public int getItemId() {
        return _itemId;
    }

    public int getColor() {
        return _color;
    }

    public String getName() {
        return _name;
    }

    public void setName(final String name) {
        _name = name;
    }

    public String getAugmentName() {
        return _augmentName;
    }

    public String getDescription() {
        return _description;
    }

    public void addFunction(final String name, final ItemNameLine template) {
        if (function == null) {
            function = new HashMap<>();
        }
        function.put(name, template);
    }

    public Optional<ItemNameLine> getFunction(final String name) {
        if (function.containsKey(name)) {
            return Optional.of(function.get(name));
        }
        return Optional.empty();
    }
}
