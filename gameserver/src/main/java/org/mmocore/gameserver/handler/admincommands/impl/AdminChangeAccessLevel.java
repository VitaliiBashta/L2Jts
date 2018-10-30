package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.configuration.config.gmAccess.GmAccessConfig;
import org.mmocore.gameserver.configuration.parser.gmAccess.GmAccessConfigParser;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.impl.CharacterDAO;
import org.mmocore.gameserver.database.dao.impl.CharacterVariablesDAO;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.mmocore.gameserver.utils.Files;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * changelvl - изменение уровня доступа
 * moders - Панель управления модераторами
 * moders_add - Добавление модератора
 * moders_del - Удаление модератора
 * penalty - Штраф за некорректное модерирование
 */
public class AdminChangeAccessLevel implements IAdminCommandHandler {
    private static final Logger _log = LoggerFactory.getLogger(AdminChangeAccessLevel.class);

    // Панель управления модераторами
    private static void showModersPannel(final Player activeChar) {
        final HtmlMessage reply = new HtmlMessage(5);
        String html = "Moderators managment panel.<br>";

        final File dir = new File(GmAccessConfig.GM_ACCESS_FILES_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            html += "Error: Can't open permissions folder.";
            reply.setHtml(html);
            activeChar.sendPacket(reply);
            return;
        }

        html += "<p align=right>";
        html += "<button width=120 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h admin_moders_add\" value=\"Add modrator\">";
        html += "</p><br>";

        html += "<center><font color=LEVEL>Moderators:</font></center>";
        html += "<table width=285>";
        for (final File f : dir.listFiles()) {
            if (f.isDirectory() || !f.getName().startsWith("m") || !f.getName().endsWith(".xml")) {
                continue;
            }

            // Для файлов модераторов префикс m
            final int oid = Integer.parseInt(f.getName().substring(1, 10));
            String pName = getPlayerNameByObjId(oid);
            boolean on = false;

            if (pName == null || pName.isEmpty()) {
                pName = String.valueOf(oid);
            } else {
                on = GameObjectsStorage.getPlayer(pName) != null;
            }

            html += "<tr>";
            html += "<td width=140>" + pName;
            html += on ? " <font color=\"33CC66\">(on)</font>" : "";
            html += "</td>";
            html += "<td width=45><button width=50 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h admin_moders_log " + oid + "\" value=\"Logs\"></td>";
            html += "<td width=45><button width=20 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h admin_moders_del " + oid + "\" value=\"X\"></td>";
            html += "</tr>";
        }
        html += "</table>";

        reply.setHtml(html);
        activeChar.sendPacket(reply);
    }

    private static String getPlayerNameByObjId(final int oid) {
        String pName = null;
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT `char_name` FROM `characters` WHERE `obj_Id`=\"" + oid + "\" LIMIT 1");
            rset = statement.executeQuery();
            if (rset.next()) {
                pName = rset.getString(1);
            }
        } catch (Exception e) {
            _log.warn("SQL Error: " + e);
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        return pName;
    }

    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanGmEdit) {
            return false;
        }

        switch (command) {
            case admin_changelvl:
                if (wordList.length == 2) {
                    final int lvl = Integer.parseInt(wordList[1]);
                    if (activeChar.getTarget().isPlayer()) {
                        ((Player) activeChar.getTarget()).setAccessLevel(lvl);
                    }
                } else if (wordList.length == 3) {
                    final int lvl = Integer.parseInt(wordList[2]);
                    final Player player = GameObjectsStorage.getPlayer(wordList[1]);
                    if (player != null) {
                        player.setAccessLevel(lvl);
                    }
                }
                break;
            case admin_moders:
                // Панель управления модераторами
                showModersPannel(activeChar);
                break;
            case admin_moders_add:
                if (activeChar.getTarget() == null || !activeChar.getTarget().isPlayer()/* || activeChar.getTarget() == activeChar*/) {
                    activeChar.sendAdminMessage("Incorrect target. Please select a player.");
                    showModersPannel(activeChar);
                    return false;
                }

                final Player modAdd = activeChar.getTarget().getPlayer();
                if (GmAccessConfig.gmlist.containsKey(modAdd.getObjectId())) {
                    activeChar.sendAdminMessage("Error: Moderator " + modAdd.getName() + " already in server access list.");
                    showModersPannel(activeChar);
                    return false;
                }

                // Копируем файл с привилегиями модератора
                final String newFName = "m" + modAdd.getObjectId() + ".xml";
                if (!Files.copyFile(GmAccessConfig.GM_ACCESS_FILES_DIR + "template/moderator.xml", GmAccessConfig.GM_ACCESS_FILES_DIR + newFName)) {
                    activeChar.sendAdminMessage("Error: Failed to copy access-file.");
                    showModersPannel(activeChar);
                    return false;
                }

                // Замена objectId
                String res = "";
                try {
                    final BufferedReader in = new BufferedReader(new FileReader(GmAccessConfig.GM_ACCESS_FILES_DIR + newFName));
                    String str;
                    while ((str = in.readLine()) != null) {
                        res += str + '\n';
                    }
                    in.close();

                    res = res.replaceFirst("ObjIdPlayer", String.valueOf(modAdd.getObjectId()));
                    Files.writeFile(GmAccessConfig.GM_ACCESS_FILES_DIR + newFName, res);
                } catch (Exception e) {
                    activeChar.sendAdminMessage("Error: Failed to modify object ID in access-file.");
                    final File fDel = new File(GmAccessConfig.GM_ACCESS_FILES_DIR + newFName);
                    if (fDel.exists()) {
                        fDel.delete();
                    }
                    showModersPannel(activeChar);
                    return false;
                }

                // Устанавливаем права модератору
                final File af = new File(GmAccessConfig.GM_ACCESS_FILES_DIR + newFName);
                if (!af.exists()) {
                    activeChar.sendAdminMessage("Error: Failed to read access-file for " + modAdd.getName());
                    showModersPannel(activeChar);
                    return false;
                }

                GmAccessConfigParser.loadGMAccess(af);
                modAdd.setPlayerAccess(GmAccessConfig.gmlist.get(modAdd.getObjectId()));

                activeChar.sendAdminMessage("Moderator " + modAdd.getName() + " added.");
                showModersPannel(activeChar);
                break;
            case admin_moders_del:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("Please specify moderator object ID to delete moderator.");
                    showModersPannel(activeChar);
                    return false;
                }

                final int oid = Integer.parseInt(wordList[1]);

                // Удаляем права из серверного списка
                if (GmAccessConfig.gmlist.containsKey(oid)) {
                    GmAccessConfig.gmlist.remove(oid);
                } else {
                    activeChar.sendAdminMessage("Error: Moderator with object ID " + oid + " not found in server access lits.");
                    showModersPannel(activeChar);
                    return false;
                }

                // Если удаляемый модератор онлайн, то отбираем у него права на ходу
                final Player modDel = GameObjectsStorage.getPlayer(oid);
                if (modDel != null) {
                    modDel.setPlayerAccess(null);
                }

                // Удаляем файл с правами
                final String fname = "m" + oid + ".xml";
                final File f = new File(GmAccessConfig.GM_ACCESS_FILES_DIR + fname);
                if (!f.exists() || !f.isFile() || !f.delete()) {
                    activeChar.sendAdminMessage("Error: Can't delete access-file: " + fname);
                    showModersPannel(activeChar);
                    return false;
                }

                if (modDel != null) {
                    activeChar.sendAdminMessage("Moderator " + modDel.getName() + " deleted.");
                } else {
                    activeChar.sendAdminMessage("Moderator with object ID " + oid + " deleted.");
                }

                showModersPannel(activeChar);
                break;
            case admin_penalty:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("USAGE: //penalty charName [count] [reason]");
                    return false;
                }

                int count = 1;
                if (wordList.length > 2) {
                    count = Integer.parseInt(wordList[2]);
                }

                String reason = "не указана";

                if (wordList.length > 3) {
                    reason = wordList[3];
                }

                int objectId = 0;

                final Player player = GameObjectsStorage.getPlayer(wordList[1]);
                if (player != null && player.getPlayerAccess().CanBanChat) {
                    objectId = player.getObjectId();
                    int oldPenaltyCount = 0;
                    final String oldPenalty = player.getPlayerVariables().get(PlayerVariables.PENALTY_CHAT_COUNT);
                    if (oldPenalty != null) {
                        oldPenaltyCount = Integer.parseInt(oldPenalty);
                    }

                    player.getPlayerVariables().set(PlayerVariables.PENALTY_CHAT_COUNT, String.valueOf(oldPenaltyCount + count), -1);
                } else {
                    // TODO: Не плохо было бы сделать сперва проверку, модератор это или нет.
                    objectId = CharacterDAO.getInstance().getObjectIdByName(wordList[1]);

                    if (objectId > 0) {
                        final int oldCount = CharacterVariablesDAO.getInstance().selectPenaltyChatCount(objectId);
                        CharacterVariablesDAO.getInstance().updatePenaltyChatCount(objectId, oldCount + count);
                    }
                }

                if (objectId > 0) {
                    if (ServerConfig.BANCHAT_ANNOUNCE_FOR_ALL_WORLD) {
                        AnnouncementUtils.announceToAll(activeChar + " оштрафовал модератора " + wordList[1] + " на " + count + ", причина: " + reason + '.');
                    } else {
                        AnnouncementUtils.shout(activeChar, activeChar + " оштрафовал модератора " + wordList[1] + " на " + count + ", причина: " + reason + '.', ChatType.CRITICAL_ANNOUNCE);
                    }
                }

                break;
        }

        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_changelvl,
        admin_moders,
        admin_moders_add,
        admin_moders_del,
        admin_penalty
    }
}