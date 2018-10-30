package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.configuration.config.OlympiadConfig;
import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.model.entity.olympiad.CompType;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.entity.olympiad.OlympiadDatabase;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExReceiveOlympiad;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class OlympiadManagerInstance extends NpcInstance {
    private static final Logger _log = LoggerFactory.getLogger(OlympiadManagerInstance.class);

    public OlympiadManagerInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
        if (OlympiadConfig.ENABLE_OLYMPIAD) {
            Olympiad.addOlympiadNpc(this);
        }
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (checkForDominionWard(player)) {
            return;
        }

        if (!OlympiadConfig.ENABLE_OLYMPIAD) {
            return;
        }

        if (command.startsWith("OlympiadNoble")) {
            if (!OlympiadConfig.ENABLE_OLYMPIAD) {
                return;
            }

            final int val = Integer.parseInt(command.substring(14));
            final HtmlMessage html = new HtmlMessage(this);

            switch (val) {
                case 1:
                    Olympiad.unRegisterNoble(player);
                    showChatWindow(player, 0);
                    break;
                case 2:
                    if (Olympiad.isRegistered(player)) {
                        player.sendPacket(html.setFile(Olympiad.OLYMPIAD_HTML_PATH + "manager_noregister.htm"));
                    } else {
                        html.setFile(Olympiad.OLYMPIAD_HTML_PATH + "manager_register.htm");
                        html.replace("%round%", String.valueOf(Olympiad.getCompetitionDone(player.getObjectId())));
                        html.replace("%circle%", String.valueOf(Olympiad.getCurrentCycle()));
                        html.replace("%participants%", String.valueOf(Olympiad.getParticipants()));
                        player.sendPacket(html);
                    }
                    break;
                case 4:
                    Olympiad.registerNoble(player, CompType.NON_CLASSED);
                    break;
                case 5:
                    if (!OlympiadConfig.OLYMPIAD_CLASS_ENABLE) {
                        player.sendMessage("Классовые бои отключены");
                        return;
                    }
                    Olympiad.registerNoble(player, CompType.CLASSED);
                    break;
                case 6:
                    final int passes = Olympiad.getNoblessePasses(player);
                    if (passes > 0) {
                        player.getInventory().addItem(OlympiadConfig.ALT_OLY_COMP_RITEM, passes);
                        player.sendPacket(SystemMessage.obtainItems(OlympiadConfig.ALT_OLY_COMP_RITEM, passes, 0));
                    } else {
                        player.sendPacket(html.setFile(Olympiad.OLYMPIAD_HTML_PATH + "manager_nopoints.htm"));
                    }
                    break;
                case 7:
                    MultiSellHolder.getInstance().SeparateAndSend(102, player, getObjectId(), 0);
                    break;
                case 9:
                    MultiSellHolder.getInstance().SeparateAndSend(103, player, getObjectId(), 0);
                    break;
                case 10:
                    if (!OlympiadConfig.OLYMPIAD_CLASS_ENABLE) {
                        player.sendMessage("3х3 бои отключены");
                        return;
                    }
                    Olympiad.registerNoble(player, CompType.TEAM);
                    break;
                default:
                    _log.warn("Olympiad System: Couldnt send packet for request " + val);
                    break;
            }
        } else if (command.startsWith("Olympiad")) {
            if (!OlympiadConfig.ENABLE_OLYMPIAD) {
                return;
            }
            final int val = Integer.parseInt(command.substring(9, 10));

            final HtmlMessage reply = new HtmlMessage(this);

            switch (val) {
                case 1:
                    if (!Olympiad.inCompPeriod() || Olympiad.isOlympiadEnd()) {
                        player.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
                        return;
                    }
                    player.sendPacket(new ExReceiveOlympiad.MatchList());
                    break;
                case 2:
                    // for example >> Olympiad 1_88
                    final int classId = Integer.parseInt(command.substring(11));
                    if (classId >= 88) {
                        reply.setFile(Olympiad.OLYMPIAD_HTML_PATH + "manager_ranking.htm");

                        final List<String> names = OlympiadDatabase.getClassLeaderBoard(classId);

                        int index = 1;
                        for (final String name : names) {
                            reply.replace("%place" + index + '%', String.valueOf(index));
                            reply.replace("%rank" + index + '%', name);
                            index++;
                            if (index > 10) {
                                break;
                            }
                        }
                        for (; index <= 10; index++) {
                            reply.replace("%place" + index + '%', "");
                            reply.replace("%rank" + index + '%', "");
                        }

                        player.sendPacket(reply);
                    }
                    // TODO Send player each class rank
                    break;
                default:
                    _log.warn("Olympiad System: Couldnt send packet for request " + val);
                    break;
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public void showChatWindow(final Player player, final int val, final Object... arg) {
        if (checkForDominionWard(player)) {
            return;
        }

        String fileName = Olympiad.OLYMPIAD_HTML_PATH;
        final int npcId = getNpcId();
        switch (npcId) {
            case 31688: // Grand Olympiad Manager
                fileName += "manager";
                break;
            default: // Monument of Heroes
                fileName += "monument";
                break;
        }
        if (player.isNoble()) {
            fileName += "_n";
        }
        if (val > 0) {
            fileName += "-" + val;
        }
        fileName += ".htm";
        player.sendPacket(new HtmlMessage(this, fileName, val));
    }
}