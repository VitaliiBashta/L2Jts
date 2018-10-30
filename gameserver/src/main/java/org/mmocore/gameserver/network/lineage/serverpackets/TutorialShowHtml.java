package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class TutorialShowHtml extends GameServerPacket {

    /**
     * <html><head><body><center>
     * <font color="LEVEL">Quest</font>
     * </center>
     * <br>
     * Speak to the <font color="LEVEL"> Paagrio Priests </font>
     * of the Temple of Paagrio. They will explain the basics of combat through quests.
     * <br>
     * You must visit them, for they will give you a useful gift after you complete a quest.
     * <br>
     * They are marked in yellow on the radar, at the upper-right corner of the screen.
     * You must visit them if you wish to advance.
     * <br>
     * <a action="link tutorial_close_0">Close Window</a>
     * </body></html>
     * <p/>
     * ВНИМАНИЕ!!! Клиент отсылает назад action!!! Используется как БАЙПАСС В RequestTutorialLinkHtml!!!
     */
    private final String html;

    public TutorialShowHtml(final String html) {
        this.html = html;
    }

    @Override
    protected final void writeData() {
        writeS(html);
    }
}