package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.holder.multisell.Multisell;
import org.mmocore.commons.data.AbstractHolder;

import java.util.List;

/**
 * @author : Camelion
 * @date : 30.08.12 14:23
 */
public class MultisellHolder extends AbstractHolder {
    private static final MultisellHolder ourInstance = new MultisellHolder();
    @Element(start = "MultiSell_begin", end = "MultiSell_end")
    private List<Multisell> multisells;

    private MultisellHolder() {
    }

    public static MultisellHolder getInstance() {
        return ourInstance;
    }

    @Override
    public int size() {
        return multisells.size();
    }

    public List<Multisell> getMultisells() {
        return multisells;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }
}