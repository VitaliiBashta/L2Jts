package org.mmocore.gameserver.handler.admincommands.impl;

import org.apache.commons.lang3.math.NumberUtils;
import org.jts.dataparser.data.holder.TransformHolder;
import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.jts.dataparser.data.holder.transform.TransformData;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.database.dao.impl.CharacterDAO;
import org.mmocore.gameserver.database.dao.impl.MailDAO;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.model.SubClass;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.base.PlayerClass;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.mail.Mail;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.RequestPlayerGamePointIncrease;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExPCCafePointInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.SkillList;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.PositionUtils;
import org.mmocore.gameserver.utils.Util;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AdminEditChar implements IAdminCommandHandler {
    public static void showCharacterList(final Player activeChar, Player player) {
        if (player == null) {
            final GameObject target = activeChar.getTarget();
            if (target != null && target.isPlayer()) {
                player = (Player) target;
            } else {
                return;
            }
        } else {
            activeChar.setTarget(player);
        }

        String clanName = "No Clan";
        if (player.getClan() != null) {
            clanName = player.getClan().getName() + '/' + player.getClan().getLevel();
        }

        final NumberFormat df = NumberFormat.getNumberInstance(Locale.ENGLISH);
        df.setMaximumFractionDigits(4);
        df.setMinimumFractionDigits(1);

        final HtmlMessage adminReply = new HtmlMessage(5);

        final StringBuilder replyMSG = new StringBuilder("<html><body>");
        replyMSG.append("<table width=260><tr>");
        replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
        replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_show_characters 0\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr></table><br>");

        replyMSG.append("<table width=270>");
        replyMSG.append("<tr><td width=100>Account/IP/Lang:</td><td>").append(player.getAccountName()).append('/').append(player.getIP()).append("/").append(player.getLanguage().toString()).append("</td></tr>");
        replyMSG.append("<tr><td width=100>Name/Level:</td><td>").append(player.getName()).append('/').append(player.getLevel()).append("</td></tr>");
        replyMSG.append("<tr><td width=100>Class/Id:</td><td>").append(player.getPlayerClassComponent().getClassId().name()).append('/').append(player.getPlayerClassComponent().getClassId().getId()).append("</td></tr>");
        replyMSG.append("<tr><td width=100>Clan/Level:</td><td>").append(clanName).append("</td></tr>");
        replyMSG.append("<tr><td width=100>Exp/Sp:</td><td>").append(player.getExp()).append('/').append(player.getSp()).append("</td></tr>");
        replyMSG.append("<tr><td width=100>Cur/Max Hp:</td><td>").append((int) player.getCurrentHp()).append('/').append(player.getMaxHp()).append("</td></tr>");
        replyMSG.append("<tr><td width=100>Cur/Max Mp:</td><td>").append((int) player.getCurrentMp()).append('/').append(player.getMaxMp()).append("</td></tr>");
        replyMSG.append("<tr><td width=100>Cur/Max Load:</td><td>").append(player.getCurrentLoad()).append('/').append(player.getMaxLoad()).append("</td></tr>");
        replyMSG.append("<tr><td width=100>Patk/Matk:</td><td>").append(player.getPAtk(null)).append('/').append(player.getMAtk(null, null)).append("</td></tr>");
        replyMSG.append("<tr><td width=100>Pdef/Mdef:</td><td>").append(player.getPDef(null)).append('/').append(player.getMDef(null, null)).append("</td></tr>");
        replyMSG.append("<tr><td width=100>PAtkSpd/MAtkSpd:</td><td>").append(player.getPAtkSpd()).append('/').append(player.getMAtkSpd()).append("</td></tr>");
        replyMSG.append("<tr><td width=100>Acc/Evas:</td><td>").append(player.getAccuracy())
                .append('/').append(player.getEvasionRate(null)).append("</td></tr>");
        replyMSG.append("<tr><td width=100>Crit/MCrit:</td><td>").append(player.getCriticalHit(null, null)).append('/').append(df.format(player.getMagicCriticalRate(null, null))).append("%</td></tr>");
        replyMSG.append("<tr><td width=100>Walk/Run:</td><td>").append(player.getWalkSpeed()).append('/').append(player.getRunSpeed()).append("</td></tr>");
        replyMSG.append("<tr><td width=100>Karma/Fame:</td><td>").append(player.getKarma()).append('/').append(player.getFame()).append("</td></tr>");
        replyMSG.append("<tr><td width=100>PvP/PK:</td><td>").append(player.getPvpKills()).append('/').append(player.getPkKills()).append("</td></tr>");
        replyMSG.append("<tr><td width=100>Coordinates:</td><td>").append(player.getX()).append(',').append(player.getY()).append(',').append(player.getZ()).append("</td></tr>");
        replyMSG.append("<tr><td width=100>Direction:</td><td>");
        replyMSG.append(PositionUtils.getDirectionTo(player, activeChar));
        replyMSG.append("</td></tr>");
        replyMSG.append("</table><br>");

        replyMSG.append("<table<tr>");
        replyMSG.append("<td><button value=\"Skills\" action=\"bypass -h admin_show_skills\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Effects\" action=\"bypass -h admin_show_effects\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Actions\" action=\"bypass -h admin_character_actions\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr><tr>");
        replyMSG.append("<td><button value=\"Stats\" action=\"bypass -h admin_edit_character\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td><button value=\"Exp & Sp\" action=\"bypass -h admin_add_exp_sp_to_character\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td></td>");
        replyMSG.append("</tr></table></body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        if (activeChar.getPlayerAccess().CanRename) {
            if (fullString.startsWith("admin_settitle")) {
                try {
                    final String val = fullString.substring(15);
                    final GameObject target = activeChar.getTarget();
                    final Player player;
                    if (target == null) {
                        return false;
                    }
                    if (target.isPlayer()) {
                        player = (Player) target;
                        player.setTitle(val);
                        player.sendMessage("Your title has been changed by a GM");
                        player.sendChanges();
                    } else if (target.isNpc()) {
                        ((NpcInstance) target).setTitle(val);
                        target.decayMe();
                        target.spawnMe();
                    }

                    return true;
                } catch (StringIndexOutOfBoundsException e) { // Case of empty character title
                    activeChar.sendAdminMessage("You need to specify the new title.");
                    return false;
                }
            } else if (fullString.startsWith("admin_setclass")) {
                try {
                    final String val = fullString.substring(15);
                    final int id = Integer.parseInt(val.trim());
                    GameObject target = activeChar.getTarget();

                    if (target == null || !target.isPlayer()) {
                        target = activeChar;
                    }
                    if (id > 136) {
                        activeChar.sendAdminMessage("There are no classes over 136 id.");
                        return false;
                    }
                    final Player player = target.getPlayer();
                    player.getPlayerClassComponent().setClassId(id, true, false);
                    player.sendMessage("Your class has been changed by a GM");
                    player.broadcastCharInfo();

                    return true;
                } catch (StringIndexOutOfBoundsException e) {
                    activeChar.sendAdminMessage("You need to specify the new class id.");
                    return false;
                }
            } else if (fullString.startsWith("admin_setname")) {
                try {
                    final String val = fullString.substring(14);
                    final GameObject target = activeChar.getTarget();
                    final Player player;
                    if (target != null && target.isPlayer()) {
                        player = (Player) target;
                    } else {
                        return false;
                    }
                    if (CharacterDAO.getInstance().getPlayersCountByName(val) > 0) {
                        activeChar.sendMessage("Name already exist.");
                        return false;
                    }
                    Log.add("Character " + player.getName() + " renamed to " + val + " by GM " + activeChar.getName(), "renames");
                    player.reName(val);
                    player.sendMessage("Your name has been changed by a GM");
                    return true;
                } catch (StringIndexOutOfBoundsException e) { // Case of empty character name
                    activeChar.sendMessage("You need to specify the new name.");
                    return false;
                }
            }
        }

        if (!activeChar.getPlayerAccess().CanEditChar && !activeChar.getPlayerAccess().CanViewChar) {
            return false;
        }

        if ("admin_current_player".equals(fullString)) {
            showCharacterList(activeChar, null);
        } else if (fullString.startsWith("admin_clearmails")) {
            try {
                final String val = fullString.substring(21);
                final int objId = CharacterDAO.getInstance().getObjectIdByName(val);
                if (objId == 0) {
                    activeChar.sendMessage("Wrong character name.");
                    return false;
                }
                final List<Mail> m = MailDAO.getInstance().getSentMailByOwnerId(objId);
                for (final Mail item : m) {
                    item.delete();
                }
            } catch (StringIndexOutOfBoundsException e) {
                activeChar.sendMessage("Usage: //clearmails char_name");
                return false;
            }
        } else if (fullString.startsWith("admin_character_list")) {
            try {
                final String val = fullString.substring(21);
                final Player target = GameObjectsStorage.getPlayer(val);
                showCharacterList(activeChar, target);
            } catch (StringIndexOutOfBoundsException e) {
                // Case of empty character name
            }
        } else if (fullString.startsWith("admin_show_characters")) {
            try {
                final String val = fullString.substring(22);
                final int page = Integer.parseInt(val);
                listCharacters(activeChar, page);
            } catch (StringIndexOutOfBoundsException e) {
                // Case of empty page
            }
        } else if (fullString.startsWith("admin_find_character")) {
            try {
                final String val = fullString.substring(21);
                findCharacter(activeChar, val);
            } catch (StringIndexOutOfBoundsException e) { // Case of empty character name
                activeChar.sendAdminMessage("You didnt enter a character name to find.");

                listCharacters(activeChar, 0);
            }
        } else if (!activeChar.getPlayerAccess().CanEditChar) {
            return false;
        } else if ("admin_edit_character".equals(fullString)) {
            editCharacter(activeChar);
        } else if ("admin_character_actions".equals(fullString)) {
            showCharacterActions(activeChar);
        } else if (fullString.startsWith("admin_setkarma")) {
            try {
                final String val = fullString.substring(15);
                final int karma = Integer.parseInt(val);
                setTargetKarma(activeChar, karma);
            } catch (StringIndexOutOfBoundsException e) {
                activeChar.sendMessage("Please specify new karma value.");
            }
        } else if (fullString.startsWith("admin_setpkkills")) {
            try {
                final int newVal = Integer.parseInt(wordList[1]);
                final GameObject target = activeChar.getTarget();
                if (target == null) {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                    return false;
                }

                final Player player;
                if (target.isPlayer()) {
                    player = (Player) target;
                } else {
                    return false;
                }

                if (newVal >= 0) {
                    final int old = player.getPkKills();
                    player.setPkKills(newVal);
                    player.sendChanges();

                    player.sendMessage("Admin has changed your pk kills from " + old + " to " + newVal + '.');
                    activeChar.sendAdminMessage("Successfully Changed pk kills for " + player.getName() + " from (" + old + ") to (" + newVal + ").");
                } else {
                    activeChar.sendAdminMessage("You must enter a value for pk kills greater than or equal to 0.");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                activeChar.sendAdminMessage("Please specify new pk kills value.");
            }
        } else if (fullString.startsWith("admin_setpvpkills")) {
            try {
                final int newVal = Integer.parseInt(wordList[1]);
                final GameObject target = activeChar.getTarget();
                if (target == null) {
                    activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                    return false;
                }

                final Player player;
                if (target.isPlayer()) {
                    player = (Player) target;
                } else {
                    return false;
                }

                if (newVal >= 0) {
                    final int old = player.getPvpKills();
                    player.setPvpKills(newVal);
                    player.sendChanges();

                    player.sendMessage("Admin has changed your pvp kills from " + old + " to " + newVal + '.');
                    activeChar.sendAdminMessage("Successfully Changed pvp kills for " + player.getName() + " from (" + old + ") to (" + newVal + ").");
                } else {
                    activeChar.sendAdminMessage("You must enter a value for pvp kills greater than or equal to 0.");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                activeChar.sendAdminMessage("Please specify new pk kills value.");
            }
        } else if (fullString.startsWith("admin_save_modifications")) {
            try {
                final String val = fullString.substring(24);
                adminModifyCharacter(activeChar, val);
            } catch (StringIndexOutOfBoundsException e) { // Case of empty character name
                activeChar.sendAdminMessage("Error while modifying character.");
                listCharacters(activeChar, 0);
            }
        } else if ("admin_rec".equals(fullString)) {
            final GameObject target = activeChar.getTarget();
            final Player player;
            if (target != null && target.isPlayer()) {
                player = (Player) target;
            } else {
                return false;
            }
            player.getRecommendationComponent().setRecomHave(player.getRecommendationComponent().getRecomHave() + 1);
            player.sendMessage("You have been recommended by a GM");
            player.broadcastCharInfo();
        } else if (fullString.startsWith("admin_rec")) {
            try {
                final String val = fullString.substring(10);
                final int recVal = Integer.parseInt(val);
                final GameObject target = activeChar.getTarget();
                final Player player;
                if (target != null && target.isPlayer()) {
                    player = (Player) target;
                } else {
                    return false;
                }
                player.getRecommendationComponent().setRecomHave(player.getRecommendationComponent().getRecomHave() + recVal);
                player.sendMessage("You have been recommended by a GM");
                player.broadcastCharInfo();
            } catch (NumberFormatException e) {
                activeChar.sendAdminMessage("Command format is //rec <number>");
            }
        } else if (fullString.startsWith("admin_sethero")) {
            // Статус меняется только на текущую логон сессию
            final GameObject target = activeChar.getTarget();
            final Player player;
            if (wordList.length > 1 && wordList[1] != null) {
                player = GameObjectsStorage.getPlayer(wordList[1]);
                if (player == null) {
                    activeChar.sendAdminMessage("Character " + wordList[1] + " not found in game.");
                    return false;
                }
            } else if (target != null && target.isPlayer()) {
                player = (Player) target;
            } else {
                activeChar.sendAdminMessage("You must specify the name or target character.");
                return false;
            }

            if (player.isHero()) {
                player.setHero(false);
                player.updatePledgeClass();
                player.removeSkill(SkillTable.getInstance().getSkillEntry(395, 1));
                player.removeSkill(SkillTable.getInstance().getSkillEntry(396, 1));
                player.removeSkill(SkillTable.getInstance().getSkillEntry(1374, 1));
                player.removeSkill(SkillTable.getInstance().getSkillEntry(1375, 1));
                player.removeSkill(SkillTable.getInstance().getSkillEntry(1376, 1));
            } else {
                player.setHero(true);
                player.updatePledgeClass();
                player.addSkill(SkillTable.getInstance().getSkillEntry(395, 1));
                player.addSkill(SkillTable.getInstance().getSkillEntry(396, 1));
                player.addSkill(SkillTable.getInstance().getSkillEntry(1374, 1));
                player.addSkill(SkillTable.getInstance().getSkillEntry(1375, 1));
                player.addSkill(SkillTable.getInstance().getSkillEntry(1376, 1));
            }

            player.sendPacket(new SkillList(player));

            player.sendMessage("Admin has changed your hero status.");
            player.broadcastUserInfo(true);
        } else if (fullString.startsWith("admin_setnoble")) {
            // Статус сохраняется в базе
            final GameObject target = activeChar.getTarget();
            final Player player;
            if (wordList.length > 1 && wordList[1] != null) {
                player = GameObjectsStorage.getPlayer(wordList[1]);
                if (player == null) {
                    activeChar.sendAdminMessage("Character " + wordList[1] + " not found in game.");
                    return false;
                }
            } else if (target != null && target.isPlayer()) {
                player = (Player) target;
            } else {
                activeChar.sendAdminMessage("You must specify the name or target character.");
                return false;
            }

            if (player.isNoble()) {
                Olympiad.removeNoble(player);
                player.setNoble(false);
                player.sendMessage("Admin changed your noble status, now you are not nobless.");
            } else {
                Olympiad.addNoble(player);
                player.setNoble(true);
                player.sendMessage("Admin changed your noble status, now you are Nobless.");
            }

            player.updatePledgeClass();
            player.updateNobleSkills();
            player.sendPacket(new SkillList(player));
            player.broadcastUserInfo(true);
        } else if (fullString.startsWith("admin_setcolor")) {
            try {
                final String val = fullString.substring(15);
                final GameObject target = activeChar.getTarget();
                final Player player;
                if (target != null && target.isPlayer()) {
                    player = (Player) target;
                } else {
                    return false;
                }
                player.getAppearanceComponent().setNameColor(Integer.decode("0x" + val));
                player.sendMessage("Your name color has been changed by a GM");
                player.broadcastUserInfo(true);
            } catch (StringIndexOutOfBoundsException e) { // Case of empty color
                activeChar.sendAdminMessage("You need to specify the new color.");
            }
        } else if (fullString.startsWith("admin_add_exp_sp_to_character")) {
            addExpSp(activeChar);
        } else if (fullString.startsWith("admin_add_exp_sp")) {
            try {
                final String val = fullString.substring(16).trim();

                final String[] vals = val.split(" ");
                final long exp = NumberUtils.toLong(vals[0], 0L);
                final int sp = vals.length > 1 ? NumberUtils.toInt(vals[1], 0) : 0;

                adminAddExpSp(activeChar, exp, sp);
            } catch (Exception e) {
                activeChar.sendAdminMessage("Usage: //add_exp_sp <exp> <sp>");
            }
        } else if (fullString.startsWith("admin_trans")) {
            final StringTokenizer st = new StringTokenizer(fullString);
            if (st.countTokens() > 1) {
                st.nextToken();
                final int transformId;
                try {
                    transformId = Integer.parseInt(st.nextToken());
                } catch (Exception e) {
                    activeChar.sendAdminMessage("Specify a valid integer value.");
                    return false;
                }
                if (transformId != 0 && activeChar.isTransformed()) {
                    activeChar.sendPacket(SystemMsg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
                    return false;
                }
                final Optional<TransformData> data = TransformHolder.getInstance().getTransformId(transformId);
                if (data != null)
                    activeChar.setTransformation(data.get());
                activeChar.sendAdminMessage("Transforming...");
            } else {
                activeChar.sendAdminMessage("Usage: //trans <ID>");
            }
        } else if (fullString.startsWith("admin_setsubclass")) {
            final GameObject target = activeChar.getTarget();
            if (target == null || !target.isPlayer()) {
                activeChar.sendPacket(SystemMsg.SELECT_TARGET);
                return false;
            }
            final Player player = (Player) target;

            final StringTokenizer st = new StringTokenizer(fullString);
            if (st.countTokens() > 1) {
                st.nextToken();
                final int classId = Short.parseShort(st.nextToken());
                if (!player.getPlayerClassComponent().addSubClass(classId, true, 0)) {
                    activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2VillageMasterInstance.SubclassCouldNotBeAdded"));
                    return false;
                }
                player.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_A_CLASS_TRANSFER); // Transfer to new class.
            } else {
                setSubclass(activeChar, player);
            }
        } else if (fullString.startsWith("admin_setfame")) {
            try {
                final String val = fullString.substring(14);
                final int fame = Integer.parseInt(val);
                setTargetFame(activeChar, fame);
            } catch (StringIndexOutOfBoundsException e) {
                activeChar.sendAdminMessage("Please specify new fame value.");
            }
        } else if (fullString.startsWith("admin_setbday")) {
            final String msgUsage = "Usage: //setbday YYYY-MM-DD";
            final String date = fullString.substring(14);
            if (date.length() != 10 || !Util.isMatchingRegexp(date, "[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
                activeChar.sendAdminMessage(msgUsage);
                return false;
            }

            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                dateFormat.parse(date);
            } catch (ParseException e) {
                activeChar.sendAdminMessage(msgUsage);
            }

            if (activeChar.getTarget() == null || !activeChar.getTarget().isPlayer()) {
                activeChar.sendAdminMessage("Please select a character.");
                return false;
            }

            CharacterDAO.getInstance().updateCreateTime(activeChar.getTarget().getObjectId(), date);

            activeChar.sendAdminMessage("New Birthday for " + activeChar.getTarget().getName() + ": " + date);
            activeChar.getTarget().getPlayer().sendMessage("Admin changed your birthday to: " + date);
        } else if (fullString.startsWith("admin_give_item")) {
            if (wordList.length < 3) {
                activeChar.sendAdminMessage("Usage: //give_item id count <target>");
                return false;
            }
            final int id = Integer.parseInt(wordList[1]);
            final int count = Integer.parseInt(wordList[2]);
            if (id < 1 || count < 1 || activeChar.getTarget() == null || !activeChar.getTarget().isPlayer()) {
                activeChar.sendAdminMessage("Usage: //give_item id count <target>");
                return false;
            }
            ItemFunctions.addItem(activeChar.getTarget().getPlayer(), id, count, true);
        } else if (fullString.startsWith("admin_add_bang")) {
            if (!AllSettingsConfig.ALT_PCBANG_POINTS_ENABLED) {
                activeChar.sendAdminMessage("Error! Pc Bang Points service disabled!");
                return true;
            }
            if (wordList.length < 1) {
                activeChar.sendAdminMessage("Usage: //add_bang count <target>");
                return false;
            }
            final int count = Integer.parseInt(wordList[1]);
            if (count < 1 || activeChar.getTarget() == null || !activeChar.getTarget().isPlayer()) {
                activeChar.sendAdminMessage("Usage: //add_bang count <target>");
                return false;
            }
            final Player target = activeChar.getTarget().getPlayer();
            target.getPremiumAccountComponent().addPcBangPoints(count, false);
            activeChar.sendAdminMessage("You have added " + count + " Pc Bang Points to " + target.getName());
        } else if (fullString.startsWith("admin_set_bang")) {
            if (!AllSettingsConfig.ALT_PCBANG_POINTS_ENABLED) {
                activeChar.sendAdminMessage("Error! Pc Bang Points service disabled!");
                return true;
            }
            if (wordList.length < 1) {
                activeChar.sendAdminMessage("Usage: //set_bang count <target>");
                return false;
            }
            final int count = Integer.parseInt(wordList[1]);
            if (count < 1 || activeChar.getTarget() == null || !activeChar.getTarget().isPlayer()) {
                activeChar.sendAdminMessage("Usage: //set_bang count <target>");
                return false;
            }
            final Player target = activeChar.getTarget().getPlayer();
            target.getPremiumAccountComponent().setPcBangPoints(count);
            target.sendMessage("Your Pc Bang Points count is now " + count);
            target.sendPacket(new ExPCCafePointInfo(target, count, 1, 2, 12));
            activeChar.sendAdminMessage("You have set " + target.getName() + "'s Pc Bang Points to " + count);
        } else if (fullString.startsWith("admin_add_premium_point")) {
            premiumPoints(activeChar, wordList, false);
        } else if (fullString.startsWith("admin_remove_premium_point")) {
            premiumPoints(activeChar, wordList, true);
        }
        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void premiumPoints(final Player activeChar, final String[] wordList, final boolean decrease) {
        final String operationWord = decrease ? "removed" : "added";
        final String commandWord = decrease ? "remove_premium_point" : "add_premium_point";
        if (wordList.length < 3)
            activeChar.sendAdminMessage("You must use //" + commandWord + " <playerName> <pointCount>");
        else {
            try {
                final String targetName = wordList[1];
                final long pointCount = Long.parseLong(wordList[2]);
                if (targetName == null || pointCount < 1) {
                    activeChar.sendAdminMessage("You must use //" + commandWord + " <playerName> <pointCount>");
                    return;
                }
                final Player targetPlayer = GameObjectsStorage.getPlayer(targetName);
                if (targetPlayer == null) {
                    activeChar.sendAdminMessage("Target is not player, or target disconnected from server.");
                    return;
                }
                AuthServerCommunication.getInstance().sendPacket(new RequestPlayerGamePointIncrease(targetPlayer, pointCount, decrease));
                activeChar.sendAdminMessage("You " + operationWord + " " + pointCount + " point from " + targetName);
                if (activeChar != targetPlayer)
                    targetPlayer.sendAdminMessage("GameMaster " + operationWord + " you " + pointCount + " point.");
            } catch (Exception e) {
                activeChar.sendAdminMessage("Please specify you request //add(remove)_premium_point <playerName> <pointCount>");
                return;
            }
        }
    }

    private void listCharacters(final Player activeChar, int page) {
        final Collection<Player> p0 = GameObjectsStorage.getPlayers();
        final Player[] players = p0.toArray(new Player[p0.size()]);

        final int MaxCharactersPerPage = 20;
        int MaxPages = players.length / MaxCharactersPerPage;

        if (players.length > MaxCharactersPerPage * MaxPages) {
            MaxPages++;
        }

        // Check if number of users changed
        if (page > MaxPages) {
            page = MaxPages;
        }

        final int CharactersStart = MaxCharactersPerPage * page;
        int CharactersEnd = players.length;
        if (CharactersEnd - CharactersStart > MaxCharactersPerPage) {
            CharactersEnd = CharactersStart + MaxCharactersPerPage;
        }

        final HtmlMessage adminReply = new HtmlMessage(5);

        final StringBuilder replyMSG = new StringBuilder("<html><body>");
        replyMSG.append("<table width=260><tr>");
        replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
        replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr></table>");
        replyMSG.append("<br><br>");
        replyMSG.append("<table width=270>");
        replyMSG.append("<tr><td width=270>You can find a character by writing his name and</td></tr>");
        replyMSG.append("<tr><td width=270>clicking Find bellow.<br></td></tr>");
        replyMSG.append("<tr><td width=270>Note: Names should be written case sensitive.</td></tr>");
        replyMSG.append("</table><br>");
        replyMSG.append("<center><table><tr><td>");
        replyMSG.append("<edit var=\"character_name\" width=80></td><td><button value=\"Find\" action=\"bypass -h admin_find_character $character_name\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
        replyMSG.append("</td></tr></table></center><br><br>");

        for (int x = 0; x < MaxPages; x++) {
            final int pagenr = x + 1;
            replyMSG.append("<center><a action=\"bypass -h admin_show_characters ").append(x).append("\">Page ").append(pagenr).append("</a></center>");
        }
        replyMSG.append("<br>");

        // List Players in a Table
        replyMSG.append("<table width=270>");
        replyMSG.append("<tr><td width=80>Name:</td><td width=110>Class:</td><td width=40>Level:</td></tr>");
        for (int i = CharactersStart; i < CharactersEnd; i++) {
            final Player p = players[i];
            replyMSG.append("<tr><td width=80>" + "<a action=\"bypass -h admin_character_list ").append(p.getName()).append("\">").append(p.getName()).append("</a></td><td width=110>").append(p.getPlayerClassComponent().getClassId().name()).append("</td><td width=40>").append(p.getLevel()).append("</td></tr>");
        }
        replyMSG.append("</table>");
        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    private void setTargetKarma(final Player activeChar, final int newKarma) {
        final GameObject target = activeChar.getTarget();
        if (target == null) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        final Player player;
        if (target.isPlayer()) {
            player = (Player) target;
        } else {
            return;
        }

        if (newKarma >= 0) {
            final int oldKarma = player.getKarma();
            player.setKarma(newKarma);

            player.sendMessage("Admin has changed your karma from " + oldKarma + " to " + newKarma + '.');
            activeChar.sendAdminMessage("Successfully Changed karma for " + player.getName() + " from (" + oldKarma + ") to (" + newKarma + ").");
        } else {
            activeChar.sendAdminMessage("You must enter a value for karma greater than or equal to 0.");
        }
    }

    private void setTargetFame(final Player activeChar, final int newFame) {
        final GameObject target = activeChar.getTarget();
        if (target == null) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        final Player player;
        if (target.isPlayer()) {
            player = (Player) target;
        } else {
            return;
        }

        if (newFame >= 0) {
            final int oldFame = player.getFame();
            player.setFame(newFame, "Admin");

            player.sendMessage("Admin has changed your fame from " + oldFame + " to " + newFame + '.');
            activeChar.sendAdminMessage("Successfully Changed fame for " + player.getName() + " from (" + oldFame + ") to (" + newFame + ").");
        } else {
            activeChar.sendAdminMessage("You must enter a value for fame greater than or equal to 0.");
        }
    }

    private void adminModifyCharacter(final Player activeChar, final String modifications) {
        final GameObject target = activeChar.getTarget();
        if (target == null || !target.isPlayer()) {
            activeChar.sendPacket(SystemMsg.SELECT_TARGET);
            return;
        }

        final Player player = (Player) target;
        final String[] strvals = modifications.split("&");
        final Integer[] vals = new Integer[strvals.length];
        for (int i = 0; i < strvals.length; i++) {
            strvals[i] = strvals[i].trim();
            vals[i] = strvals[i].isEmpty() ? null : Integer.valueOf(strvals[i]);
        }

        if (vals[0] != null) {
            player.setCurrentHp(vals[0], false);
        }

        if (vals[1] != null) {
            player.setCurrentMp(vals[1]);
        }

        if (vals[2] != null) {
            player.setKarma(vals[2]);
        }

        if (vals[3] != null) {
            player.setPvpFlag(vals[3]);
        }

        if (vals[4] != null) {
            player.setPvpKills(vals[4]);
        }

        if (vals[5] != null) {
            player.getPlayerClassComponent().setClassId(vals[5], true, false);
        }

        editCharacter(activeChar); // Back to start
        player.broadcastCharInfo();
        player.decayMe();
        player.spawnMe(activeChar.getLoc());
    }

    private void editCharacter(final Player activeChar) {
        final GameObject target = activeChar.getTarget();
        if (target == null || !target.isPlayer()) {
            activeChar.sendPacket(SystemMsg.SELECT_TARGET);
            return;
        }

        final Player player = (Player) target;
        final HtmlMessage adminReply = new HtmlMessage(5);

        final StringBuilder replyMSG = new StringBuilder("<html><body>");
        replyMSG.append("<table width=260><tr>");
        replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
        replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr></table>");
        replyMSG.append("<br><br>");
        replyMSG.append("<center>Editing character: ").append(player.getName()).append("</center><br>");
        replyMSG.append("<table width=250>");
        replyMSG.append("<tr><td width=40></td><td width=70>Curent:</td><td width=70>Max:</td><td width=70></td></tr>");
        replyMSG.append("<tr><td width=40>HP:</td><td width=70>").append(player.getCurrentHp()).append("</td><td width=70>").append(player.getMaxHp()).append("</td><td width=70>Karma: ").append(player.getKarma()).append("</td></tr>");
        replyMSG.append("<tr><td width=40>MP:</td><td width=70>").append(player.getCurrentMp()).append("</td><td width=70>").append(player.getMaxMp()).append("</td><td width=70>Pvp Kills: ").append(player.getPvpKills()).append("</td></tr>");
        replyMSG.append("<tr><td width=40>Load:</td><td width=70>").append(player.getCurrentLoad()).append("</td><td width=70>").append(player.getMaxLoad()).append("</td><td width=70>Pvp Flag: ").append(player.getPvpFlag()).append("</td></tr>");
        replyMSG.append("</table>");
        replyMSG.append("<table width=270><tr><td>Class<?> Template Id: ").append(player.getPlayerClassComponent().getClassId()).append('/').append(player.getPlayerClassComponent().getClassId().getId()).append("</td></tr></table><br>");
        replyMSG.append("<table width=270>");
        replyMSG.append("<tr><td>Note: Fill all values before saving the modifications.</td></tr>");
        replyMSG.append("</table><br>");
        replyMSG.append("<table width=270>");
        replyMSG.append("<tr><td width=50>Hp:</td><td><edit var=\"hp\" width=50></td><td width=50>Mp:</td><td><edit var=\"mp\" width=50></td></tr>");
        replyMSG.append("<tr><td width=50>Pvp Flag:</td><td><edit var=\"pvpflag\" width=50></td><td width=50>Karma:</td><td><edit var=\"karma\" width=50></td></tr>");
        replyMSG.append("<tr><td width=50>Class<?> Id:</td><td><edit var=\"classid\" width=50></td><td width=50>Pvp Kills:</td><td><edit var=\"pvpkills\" width=50></td></tr>");
        replyMSG.append("</table><br>");
        replyMSG.append("<center><button value=\"Save Changes\" action=\"bypass -h admin_save_modifications $hp & $mp & $karma & $pvpflag & $pvpkills & $classid &\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center><br>");
        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    private void showCharacterActions(final Player activeChar) {
        final GameObject target = activeChar.getTarget();
        final Player player;
        if (target != null && target.isPlayer()) {
            player = (Player) target;
        } else {
            return;
        }

        final HtmlMessage adminReply = new HtmlMessage(5);

        final StringBuilder replyMSG = new StringBuilder("<html><body>");
        replyMSG.append("<table width=260><tr>");
        replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
        replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr></table><br><br>");
        replyMSG.append("<center>Admin Actions for: ").append(player.getName()).append("</center><br>");
        replyMSG.append("<center><table width=200><tr>");
        replyMSG.append("<td width=100>Argument(*):</td><td width=100><edit var=\"arg\" width=100></td>");
        replyMSG.append("</tr></table><br></center>");
        replyMSG.append("<table width=270>");

        replyMSG.append("<tr><td width=90><button value=\"Teleport\" action=\"bypass -h admin_teleportto ").append(player.getName()).append("\" width=85 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td width=90><button value=\"Recall\" action=\"bypass -h admin_recall ").append(player.getName()).append("\" width=85 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td width=90><button value=\"Quests\" action=\"bypass -h admin_quests ").append(player.getName()).append("\" width=85 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>");

        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    private void findCharacter(final Player activeChar, final String CharacterToFind) {
        final HtmlMessage adminReply = new HtmlMessage(5);
        int CharactersFound = 0;

        final StringBuilder replyMSG = new StringBuilder("<html><body>");
        replyMSG.append("<table width=260><tr>");
        replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
        replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_show_characters 0\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr></table>");
        replyMSG.append("<br><br>");

        for (final Player element : GameObjectsStorage.getPlayers()) {
            if (element.getName().startsWith(CharacterToFind)) {
                CharactersFound += 1;
                replyMSG.append("<table width=270>");
                replyMSG.append("<tr><td width=80>Name</td><td width=110>Class</td><td width=40>Level</td></tr>");
                replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_character_list ").append(element.getName()).append("\">").append(element.getName()).append("</a></td><td width=110>").append(element.getPlayerClassComponent().getClassId().name()).append("</td><td width=40>").append(element.getLevel()).append("</td></tr>");
                replyMSG.append("</table>");
            }
        }

        if (CharactersFound == 0) {
            replyMSG.append("<table width=270>");
            replyMSG.append("<tr><td width=270>Your search did not find any characters.</td></tr>");
            replyMSG.append("<tr><td width=270>Please try again.<br></td></tr>");
            replyMSG.append("</table><br>");
            replyMSG.append("<center><table><tr><td>");
            replyMSG.append("<edit var=\"character_name\" width=80></td><td><button value=\"Find\" action=\"bypass -h admin_find_character $character_name\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">");
            replyMSG.append("</td></tr></table></center>");
        } else {
            replyMSG.append("<center><br>Found ").append(CharactersFound).append(" character");

            if (CharactersFound == 1) {
                replyMSG.append('.');
            } else if (CharactersFound > 1) {
                replyMSG.append("s.");
            }
        }

        replyMSG.append("</center></body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    private void addExpSp(final Player activeChar) {
        final GameObject target = activeChar.getTarget();
        final Player player;
        if (target != null && target.isPlayer() && (activeChar.equals(target) || activeChar.getPlayerAccess().CanEditCharAll)) {
            player = (Player) target;
        } else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        final HtmlMessage adminReply = new HtmlMessage(5);

        final StringBuilder replyMSG = new StringBuilder("<html><body>");
        replyMSG.append("<table width=260><tr>");
        replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
        replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr></table>");
        replyMSG.append("<br><br>");
        replyMSG.append("<table width=270><tr><td>Name: ").append(player.getName()).append("</td></tr>");
        replyMSG.append("<tr><td>Lv: ").append(player.getLevel()).append(' ').append(player.getPlayerClassComponent().getClassId().name()).append("</td></tr>");
        replyMSG.append("<tr><td>Exp: ").append(player.getExp()).append("</td></tr>");
        replyMSG.append("<tr><td>Sp: ").append(player.getSp()).append("</td></tr></table>");
        replyMSG.append("<br><table width=270><tr><td>Note: Dont forget that modifying players skills can</td></tr>");
        replyMSG.append("<tr><td>ruin the game...</td></tr></table><br>");
        replyMSG.append("<table width=270><tr><td>Note: Fill all values before saving the modifications.,</td></tr>");
        replyMSG.append("<tr><td>Note: Use 0 if no changes are needed.</td></tr></table><br>");
        replyMSG.append("<center><table><tr>");
        replyMSG.append("<td>Exp: <edit var=\"exp_to_add\" width=50></td>");
        replyMSG.append("<td>Sp:  <edit var=\"sp_to_add\" width=50></td>");
        replyMSG.append("<td>&nbsp;<button value=\"Save Changes\" action=\"bypass -h admin_add_exp_sp $exp_to_add $sp_to_add\" width=80 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr></table></center>");
        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    private void adminAddExpSp(final Player activeChar, final long exp, final int sp) {
        if (!activeChar.getPlayerAccess().CanEditCharAll) {
            activeChar.sendAdminMessage("You have not enough privileges, for use this function.");
            return;
        }

        final GameObject target = activeChar.getTarget();
        if (target == null) {
            activeChar.sendPacket(SystemMsg.SELECT_TARGET);
            return;
        }

        if (!target.isPlayable()) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        final Playable playable = (Playable) target;
        playable.addExpAndSp(exp, sp);

        activeChar.sendAdminMessage("Added " + exp + " experience and " + sp + " SP to " + playable.getName() + '.');
    }

    private void setSubclass(final Player activeChar, final Player player) {
        final StringBuilder content = new StringBuilder("<html><body>");
        final HtmlMessage html = new HtmlMessage(5);
        final Set<PlayerClass> subsAvailable;
        subsAvailable = getAvailableSubClasses(player);

        if (subsAvailable != null && !subsAvailable.isEmpty()) {
            content.append("Add Subclass:<br>Which subclass do you wish to add?<br>");

            for (final PlayerClass subClass : subsAvailable) {
                content.append("<a action=\"bypass -h admin_setsubclass ").append(subClass.ordinal()).append("\">").append(formatClassForDisplay(subClass)).append("</a><br>");
            }
        } else {
            activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2VillageMasterInstance.NoSubAtThisTime"));
            return;
        }
        content.append("</body></html>");
        html.setHtml(content.toString());
        activeChar.sendPacket(html);
    }

    private Set<PlayerClass> getAvailableSubClasses(final Player player) {
        final int charClassId = player.getPlayerClassComponent().getBaseClassId();

        final PlayerClass currClass = PlayerClass.values()[charClassId];// .valueOf(charClassName);

        /**
         * If the race of your main class is Elf or Dark Elf, you may not select
         * each class as a subclass to the other class, and you may not select
         * Overlord and Warsmith class as a subclass.
         *
         * You may not select a similar class as the subclass. The occupations
         * classified as similar classes are as follows:
         *
         * Treasure Hunter, Plainswalker and Abyss Walker Hawkeye, Silver Ranger
         * and Phantom Ranger Paladin, Dark Avenger, Temple Knight and Shillien
         * Knight Warlocks, Elemental Summoner and Phantom Summoner Elder and
         * Shillien Elder Swordsinger and Bladedancer Sorcerer, Spellsinger and
         * Spellhowler
         *
         * Kamael могут брать только сабы Kamael
         * Другие классы не могут брать сабы Kamael
         *
         */
        final Set<PlayerClass> availSubs = currClass.getAvailableSubclasses();
        if (availSubs == null) {
            return null;
        }

        // из списка сабов удаляем мейн класс игрока
        availSubs.remove(currClass);

        for (final PlayerClass availSub : availSubs) {
            // Удаляем из списка возможных сабов, уже взятые сабы и их предков
            for (final SubClass subClass : player.getPlayerClassComponent().getSubClasses().values()) {
                if (availSub.ordinal() == subClass.getClassId()) {
                    availSubs.remove(availSub);
                    continue;
                }

                // Удаляем из возможных сабов их родителей, если таковые есть у чара
                final ClassId parent = ClassId.VALUES[availSub.ordinal()].getParent(player.getSex());
                if (parent != null && parent.getId() == subClass.getClassId()) {
                    availSubs.remove(availSub);
                    continue;
                }

                // Удаляем из возможных сабов родителей текущих сабклассов, иначе если взять саб berserker
                // и довести до 3ей профы - doombringer, игроку будет предложен berserker вновь (дежавю)
                final ClassId subParent = ClassId.VALUES[subClass.getClassId()].getParent(player.getSex());
                if (subParent != null && subParent.getId() == availSub.ordinal()) {
                    availSubs.remove(availSub);
                }
            }

            // Особенности саб классов камаэль
            if (availSub.isOfRace(PlayerRace.kamael)) {
                // Для Soulbreaker-а и SoulHound не предлагаем Soulbreaker-а другого пола
                if ((currClass == PlayerClass.MaleSoulHound || currClass == PlayerClass.FemaleSoulHound || currClass == PlayerClass.FemaleSoulbreaker || currClass == PlayerClass.MaleSoulbreaker) && (availSub == PlayerClass.FemaleSoulbreaker || availSub == PlayerClass.MaleSoulbreaker)) {
                    availSubs.remove(availSub);
                }

                // Для Berserker(doombringer) и Arbalester(trickster) предлагаем Soulbreaker-а только своего пола
                if (currClass == PlayerClass.Berserker || currClass == PlayerClass.Doombringer || currClass == PlayerClass.Arbalester || currClass == PlayerClass.Trickster) {
                    if (player.getSex() == 1 && availSub == PlayerClass.MaleSoulbreaker || player.getSex() == 0 && availSub == PlayerClass.FemaleSoulbreaker) {
                        availSubs.remove(availSub);
                    }
                }

                // Inspector доступен, только когда вкачаны 2 возможных первых саба камаэль(+ мейн класс):
                // doombringer(berserker), soulhound(maleSoulbreaker, femaleSoulbreaker), trickster(arbalester)
                if (availSub == PlayerClass.Inspector)
                // doombringer(berserker)
                {
                    if (!(player.getPlayerClassComponent().getSubClasses().containsKey(131) || player.getPlayerClassComponent().getSubClasses().containsKey(127))) {
                        availSubs.remove(availSub);
                    }
                    // soulhound(maleSoulbreaker, femaleSoulbreaker)
                    else if (!(player.getPlayerClassComponent().getSubClasses().containsKey(132) || player.getPlayerClassComponent().getSubClasses().containsKey(133) || player.getPlayerClassComponent().getSubClasses().containsKey(128) || player.getPlayerClassComponent().getSubClasses().containsKey(129))) {
                        availSubs.remove(availSub);
                    }
                    // trickster(arbalester)
                    else if (!(player.getPlayerClassComponent().getSubClasses().containsKey(134) || player.getPlayerClassComponent().getSubClasses().containsKey(130))) {
                        availSubs.remove(availSub);
                    }
                }
            }
        }
        return availSubs;
    }

    private String formatClassForDisplay(final PlayerClass className) {
        String classNameStr = className.toString();
        final char[] charArray = classNameStr.toCharArray();

        for (int i = 1; i < charArray.length; i++) {
            if (Character.isUpperCase(charArray[i])) {
                classNameStr = classNameStr.substring(0, i) + ' ' + classNameStr.substring(i);
            }
        }

        return classNameStr;
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_edit_character,
        admin_character_actions,
        admin_current_player,
        admin_character_list,
        admin_show_characters,
        admin_find_character,
        admin_save_modifications,
        admin_rec,
        admin_settitle,
        admin_setclass,
        admin_setname,
        admin_setcolor,
        admin_add_exp_sp_to_character,
        admin_add_exp_sp,
        admin_sethero,
        admin_setnoble,
        admin_trans,
        admin_setsubclass,
        admin_setfame,
        admin_setbday,
        admin_give_item,
        admin_add_bang,
        admin_set_bang,
        admin_setkarma,
        admin_setpkkills,
        admin_setpvpkills,
        admin_clearmails,
        admin_add_premium_point,
        admin_remove_premium_point
    }
}
