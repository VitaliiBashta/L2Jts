package org.mmocore.gameserver.object.components.items;

/**
 * @author Gnacik
 * @corrected by n0nam3
 */
public class PremiumItem extends ItemHolder {
    private final String sender;

    public PremiumItem(final int itemid, final long count, final String sender) {
        super(itemid, count);
        this.sender = sender;
    }

    public void updateCount(final long newcount) {
        setCount(newcount);
    }

    public String getSender() {
        return sender;
    }
}