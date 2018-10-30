package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.NpcString;

public class ExShowScreenMessage extends NpcStringContainer {
    public static final int SYSMSG_TYPE = 0;
    public static final int STRING_TYPE = 1;
    private final int type;
    private final int sysMessageId;
    private final boolean big_font;
    private final boolean effect;
    private final ScreenMessageAlign text_align;
    private final int time;
    @Deprecated
    public ExShowScreenMessage(final String text, final int time, final ScreenMessageAlign text_align, final boolean big_font) {
        this(text, time, text_align, big_font, 1, -1, false);
    }

    @Deprecated
    public ExShowScreenMessage(final String text, final int time, final ScreenMessageAlign text_align, final boolean big_font, final int type, final int messageId, final boolean showEffect) {
        super(NpcString.NONE, text);
        this.type = type;
        sysMessageId = messageId;
        this.time = time;
        this.text_align = text_align;
        this.big_font = big_font;
        effect = showEffect;
    }

    public ExShowScreenMessage(final NpcString t, final int time, final ScreenMessageAlign text_align, final String... params) {
        this(t, time, text_align, true, STRING_TYPE, -1, false, params);
    }

    public ExShowScreenMessage(final NpcString npcString, final int time, final ScreenMessageAlign text_align, final boolean big_font, final String... params) {
        this(npcString, time, text_align, big_font, STRING_TYPE, -1, false, params);
    }

    public ExShowScreenMessage(final NpcString npcString, final int time, final ScreenMessageAlign text_align, final boolean big_font, final boolean showEffect, final String... params) {
        this(npcString, time, text_align, big_font, STRING_TYPE, -1, showEffect, params);
    }

    public ExShowScreenMessage(final NpcString npcString, final int time, final ScreenMessageAlign text_align, final boolean big_font, final int type, final int systemMsg, final boolean showEffect, final String... params) {
        super(npcString, params);
        this.type = type;
        sysMessageId = systemMsg;
        this.time = time;
        this.text_align = text_align;
        this.big_font = big_font;
        effect = showEffect;
    }

    @Override
    protected final void writeData() {
        writeD(type); // 0 - system messages, 1 - your defined text
        writeD(sysMessageId); // system message id (_type must be 0 otherwise no effect)
        writeD(text_align.ordinal() + 1); // размещение текста
        writeD(0x00); // ?
        writeD(big_font ? 0 : 1); // размер текста
        writeD(0x00); // ?
        writeD(0x00); // ?
        writeD(effect ? 1 : 0); // upper effect (0 - disabled, 1 enabled) - _position must be 2 (center) otherwise no effect
        writeD(time); // время отображения сообщения в милисекундах
        writeD(0x01); // ?
        writeElements();
    }

    public enum ScreenMessageAlign {
        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT,
        MIDDLE_LEFT,
        MIDDLE_CENTER,
        MIDDLE_RIGHT,
        BOTTOM_CENTER,
        BOTTOM_RIGHT,
    }
}