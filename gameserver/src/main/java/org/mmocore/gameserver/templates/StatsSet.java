package org.mmocore.gameserver.templates;

import org.mmocore.commons.collections.MultiValueSet;

public class StatsSet extends MultiValueSet<String> {
    @SuppressWarnings("serial")
    public static final StatsSet EMPTY = new StatsSet() {
        @Override
        public Object put(final String a, final Object a2) {
            throw new UnsupportedOperationException();
        }
    };
    private static final long serialVersionUID = -2209589233655930756L;

    public StatsSet() {
        super();
    }

    public StatsSet(final StatsSet set) {
        super(set);
    }

    @Override
    public StatsSet clone() {
        return new StatsSet(this);
    }
}