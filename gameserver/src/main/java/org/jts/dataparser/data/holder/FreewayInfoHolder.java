package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.holder.freeway.Freeway;
import org.mmocore.commons.data.AbstractHolder;

import java.util.List;

/**
 * @author : Camelion
 * @date : 27.08.12 13:14
 */
public class FreewayInfoHolder extends AbstractHolder {
    private static final FreewayInfoHolder ourInstance = new FreewayInfoHolder();
    @Element(start = "freeway_begin", end = "freeway_end")
    private List<Freeway> freeways;

    private FreewayInfoHolder() {
    }

    public static FreewayInfoHolder getInstance() {
        return ourInstance;
    }

    @Override
    public int size() {
        return freeways.size();
    }

    public List<Freeway> getFreeways() {
        return freeways;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }
}