package org.mmocore.gameserver.scripts.npc.model.pts.aerial_cleft;

import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.manager.SoDManager;
import org.mmocore.gameserver.manager.SoIManager;
import org.mmocore.gameserver.manager.games.HandysBlockCheckerManager;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.AerialCleftEvent;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author KilRoy
 * Ai: ai_cleft_gate
 * TODO[K] - доделать возможность входа в уже активный ивент(и просчитывать время плеера в ивенте, если < 15мин -> награду не дают)
 */
public final class CleftGatesInstance extends NpcInstance {
    private static final String fnNoEnter = "pts/gracia/aerial_cleft/cleft_inner_gate002.htm";
    private static final String fnInfo = "pts/gracia/aerial_cleft/cleft_inner_gate003.htm";
    private static final String fnNotEnoughLevel = "pts/gracia/aerial_cleft/cleft_inner_gate004.htm";
    private static final String fnNotActivated = "pts/gracia/aerial_cleft/cleft_inner_gate005.htm";
    private static final String fnTimeLimit = "pts/gracia/aerial_cleft/cleft_inner_gate006.htm";

    public CleftGatesInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        if (player.getLevel() < 75) {
            showChatWindow(player, fnNotEnoughLevel);
            return;
        }
        switch (getNpcId()) {
            case 32518:
                showChatWindow(player, "pts/gracia/aerial_cleft/cleft_outer_gate001.htm");
                break;
            case 32519:
                showChatWindow(player, "pts/gracia/aerial_cleft/cleft_inner_gate001.htm");
                break;
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.startsWith("menu_select?ask=-200&")) {
            if (command.endsWith("reply=1")) {
                // В АИ данный реплей тупо убит, походу использовался для отладки.
            } else if (command.endsWith("reply=2")) {
                if (Olympiad.isRegistered(player) || HandysBlockCheckerManager.isRegistered(player)) {
                    player.sendPacket(SystemMsg.YOU_CANNOT_BE_SIMULTANEOUSLY_REGISTERED_FOR_PVP_MATCHES_SUCH_AS_THE_OLYMPIAD_UNDERGROUND_COLISEUM_AERIAL_CLEFT_KRATEIS_CUBE_AND_HANDYS_BLOCK_CHECKERS);
                    return;
                }
                if (player.isCursedWeaponEquipped()) {
                    player.sendPacket(SystemMsg.YOU_CANNOT_REGISTER_WHILE_IN_POSSESSION_OF_A_CURSED_WEAPON);
                    return;
                }
                if (SoDManager.isOpened() || SoIManager.isSeedOpen()) {
                    final AerialCleftEvent aerialCleftEvent = EventHolder.getInstance().getEvent(EventType.PVP_EVENT, 10);
                    if (aerialCleftEvent == null) {
                        return;
                    }

                    if (aerialCleftEvent.isInProgress() || aerialCleftEvent.isRegistrationOver()) {
                        showChatWindow(player, fnTimeLimit);
                        return;
                    }

                    if (aerialCleftEvent.registerPlayer(player) == 18) {
                        aerialCleftEvent.startEvent();
                    }
                } else {
                    showChatWindow(player, fnNotActivated);
                    return;
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}