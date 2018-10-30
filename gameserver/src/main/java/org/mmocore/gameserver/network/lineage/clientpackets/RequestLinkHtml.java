package org.mmocore.gameserver.network.lineage.clientpackets;

import org.apache.commons.io.FileUtils;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.manager.GmManager;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;

/**
 * @author n0nam3
 * @date 22/08/2010 15:16
 */
public class RequestLinkHtml extends L2GameClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLinkHtml.class);

    //Format: cS
    private String link;

    @Override
    protected void readImpl() {
        link = readS();
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null) {
            return;
        }

        if (link.contains("..") || !link.endsWith(".htm")) {
            player.sendDebugMessage("Hmmm...It's not possible, man. PTS shell not working. Sayed GM.");
            GmManager.broadcastMessageToGMs("Character " + player.getName() + "(" + player.getObjectId() + "): used Hlapex or another packet sender and request HTML in [RequestLinkHtml] packet. Ban him!");
            LOGGER.warn("[RequestLinkHtml] hack? link contains prohibited characters: '" + link + "', skipped");
            return;
        }
        final String lang = player.isLangRus() ? "ru" : "en";
        final File file = new File(ServerConfig.DATAPACK_ROOT, "data/html-" + lang + "/pts/");
        final Collection<File> files = FileUtils.listFiles(file, new String[]{".htm"}, true);
        if (files == null) {
            return;
        }
        files.stream().filter(f -> !f.isDirectory() && f.getName() == link).forEach(f -> {
            link = f.getAbsolutePath();
        });
        final HtmlMessage msg = new HtmlMessage(5);
        msg.setFile(link);
        player.sendPacket(msg);
    }
}