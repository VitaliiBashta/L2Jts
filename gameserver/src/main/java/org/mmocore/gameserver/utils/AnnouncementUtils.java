package org.mmocore.gameserver.utils;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.Format.TextMode;
import org.jdom2.output.XMLOutputter;
import org.mmocore.commons.data.xml.helpers.SimpleDTDEntityResolver;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.StringHolder;
import org.mmocore.gameserver.data.xml.holder.AnnouncementsHolder;
import org.mmocore.gameserver.data.xml.parser.AnnouncementsParser;
import org.mmocore.gameserver.model.Announcement;
import org.mmocore.gameserver.model.AutoAnnouncement;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.Say2;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.io.*;
import java.util.Iterator;

public class AnnouncementUtils {
    public static void autoAnnouncementsLaunch() {
        AnnouncementsHolder.getInstance().getAutoAnnouncements().forEach(AutoAnnouncement::createTask);
    }

    public static void showAnnouncements(final Player activeChar) {
        AnnouncementsHolder.getInstance().getShownOnLoginAnnouncements().forEach(announce -> showAnnounce(announce, activeChar));
    }

    public static void announceToAll(final Announcement announcement) {
        announceToAll(announcement.getMessage(), announcement.isCritical() ? ChatType.CRITICAL_ANNOUNCE : ChatType.ANNOUNCEMENT);
    }

    public static void announceToAll(final String text) {
        announceToAll(text, ChatType.ANNOUNCEMENT);
    }

    public static void announceToPlayer(final String text, Player player) {
        announceToPlayer(player, text, ChatType.ANNOUNCEMENT);
    }

    public static void shout(final Player activeChar, final String text, final ChatType type) {
        final Say2 cs = new Say2(activeChar.getObjectId(), type, activeChar.getName(), text, null);
        ChatUtils.shout(activeChar, cs);
        activeChar.sendPacket(cs);
    }

    public static void announceToAll(final String text, final ChatType type) {
        final Say2 cs = new Say2(0, type, "", text, null);
        for (final Player player : GameObjectsStorage.getPlayers()) {
            player.sendPacket(cs);
        }
    }

    public static void announceToAll(final CustomMessage message, final ChatType type) {
        for (final Player player : GameObjectsStorage.getPlayers()) {
            final Say2 cs = new Say2(0, type, "", message.toString(player), null);
            player.sendPacket(cs);
        }
    }

    public static void announceWithSplash(String ruText, String enText) {
        Say2 ruMsg = new Say2(0, ChatType.ANNOUNCEMENT, "", ruText, null);
        Say2 enMsg = new Say2(0, ChatType.ANNOUNCEMENT, "", enText, null);
        ExShowScreenMessage ruSplash = new ExShowScreenMessage(ruText, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
        ExShowScreenMessage enSplash = new ExShowScreenMessage(enText, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
        for (Player player : GameObjectsStorage.getPlayers()) {
            player.sendPacket(player.isLangRus() ? ruMsg : enMsg);
            player.sendPacket(player.isLangRus() ? ruSplash : enSplash);
        }
    }

    public static void announceBossDeath(NpcInstance boss) {
        if (boss == null)
            return;
        if (boss.getAggroList() != null && boss.getAggroList().getTopDamager() != null) {
            Player dropOwner = boss.getAggroList().getTopDamager().getPlayer();
            if (dropOwner != null && dropOwner.getClan() != null)
                AnnouncementUtils.announceWithSplash(boss.getName() + " был сражен кланом " + dropOwner.getClan() + "!",
                        boss.getName() + " has been defeated by clan " + dropOwner.getClan() + "!");
        } else {
            AnnouncementUtils.announceWithSplash(boss.getName() + " был сражен!", boss.getName() + " has been defeated!");
        }
    }

    public static void announceMultilang(String ruText, String enText) {
        Say2 ruMsg = new Say2(0, ChatType.ANNOUNCEMENT, "", ruText, null);
        Say2 enMsg = new Say2(0, ChatType.ANNOUNCEMENT, "", enText, null);
        for (Player player : GameObjectsStorage.getPlayers())
            player.sendPacket(player.isLangRus() ? ruMsg : enMsg);
    }

    public static void splashToPlayer(Player player, String ruText, String enText) {
        ExShowScreenMessage ruSplash = new ExShowScreenMessage(ruText, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
        ExShowScreenMessage enSplash = new ExShowScreenMessage(enText, 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false);
        player.sendPacket(player.isLangRus() ? ruSplash : enSplash);
    }

    public static void announceToPlayer(Player player, final String text, final ChatType type) {
        if (player == null)
            return;

        final Say2 cs = new Say2(0, type, "", text, null);
        player.sendPacket(cs);
    }

    public static void announceToPlayerFromStringHolder(final String add, Player player, final Object... arg) {
        player.sendPacket(new Say2(0, ChatType.ANNOUNCEMENT, "", String.format(StringHolder.getInstance().getString(add, player), arg), null));
    }

    public static void announceToAllFromStringHolder(final String add, final Object... arg) {
        for (final Player player : GameObjectsStorage.getPlayers()) {
            player.sendPacket(new Say2(0, ChatType.ANNOUNCEMENT, "", String.format(StringHolder.getInstance().getString(add, player), arg), null));
        }
    }

    public static void announceToAll(final CustomMessage cm) {
        for (final Player player : GameObjectsStorage.getPlayers()) {
            player.sendPacket(cm);
        }
    }

    public static void announceToAll(final IBroadcastPacket sm) {
        for (final Player player : GameObjectsStorage.getPlayers()) {
            player.sendPacket(sm);
        }
    }

    public static void showAnnounce(final Announcement announce, final Player player) {
        final Say2 cs = new Say2(0, ChatType.ANNOUNCEMENT, player.getName(), announce.getMessage(), null);
        player.sendPacket(cs);
    }

    public static void addAnnouncement(final String message, final boolean critical, final boolean auto, final int initial_delay, final int delay, final int limit) {
        try {
            final File file_xml = new File(ServerConfig.DATAPACK_ROOT, "data/announcements.xml");
            final File file_dtd = new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/dtd/announcements.dtd");
            final String fName = file_xml.getAbsolutePath();
            final SAXBuilder parser = new SAXBuilder();
            parser.setEntityResolver(new SimpleDTDEntityResolver(file_dtd));
            final Document doc = parser.build(file_xml);
            final Element temp = doc.getRootElement();
            final Element announcement = new Element("announcement");
            announcement.setAttribute("message", message);
            announcement.setAttribute("critical", String.valueOf(critical));
            announcement.setAttribute("auto", delay >= 30 ? String.valueOf(auto) : "false");
            announcement.setAttribute("initial_delay", auto ? (initial_delay > 0 ? String.valueOf(initial_delay) : "-1") : "-1");
            announcement.setAttribute("delay", auto ? (delay >= 30 ? String.valueOf(delay) : "-1") : "-1");
            announcement.setAttribute("limit", auto ? (limit > 0 ? String.valueOf(limit) : "-1") : "-1");
            temp.addContent(announcement);
            XMLOutputter writer;
            Format format;
            try {
                format = Format.getRawFormat();
                format.setIndent("\t");
                format.setTextMode(TextMode.TRIM);
                writer = new XMLOutputter();
                writer.setFormat(format);
                writer.output(doc, new OutputStreamWriter(new FileOutputStream(fName), "UTF8"));
                AnnouncementsParser.getInstance().reload();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAnnouncement(final int line) {
        try {
            final File file_xml = new File(ServerConfig.DATAPACK_ROOT, "data/announcements.xml");
            final File file_dtd = new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/dtd/announcements.dtd");
            final String fName = file_xml.getAbsolutePath();
            final SAXBuilder parser = new SAXBuilder();
            parser.setEntityResolver(new SimpleDTDEntityResolver(file_dtd));
            final Document doc = parser.build(file_xml);
            final Iterator<Element> cities = doc.getRootElement().getChildren().iterator();
            int i = -1;
            while (cities.hasNext()) {
                i++;
                cities.next();
                if (i == line) {
                    cities.remove();
                    break;
                }
            }
            XMLOutputter writer;
            Format format;
            try {
                format = Format.getRawFormat();
                format.setIndent("\t");
                format.setTextMode(TextMode.TRIM);
                writer = new XMLOutputter();
                writer.setFormat(format);
                writer.output(doc, new OutputStreamWriter(new FileOutputStream(fName), "UTF8"));
                AnnouncementsParser.getInstance().reload();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (IOException | JDOMException e) {
            e.printStackTrace();
        }
    }
}
