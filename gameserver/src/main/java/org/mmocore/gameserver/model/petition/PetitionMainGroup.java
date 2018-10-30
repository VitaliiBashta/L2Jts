package org.mmocore.gameserver.model.petition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author VISTALL
 * @date 7:28/25.07.2011
 */
public class PetitionMainGroup extends PetitionGroup {
    private final Map<Integer, PetitionSubGroup> _subGroups = new HashMap<>();

    public PetitionMainGroup(final int id) {
        super(id);
    }

    public void addSubGroup(final PetitionSubGroup subGroup) {
        _subGroups.put(subGroup.getId(), subGroup);
    }

    public PetitionSubGroup getSubGroup(final int val) {
        return _subGroups.get(val);
    }

    public Collection<PetitionSubGroup> getSubGroups() {
        return _subGroups.values();
    }
}
