package org.mmocore.gameserver.handler.voicecommands.impl;

import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.handler.voicecommands.VoicedCommandHandler;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.skillclasses.Call;

import java.util.List;

public class Relocate implements IVoicedCommandHandler {

    private final String[] _commandList = {"km-all-to-me"};

    public String[] getVoicedCommandList() {
        return _commandList;
    }

    public boolean useVoicedCommand(final String command, final Player activeChar, final String target) {
        if ("km-all-to-me".equalsIgnoreCase(command)) {
            if (!activeChar.isClanLeader()) {
                activeChar.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
                return false;
            }

            final IBroadcastPacket msg = Call.canSummonHere(activeChar);
            if (msg != null) {
                activeChar.sendPacket(msg);
                return false;
            }

            if (activeChar.isAlikeDead()) {
                activeChar.sendMessage("Вы не можете использовать эту команду мертвым.");
                return false;
            }

            if (activeChar.getInventory().destroyItemByItemId(OtherConfig.SUMMON_PRICE_ID, OtherConfig.SUMMON_PRICE_CL)) {
                final List<Player> clan = activeChar.getClan().getOnlineMembers(activeChar.getObjectId());
                for (final Player pl : clan) {
                    if (Call.canBeSummoned(pl) == null)
                    // Спрашиваем, согласие на призыв
                    {
                        pl.summonCharacterRequest(activeChar, OtherConfig.SUMMON_PRICE_ID, OtherConfig.SUMMON_PRICE);
                    }
                }

                return true;
            } else {
                activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            }
        }
        return false;
    }

    public void onLoad() {
        VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
    }
}