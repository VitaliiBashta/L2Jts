package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.interfaces.ShortCut;
import org.mmocore.gameserver.skills.TimeStamp;

/**
 * @author VISTALL
 * @date 7:48/29.03.2011
 */
public abstract class ShortCutPacket extends GameServerPacket {
    public static ShortcutInfo convert(final Player player, final ShortCut shortCut) {
        ShortcutInfo shortcutInfo = null;
        final int page = shortCut.getSlot() + shortCut.getPage() * 12;
        switch (shortCut.getType()) {
            case ShortCut.TYPE_ITEM:
                int variation1Id = 0;
                int variation2Id = 0;
                int reuseGroup = -1, currentReuse = 0, reuse = 0;
                final ItemInstance item = player.getInventory().getItemByObjectId(shortCut.getId());
                if (item != null) {
                    variation1Id = item.getVariation1Id();
                    variation2Id = item.getVariation2Id();
                    reuseGroup = item.getTemplate().getDisplayReuseGroup();
                    if (item.getTemplate().getReuseDelay() > 0) {
                        final TimeStamp timeStamp = player.getSharedGroupReuse(item.getTemplate().getReuseGroup());
                        if (timeStamp != null) {
                            currentReuse = (int) (timeStamp.getReuseCurrent() / 1000L);
                            reuse = (int) (timeStamp.getReuseBasic() / 1000L);
                        }
                    }
                }
                shortcutInfo = new ItemShortcutInfo(shortCut.getType(), page, shortCut.getId(), reuseGroup, currentReuse, reuse, variation1Id, variation2Id,
                        shortCut.getCharacterType());
                break;
            case ShortCut.TYPE_SKILL:
                shortcutInfo = new SkillShortcutInfo(shortCut.getType(), page, shortCut.getId(), shortCut.getLevel(), shortCut.getCharacterType());
                break;
            default:
                shortcutInfo = new ShortcutInfo(shortCut.getType(), page, shortCut.getId(), shortCut.getCharacterType());
                break;
        }
        return shortcutInfo;
    }

    protected static class ItemShortcutInfo extends ShortcutInfo {
        private final int reuseGroup;
        private final int currentReuse;
        private final int basicReuse;
        private int variation1Id;
        private int variation2Id;

        public ItemShortcutInfo(final int type, final int page, final int id, final int reuseGroup, final int currentReuse, final int basicReuse, int variation1Id, int variation2Id, final int characterType) {
            super(type, page, id, characterType);
            this.reuseGroup = reuseGroup;
            this.currentReuse = currentReuse;
            this.basicReuse = basicReuse;
            this.variation1Id = variation1Id;
            this.variation2Id = variation2Id;
        }

        @Override
        protected void write0(final ShortCutPacket p) {
            p.writeD(id);
            p.writeD(characterType);
            p.writeD(reuseGroup);
            p.writeD(currentReuse);
            p.writeD(basicReuse);
            p.writeH(variation1Id);
            p.writeH(variation2Id);
        }
    }

    protected static class SkillShortcutInfo extends ShortcutInfo {
        private final int level;

        public SkillShortcutInfo(final int type, final int page, final int id, final int level, final int characterType) {
            super(type, page, id, characterType);
            this.level = level;
        }

        public int getLevel() {
            return level;
        }

        @Override
        protected void write0(final ShortCutPacket p) {
            p.writeD(id);
            p.writeD(level);
            p.writeC(0x00);
            p.writeD(characterType);
        }
    }

    protected static class ShortcutInfo {
        protected final int type;
        protected final int page;
        protected final int id;
        protected final int characterType;

        public ShortcutInfo(final int type, final int page, final int id, final int characterType) {
            this.type = type;
            this.page = page;
            this.id = id;
            this.characterType = characterType;
        }

        protected void write(final ShortCutPacket p) {
            p.writeD(type);
            p.writeD(page);
            write0(p);
        }

        protected void write0(final ShortCutPacket p) {
            p.writeD(id);
            p.writeD(characterType);
        }
    }
}
