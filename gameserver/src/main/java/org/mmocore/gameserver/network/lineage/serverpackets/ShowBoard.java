package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShowBoard extends GameServerPacket {
    private static final String[] DIRECT_BYPASS = {"bypass _bbshome", "bypass _bbsgetfav", "bypass _bbsloc", "bypass _bbsclan", "bypass _bbsmemo", "bypass _maillist_0_1_0_", "bypass _friendlist_0_"};
    private String html;
    private String fav;

    private ShowBoard(final String id, final String html, final String fav) {
        this.html = id + '\u0008';
        if (html != null) {
            this.html += html;
        }
        this.fav = fav;
    }

    private ShowBoard(final String id, final List<String> arg, final String fav) {
        html = id + '\u0008';
        for (final String a : arg) {
            html += a + " \u0008";
        }
    }

    public static void separateAndSend(String html, final Player player) {
        html = html.replace("\t", "");
        final String pathFileCommunity = CBasicConfig.BBS_PATH + "/";
        final Pattern p = Pattern.compile("\\%include\\(([^\\)]+)\\)\\%");
        final Matcher m = p.matcher(html);
        while (m.find()) {
            html = html.replace(m.group(0), HtmCache.getInstance().getHtml(pathFileCommunity + m.group(1), player));
        }
        String fav = "";
        if (player.getSessionVar("add_fav") != null) {
            fav = "bypass _bbsaddfav_List";
        }

        player.getBypassStorage().parseHtml(html, true);

        if (html.length() < 8180) {
            player.sendPacket(new ShowBoard("101", html, fav));
            player.sendPacket(new ShowBoard("102", "", fav));
            player.sendPacket(new ShowBoard("103", "", fav));
        } else if (html.length() < 8180 * 2) {
            player.sendPacket(new ShowBoard("101", html.substring(0, 8180), fav));
            player.sendPacket(new ShowBoard("102", html.substring(8180, html.length()), fav));
            player.sendPacket(new ShowBoard("103", "", fav));
        } else if (html.length() < 8180 * 3) {
            player.sendPacket(new ShowBoard("101", html.substring(0, 8180), fav));
            player.sendPacket(new ShowBoard("102", html.substring(8180, 8180 * 2), fav));
            player.sendPacket(new ShowBoard("103", html.substring(8180 * 2, html.length()), fav));
        } else {
            throw new IllegalArgumentException("Html is too long!");
        }
    }

    public static void separateAndSend(final String html, final List<String> arg, final Player player) {
        String fav = "";
        if (player.getSessionVar("add_fav") != null) {
            fav = "bypass _bbsaddfav_List";
        }

        player.getBypassStorage().parseHtml(html, true);

        if (html.length() < 8180) {
            player.sendPacket(new ShowBoard("1001", html, fav));
            player.sendPacket(new ShowBoard("1002", arg, fav));
        } else {
            throw new IllegalArgumentException("Html is too long!");
        }
    }

    @Override
    protected final void writeData() {
        writeC(0x01); //c4 1 to show community 00 to hide
        for (final String bbsBypass : DIRECT_BYPASS) {
            writeS(bbsBypass);
        }
        writeS(fav);
        writeS(html);
    }
}