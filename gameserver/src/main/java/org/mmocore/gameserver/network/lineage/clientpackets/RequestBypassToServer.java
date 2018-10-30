package org.mmocore.gameserver.network.lineage.clientpackets;

import org.apache.commons.lang3.tuple.Pair;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.OlympiadConfig;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.handler.admincommands.AdminCommandHandler;
import org.mmocore.gameserver.handler.bbs.BbsHandlerHolder;
import org.mmocore.gameserver.handler.bbs.IBbsHandler;
import org.mmocore.gameserver.handler.bypass.BypassHolder;
import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.handler.voicecommands.VoicedCommandHandler;
import org.mmocore.gameserver.manager.OlympiadHistoryManager;
import org.mmocore.gameserver.model.entity.Hero;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.instances.OlympiadManagerInstance;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.BypassStorage.ValidBypass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.StringTokenizer;

public class RequestBypassToServer extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(RequestBypassToServer.class);

    private String multiBypass = null;

    private static void actionShowHtml(final Player activeChar, final String path) {
        final HtmlMessage html = new HtmlMessage(5);
        html.setFile(path);
        activeChar.sendPacket(html);
    }

    @Override
    protected void readImpl() {
        multiBypass = readS();
    }

    @Override
    protected void runImpl() {
        String[] bypasses = multiBypass.split("#");
        if (bypasses.length > 3) { //TODO[Hack]: заслуживает конфига
            _log.warn("Client " + getClient() + "tries to use multi-bypass with too much actions.");
            return;
        }
        for (String bypass : bypasses)
            manageBypass(bypass, bypasses.length);
    }

    private void manageBypass(String _bypass, int multiSize) {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null || _bypass.isEmpty()) {
            return;
        }

        NpcInstance npc = activeChar.getLastNpc();
        final GameObject target = activeChar.getTarget();
        if (npc == null && target != null && target.isNpc()) {
            npc = (NpcInstance) target;
        }

        final ValidBypass bp = activeChar.getBypassStorage().validate(_bypass, multiSize);
        if (bp == null) {
            _log.debug("RequestBypassToServer: Unexpected bypass : " + _bypass + " client : " + getClient() + '!');
            return;
        }
        try {
            if (bp.bypass.startsWith("admin_")) {
                AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, bp.bypass);
            } else if (bp.bypass.startsWith("scripts_")) {
                _log.error("Trying to call script bypass: " + bp.bypass + ' ' + activeChar);
            } else if (bp.bypass.startsWith("htmbypass_")) {
                final String command = bp.bypass.substring(10).trim();
                final String word = command.split("\\s+")[0];
                final Pair<Object, Method> b = BypassHolder.getInstance().getBypass(word);
                if (b != null) {
                    b.getValue().invoke(b.getKey(), activeChar, npc, command.substring(word.length()).trim().split("\\s+"));
                }
            } else if (bp.bypass.startsWith("user_")) {
                final String command = bp.bypass.substring(5).trim();
                final String word = command.split("\\s+")[0];
                final String args = command.substring(word.length()).trim();
                final IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(word);
                if (vch != null) {
                    vch.useVoicedCommand(word, activeChar, args);
                } else {
                    _log.warn("Unknow voiced command '" + word + '\'');
                }
            } else if (bp.bypass.equals("teleport_request")) {
                if (!AllSettingsConfig.ALLOW_TALK_WHILE_SITTING && activeChar.isSitting() || activeChar.isAlikeDead()) {
                    activeChar.sendActionFailed();
                    return;
                }

                if (npc != null && activeChar.isInRange(npc, activeChar.getInteractDistance(npc))) {
                    npc.getAI().onTeleportRequested(activeChar);
                }
            } else if (bp.bypass.startsWith("npc_")) {
                final int endOfId = bp.bypass.indexOf('_', 5);
                final String id;
                if (endOfId > 0) {
                    id = bp.bypass.substring(4, endOfId);
                } else {
                    id = bp.bypass.substring(4);
                }
                final GameObject object = activeChar.getVisibleObject(Integer.parseInt(id));
                if (object != null && object.isNpc() && endOfId > 0 && activeChar.isInRangeZ(object.getLoc(), activeChar.getInteractDistance(object))) {
                    activeChar.setLastNpc((NpcInstance) object);
                    ((NpcInstance) object).onBypassFeedback(activeChar, bp.bypass.substring(endOfId + 1));
                }
            } else if (bp.bypass.startsWith("_olympiad?")) {
                final String[] ar = bp.bypass.replace("_olympiad?", "").split("&");
                final String firstVal = ar[0].split("=")[1];
                final String secondVal = ar[1].split("=")[1];

                if ("move_op_field".equalsIgnoreCase(firstVal)) {
                    if (!OlympiadConfig.ENABLE_OLYMPIAD_SPECTATING) {
                        return;
                    }

                    // Переход в просмотр олимпа разрешен только от менеджера или с арены.
                    if ((npc instanceof OlympiadManagerInstance && npc.isInRangeZ(activeChar, npc.getInteractDistance(activeChar))) || activeChar.getOlympiadObserveGame() != null) {
                        Olympiad.addSpectator(Integer.parseInt(secondVal) - 1, activeChar);
                    }
                }
            } else if (bp.bypass.startsWith("_diary")) {
                final String params = bp.bypass.substring(bp.bypass.indexOf('?') + 1);
                final StringTokenizer st = new StringTokenizer(params, "&");
                final int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
                final int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
                final int heroid = Hero.getInstance().getHeroByClass(heroclass);
                if (heroid > 0) {
                    Hero.getInstance().showHeroDiary(activeChar, heroclass, heroid, heropage);
                }
            } else if (bp.bypass.startsWith("_match")) {
                final String params = bp.bypass.substring(bp.bypass.indexOf('?') + 1);
                final StringTokenizer st = new StringTokenizer(params, "&");
                final int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
                final int heropage = Integer.parseInt(st.nextToken().split("=")[1]);

                OlympiadHistoryManager.getInstance().showHistory(activeChar, heroclass, heropage);
            } else if (bp.bypass.startsWith("manor_menu_select?")) // Navigate throught Manor windows
            {
                final GameObject object = activeChar.getTarget();
                if (object != null && object.isNpc()) {
                    ((NpcInstance) object).onBypassFeedback(activeChar, bp.bypass);
                }
            } else if (bp.bypass.startsWith("menu_select?")) {
                if (activeChar.isInRangeZ(npc, activeChar.getInteractDistance(npc))) {
                    final String params = bp.bypass.substring(bp.bypass.indexOf('?') + 1);
                    final StringTokenizer st = new StringTokenizer(params, "&");
                    final int ask = Integer.parseInt(st.nextToken().split("=")[1]);
                    final int reply = Integer.parseInt(st.nextToken().split("=")[1]);
                    if (npc != null)
                        npc.onMenuSelect(activeChar, ask, reply);
                }
            } else if (bp.bypass.startsWith("talk_select")) {
                final String quest = bp.bypass.substring(11).trim();
                if (quest.isEmpty()) {
                    if (activeChar.isInRangeZ(npc, activeChar.getInteractDistance(npc))) {
                        npc.showQuestWindow(activeChar);
                    }
                } else {
                    try {
                        final StringTokenizer tokenizer = new StringTokenizer(bp.bypass);
                        tokenizer.nextToken();

                        final String questName = tokenizer.nextToken();
                        final int questId = Integer.parseInt(questName.split("_")[1]);

                        activeChar.processQuestEvent(questId, bp.bypass.substring(13 + questName.length()), null);
                    } catch (final Exception e) {
                        _log.error("Bypass: " + bp.bypass + " not such element. Player : " + activeChar.getName() + " used this bypass, check him!", e);
                    }
                }
            } else if (bp.bypass.startsWith("action_show_html ")) {
                actionShowHtml(activeChar, bp.bypass.substring(17));
            } else if (bp.bypass.startsWith("link ")) {
                actionShowHtml(activeChar, "pts/default/" + bp.bypass.substring(5));
            } else if (bp.bypass.startsWith("multisell ")) {
                MultiSellHolder.getInstance().SeparateAndSend(Integer.parseInt(bp.bypass.substring(10)), activeChar, npc != null ? npc.getObjectId() : -1, 0);
            } else if (bp.bypass.startsWith("Quest ")) {
				/*String p = bp.bypass.substring(6).trim();
				int idx = p.indexOf(' ');
				if(idx < 0)
					activeChar.processQuestEvent(Integer.parseInt(p.split("_")[1]), StringUtils.EMPTY, npc);
				else
					activeChar.processQuestEvent(Integer.parseInt(p.substring(0, idx).split("_")[1]), p.substring(idx).trim(), npc); */
                _log.warn("Trying to call Quest bypass: " + bp.bypass + ", player: " + activeChar);
            } else if (bp.bbs) {
                if (!CBasicConfig.COMMUNITYBOARD_ENABLED) {
                    activeChar.sendPacket(SystemMsg.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE);
                } else {
                    final Optional<IBbsHandler> handler = BbsHandlerHolder.getInstance().getCommunityHandler(bp.bypass);
                    if (handler.isPresent()) {
                        //if(!activeChar.getEvents().isEmpty())
                        //activeChar.sendPacket(SystemMsg.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE);
                        handler.get().onBypassCommand(activeChar, bp.bypass);
                    }
                }
            }
        } catch (Exception e) {
            String st = "Error while handling bypass: " + bp.bypass;
            if (npc != null) {
                st = st + " via NPC " + npc;
            }

            _log.error(st, e);
        }

    }
}