package org.mmocore.gameserver.model.petition;


import org.mmocore.gameserver.utils.Language;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author VISTALL
 * @date 7:32/25.07.2011
 */
public abstract class PetitionGroup {
    private final Map<Language, String> name = new EnumMap<>(Language.class);
    private final Map<Language, String> description = new EnumMap<>(Language.class);

    private final int _id;

    public PetitionGroup(final int id) {
        _id = id;
    }

    public int getId() {
        return _id;
    }

    public String getName(final Language lang) {
        return name.get(lang);
    }

    public void setName(final Language lang, final String name) {
        this.name.put(lang, name);
    }

    public String getDescription(final Language lang) {
        return description.get(lang);
    }

    public void setDescription(final Language lang, final String name) {
        description.put(lang, name);
    }
}
