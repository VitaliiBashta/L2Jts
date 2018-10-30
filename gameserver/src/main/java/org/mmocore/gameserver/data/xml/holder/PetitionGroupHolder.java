package org.mmocore.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.model.petition.PetitionMainGroup;

import java.util.Collection;

/**
 * @author VISTALL
 * @date 7:22/25.07.2011
 */
public class PetitionGroupHolder extends AbstractHolder {
    private static final PetitionGroupHolder INSTANCE = new PetitionGroupHolder();

    private final TIntObjectMap<PetitionMainGroup> petitionGroups = new TIntObjectHashMap<>();

    private PetitionGroupHolder() {
    }

    public static PetitionGroupHolder getInstance() {
        return INSTANCE;
    }

    public void addPetitionGroup(final PetitionMainGroup g) {
        petitionGroups.put(g.getId(), g);
    }

    public PetitionMainGroup getPetitionGroup(final int val) {
        return petitionGroups.get(val);
    }

    public Collection<PetitionMainGroup> getPetitionGroups() {
        return petitionGroups.valueCollection();
    }

    @Override
    public int size() {
        return petitionGroups.size();
    }

    @Override
    public void clear() {
        petitionGroups.clear();
    }
}
