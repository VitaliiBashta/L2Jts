package org.mmocore.gameserver.phantoms.template;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hack
 * Date: 23.08.2016 0:48
 */
public class PhantomArmorTemplate {
    private final List<Integer> itemIds = new ArrayList<>();

    public void addId(int id) {
        itemIds.add(id);
    }

    public List<Integer> getIds() {
        return itemIds;
    }
}
