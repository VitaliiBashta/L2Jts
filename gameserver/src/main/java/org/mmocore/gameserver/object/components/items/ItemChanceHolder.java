package org.mmocore.gameserver.object.components.items;

/**
 * @author Java-man
 */
public class ItemChanceHolder extends ItemHolder {
    private final int chance;

    public ItemChanceHolder(final int itemId, final long count, final int chance) {
        super(itemId, count);
        this.chance = chance;
    }

    public int getChance() {
        return chance;
    }
}
