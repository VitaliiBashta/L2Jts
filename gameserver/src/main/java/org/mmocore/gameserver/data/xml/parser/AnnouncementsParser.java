package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.AnnouncementsHolder;
import org.mmocore.gameserver.model.Announcement;

import java.io.File;

/**
 * @author Java-man
 */
public class AnnouncementsParser extends AbstractFileParser<AnnouncementsHolder> {
    private static final AnnouncementsParser INSTANCE = new AnnouncementsParser();

    protected AnnouncementsParser() {
        super(AnnouncementsHolder.getInstance());
    }

    public static AnnouncementsParser getInstance() {
        return INSTANCE;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/announcements.xml");
    }

    @Override
    public String getDTDFileName() {
        return "xmlscript/dtd/announcements.dtd";
    }

    @Override
    protected void readData(final AnnouncementsHolder holder, final Element rootElement) throws Exception {
        int initialDelay, delay, limit;
        String message;
        boolean critical, auto;

        for (final Element announcementElement : rootElement.getChildren("announcement")) {
            message = announcementElement.getAttributeValue("message");
            critical = Boolean.parseBoolean(announcementElement.getAttributeValue("critical"));
            auto = Boolean.parseBoolean(announcementElement.getAttributeValue("auto"));
            initialDelay = Integer.parseInt(announcementElement.getAttributeValue("initial_delay"));
            delay = Integer.parseInt(announcementElement.getAttributeValue("delay"));
            limit = Integer.parseInt(announcementElement.getAttributeValue("limit"));
            if (auto && limit < -1) {
                limit = -1;
            }
            if (auto && delay < 30) {
                _log.warn("Announcement \"{}\" is using unrealistic delay \"{}\". Ignoring it!", message, delay);
                continue;
            }
            if (message == null) {
                _log.warn("Announcement is empty. Ignoring it!");
                continue;
            }

            if (auto) {
                holder.addAutoAnnouncement(new Announcement(message, critical, auto, initialDelay, delay, limit));
            } else {
                holder.addShownOnLoginAnnouncement(new Announcement(message, critical, auto, initialDelay, delay, limit));
            }
            holder.addFullAnnouncement(new Announcement(message, critical, auto, initialDelay, delay, limit));
        }
    }
}