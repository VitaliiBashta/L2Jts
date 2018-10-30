package org.mmocore.gameserver.listener.actor.player.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.text.StrTable;
import org.mmocore.gameserver.cache.ItemInfoCache;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.EventsConfig;
import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.configuration.config.chatFilter.ChatFilterConfig;
import org.mmocore.gameserver.configuration.config.custom.CustomConfig;
import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.handler.voicecommands.VoicedCommandHandler;
import org.mmocore.gameserver.listener.actor.player.OnPlayerChatListener;
import org.mmocore.gameserver.manager.PetitionManager;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.olympiad.OlympiadGame;
import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ActionFail;
import org.mmocore.gameserver.network.lineage.serverpackets.Say2;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.bot_punish.BotPunish.Punish;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.phantoms.action.ChatAnswerAction;
import org.mmocore.gameserver.phantoms.model.Phantom;
import org.mmocore.gameserver.scripts.events.LastHero.LastHero;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Language;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.utils.Strings;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.mmocore.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author KilRoy
 */
public class PlayerChatListener implements OnPlayerChatListener {
    /**
     * RegExp для кэширования ссылок на предметы, пример ссылки: \b\tType=1 \tID=268484598 \tColor=0 \tUnderline=0 \tTitle=\u001BAdena\u001B\b
     */
    public static final Pattern EX_ITEM_LINK_PATTERN = Pattern.compile("[\b]\tType=[0-9]+[\\s]+\tID=([0-9]+)[\\s]+\tColor=[0-9]+[\\s]+\tUnderline=[0-9]+[\\s]+\tTitle=\u001B(.[^\u001B]*)[^\b]");
    public static final Pattern SKIP_ITEM_LINK_PATTERN = Pattern.compile("[\b]\tType=[0-9]+(.[^\b]*)[\b]");
    private static final Logger _log = LoggerFactory.getLogger(PlayerChatListener.class);

    @Override
    public void onPlayerChat(Player activeChar, String text, ChatType chatType, String target) {
        if (ExtConfig.EX_ENABLE_AUTO_HUNTING_REPORT && activeChar.getBotPunishComponent().isBeingPunished()) {
            if (activeChar.getBotPunishComponent().getPlayerPunish().canTalk() && activeChar.getBotPunishComponent().getBotPunishType() == Punish.CHATBAN) {
                activeChar.getBotPunishComponent().endPunishment();
            } else {
                activeChar.sendPacket(SystemMsg.YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_CHATTING_IS_NOT_ALLOWED);
                return;
            }
        }

        if (chatType == null || text == null || text.isEmpty()) {
            activeChar.sendActionFailed();
            return;
        }

        text = text.replace("\\\\n", "");
        text = text.replace("\n", "");

		/* Это блять што?
		text = text.replaceAll("\\\\n", "\n");

		if(text.contains("\n"))
		{
			final String[] lines = text.split("\n");
			text = StringUtils.EMPTY;
			for(int i = 0; i < lines.length; i++)
			{
				lines[i] = lines[i].trim();
				if(lines[i].isEmpty())
				{
					continue;
				}
				if(!text.isEmpty())
				{
					text += "\n  >";
				}
				text += lines[i];
			}
		}
		*/

        if (text.isEmpty()) {
            activeChar.sendActionFailed();
            return;
        }

        text = StrTable.format(text);
        if (AllSettingsConfig.ALT_ENABLE_ADDITIONAL_CHAR_STRING) {
            final String commands = text;
            if (text.startsWith(".") && ArrayUtils.contains(AllSettingsConfig.ALT_ADDITIONAL_CHAR_STRING_LIST, commands.substring(0, commands.split(" ")[0].length()))) {
                final String fullcmd = text.substring(1).trim();
                final String command = fullcmd.split("\\s+")[0];
                final String args = fullcmd.substring(command.length()).trim();

                if (!command.isEmpty()) {
                    // then check for VoicedCommands
                    final IVoicedCommandHandler vch = VoicedCommandHandler.getInstance().getVoicedCommandHandler(command);
                    if (vch != null) {
                        vch.useVoicedCommand(command, activeChar, args);
                        return;
                    }
                }
                activeChar.sendMessage(new CustomMessage("common.command404"));
                return;
            }
        }

        if (ServerConfig.CHATFILTER_MIN_LEVEL > 0 && ArrayUtils.contains(ServerConfig.CHATFILTER_CHANNELS, chatType.ordinal()) && activeChar.getLevel() < ServerConfig.CHATFILTER_MIN_LEVEL) {
            if (ServerConfig.CHATFILTER_WORK_TYPE == 1) {
                chatType = ChatType.ALL;
            } else if (ServerConfig.CHATFILTER_WORK_TYPE == 2) {
                if (ServerConfig.CHATFILTER_MIN_LEVEL == 20 && !activeChar.getPremiumAccountComponent().hasBonus()) // По офу О_о
                {
                    activeChar.sendPacket(SystemMsg.SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_TRIAL_ACCOUNT_HAVE_LIMITED_CHATTING_CAPABILITIES_TO_UNLOCK_ALL_OF_THE_FEATURES_OF_LINEAGE_II_PURCHASE_THE_FULL_VERSION_TODAY);
                    activeChar.sendPacket(SystemMsg.FREE_ACOUNTS_BELOW_LEVEL_20_CANNOT_SHOUT_OR_ENGAGE_IN_BUY_SELL_CHATTING);
                } else
                    activeChar.sendMessage(new CustomMessage("chat.NotHavePermission").addNumber(ServerConfig.CHATFILTER_MIN_LEVEL));
                return;
            }
        }

        final boolean globalchat = chatType != ChatType.ALLIANCE && chatType != ChatType.CLAN && chatType != ChatType.PARTY;

        if ((globalchat || ArrayUtils.contains(ServerConfig.BAN_CHANNEL_LIST, chatType.ordinal())) && activeChar.getNoChannel() != 0) {
            if (activeChar.getNoChannelRemained() > 0 || activeChar.getNoChannel() < 0) {
                if (activeChar.getNoChannel() > 0) {
                    final int timeRemained = Math.round(activeChar.getNoChannelRemained() / 60000F);
                    activeChar.sendMessage(new CustomMessage("common.ChatBanned").addNumber(timeRemained));
                } else {
                    activeChar.sendMessage(new CustomMessage("common.ChatBannedPermanently"));
                }
                activeChar.sendActionFailed();
                return;
            }
            activeChar.updateNoChannel(0);
        }

        if (globalchat) {
            if (ServerConfig.ABUSEWORD_REPLACE) {
                if (ChatFilterConfig.containsAbuseWord(text)) {
                    text = ServerConfig.ABUSEWORD_REPLACE_STRING;
                    activeChar.sendActionFailed();
                }
            } else if (ServerConfig.ABUSEWORD_BANCHAT && ChatFilterConfig.containsAbuseWord(text)) {
                activeChar.sendMessage(new CustomMessage("common.ChatBanned").addNumber(ServerConfig.ABUSEWORD_BANTIME * 60));
                Log.add(activeChar + ": " + text, "abuse");
                activeChar.updateNoChannel(ServerConfig.ABUSEWORD_BANTIME * 60000L);
                activeChar.sendActionFailed();
                return;
            }
        }

        // Кэширование линков предметов
        Matcher m = EX_ITEM_LINK_PATTERN.matcher(text);
        ItemInstance item;
        int objectId;
        Language lang = null;

        while (m.find()) {
            objectId = Integer.parseInt(m.group(1));
            item = activeChar.getInventory().getItemByObjectId(objectId);

            if (item == null) {
                activeChar.sendActionFailed();
                break;
            }

            lang = activeChar.getLanguage();
            ItemInfoCache.getInstance().put(item);
        }

        final String translit = activeChar.getPlayerVariables().get(PlayerVariables.TRANSLIT);
        if (translit != null) {
            //Исключаем из транслитерации ссылки на предметы
            m = SKIP_ITEM_LINK_PATTERN.matcher(text);
            final StringBuilder sb = new StringBuilder();
            int end = 0;
            while (m.find()) {
                sb.append(Strings.fromTranslit(text.substring(end, end = m.start()), translit.equals("tl") ? 1 : 2));
                sb.append(text.substring(end, end = m.end()));
            }

            text = sb.append(Strings.fromTranslit(text.substring(end, text.length()), translit.equals("tl") ? 1 : 2)).toString();
        }

        Log.chat(chatType.name(), activeChar.getName(), target, text);

        activeChar.getListeners().onSay(chatType, target, text);

        String publicName = activeChar.getName();
        if (activeChar.isInLastHero() && !EventsConfig.LhCustomName.isEmpty())
            publicName = EventsConfig.LhCustomName;
        Say2 cs = new Say2(activeChar.getObjectId(), chatType, publicName, text, lang);

        switch (chatType) {
            case TELL:
                final Player receiver = World.getPlayer(target);
                if (receiver != null && receiver.isInOfflineMode()) {
                    activeChar.sendPacket(new SystemMessage(SystemMsg.S1_IS_NOT_CURRENTLY_LOGGED_IN).addString(target));
                    activeChar.sendActionFailed();
                } else if (receiver != null && !receiver.isInBlockList(activeChar) && !receiver.isBlockAll()) {
                    if (!receiver.getMessageRefusal()) {
                        if (activeChar.antiFlood.canTell(receiver.getObjectId(), text)) {
                            receiver.sendPacket(cs);
                        }

                        String receiverPublicName = receiver.getName();
                        if (activeChar.isInLastHero() && !EventsConfig.LhCustomName.isEmpty())
                            receiverPublicName = EventsConfig.LhCustomName;
                        cs = new Say2(activeChar.getObjectId(), chatType, "->" + receiverPublicName, text, lang);
                        activeChar.sendPacket(cs);
                        if (receiver.isPhantom()) {
                            ((Phantom) receiver).doAction(new ChatAnswerAction(activeChar));
                        }
                    } else {
                        activeChar.sendPacket(SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE);
                    }
                } else if (receiver == null || (receiver.getPlayerAccess().GodMode && receiver.isInvisible())) {
                    activeChar.sendPacket(new SystemMessage(SystemMsg.S1_IS_NOT_CURRENTLY_LOGGED_IN).addString(target), ActionFail.STATIC);
                } else {
                    activeChar.sendPacket(SystemMsg.YOU_HAVE_BEEN_BLOCKED_FROM_CHATTING_WITH_THAT_CONTACT, ActionFail.STATIC);
                }
                break;
            case SHOUT:
                if (CustomConfig.subscriptionAllow && CustomConfig.blockСhatShout && !activeChar.isGM() && !activeChar.getCustomPlayerComponent().isSubscriptionActive()) {
                    return;
                }
                if (activeChar.isCursedWeaponEquipped()) {
                    activeChar.sendPacket(SystemMsg.SHOUT_AND_TRADE_CHATTING_CANNOT_BE_USED_WHILE_POSSESSING_A_CURSED_WEAPON);
                    return;
                }
                if (activeChar.isInObserverMode()) {
                    activeChar.sendPacket(SystemMsg.YOU_CANNOT_CHAT_WHILE_IN_OBSERVATION_MODE);
                    return;
                }

                if (!activeChar.isGM() && !activeChar.antiFlood.canShout(text)) {
                    activeChar.sendPacket(SystemMsg.CHATTING_IS_CURRENTLY_PROHIBITED_);
                    return;
                }

                if (ServerConfig.GLOBAL_SHOUT) {
                    ChatUtils.announce(activeChar, cs);
                } else {
                    ChatUtils.shout(activeChar, cs);
                }

                activeChar.sendPacket(cs);
                break;
            case TRADE:
                if (CustomConfig.subscriptionAllow && CustomConfig.blockСhatTrade && !activeChar.isGM() && !activeChar.getCustomPlayerComponent().isSubscriptionActive()) {
                    return;
                }
                if (activeChar.isCursedWeaponEquipped()) {
                    activeChar.sendPacket(SystemMsg.SHOUT_AND_TRADE_CHATTING_CANNOT_BE_USED_WHILE_POSSESSING_A_CURSED_WEAPON);
                    return;
                }
                if (activeChar.isInObserverMode()) {
                    activeChar.sendPacket(SystemMsg.YOU_CANNOT_CHAT_WHILE_IN_OBSERVATION_MODE);
                    return;
                }

                if (!activeChar.isGM() && !activeChar.antiFlood.canTrade(text)) {
                    activeChar.sendPacket(SystemMsg.CHATTING_IS_CURRENTLY_PROHIBITED_);
                    return;
                }

                if (ServerConfig.GLOBAL_TRADE_CHAT) {
                    ChatUtils.announce(activeChar, cs);
                } else {
                    ChatUtils.shout(activeChar, cs);
                }

                activeChar.sendPacket(cs);
                break;
            case ALL:
                if (activeChar.isInObserverMode()) {
                    activeChar.sendPacket(SystemMsg.YOU_CANNOT_CHAT_WHILE_IN_OBSERVATION_MODE);
                    return;
                }

                if (activeChar.isTransformed() && activeChar.isCursedWeaponEquipped()) {
                    cs = new Say2(activeChar.getObjectId(), chatType, activeChar.getTransformation().getNameTransform(), text, lang);
                }

                if (activeChar.isInOlympiadMode()) {
                    final OlympiadGame game = activeChar.getOlympiadGame();
                    if (game != null) {
                        ChatUtils.say(activeChar, game.getAllPlayers(), cs);
                        break;
                    }
                }

                ChatUtils.say(activeChar, cs);
                activeChar.sendPacket(cs);
                break;
            case CLAN:
                if (activeChar.getClan() != null) {
                    activeChar.getClan().broadcastToOnlineMembers(cs);
                }
                break;
            case ALLIANCE:
                if (activeChar.getClan() != null && activeChar.getClan().getAlliance() != null) {
                    activeChar.getClan().getAlliance().broadcastToOnlineMembers(cs);
                }
                break;
            case PARTY:
                if (activeChar.isInParty()) {
                    activeChar.getParty().broadCast(cs);
                }
                break;
            case PARTY_ROOM:
                final MatchingRoom r = activeChar.getMatchingRoom();
                if (r != null && r.getType() == MatchingRoom.PARTY_MATCHING) {
                    r.broadCast(cs);
                }
                break;
            case COMMANDCHANNEL_ALL:
                if (!activeChar.isInParty() || !activeChar.getParty().isInCommandChannel()) {
                    activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
                    return;
                }
                if (activeChar.getParty().getCommandChannel().getGroupLeader() == activeChar) {
                    activeChar.getParty().getCommandChannel().broadCast(cs);
                } else {
                    activeChar.sendPacket(SystemMsg.ONLY_THE_COMMAND_CHANNEL_CREATOR_CAN_USE_THE_RAID_LEADER_TEXT);
                }
                break;
            case COMMANDCHANNEL_COMMANDER:
                if (!activeChar.isInParty() || !activeChar.getParty().isInCommandChannel()) {
                    activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
                    return;
                }
                if (activeChar.getParty().isLeader(activeChar)) {
                    activeChar.getParty().getCommandChannel().broadcastToChannelPartyLeaders(cs);
                } else {
                    activeChar.sendPacket(SystemMsg.ONLY_A_PARTY_LEADER_CAN_ACCESS_THE_COMMAND_CHANNEL);
                }
                break;
            case HERO_VOICE:
                boolean premiumHeroChat = false;
                if (ServerConfig.PREMIUM_HEROCHAT && activeChar.getPremiumAccountComponent().hasBonus()) {
                    premiumHeroChat = true;
                }

                if (activeChar.isHero()
                        || activeChar.getCustomPlayerComponent().isTemporalHero()
                        || activeChar.getPlayerAccess().CanAnnounce
                        || premiumHeroChat) {
                    if (activeChar.getCustomPlayerComponent().isTemporalHero()
                            && LastHero.isWinner(activeChar) && !EventsConfig.LhAllowHeroChatForWinner)
                        return;
                    // Ограничение только для героев, гм-мы пускай говорят.
                    if (!activeChar.getPlayerAccess().CanAnnounce) {
                        if (!activeChar.antiFlood.canHero(text)) {
                            activeChar.sendPacket(SystemMsg.CHATTING_IS_CURRENTLY_PROHIBITED_);
                            return;
                        }
                    }
                    for (final Player player : GameObjectsStorage.getPlayers()) {
                        if (!player.isInBlockList(activeChar) && !player.isBlockAll()) {
                            player.sendPacket(cs);
                        }
                    }
                }
                break;
            case PETITION_PLAYER:
            case PETITION_GM:
                if (!PetitionManager.getInstance().isPlayerInConsultation(activeChar)) {
                    activeChar.sendPacket(SystemMsg.YOU_ARE_CURRENTLY_NOT_IN_A_PETITION_CHAT);
                    return;
                }

                PetitionManager.getInstance().sendActivePetitionMessage(activeChar, text);
                break;
            case BATTLEFIELD:
                final DominionSiegeEvent siegeEvent = activeChar.getEvent(DominionSiegeEvent.class);
                if (siegeEvent == null) {
                    return;
                }

                for (final Player player : GameObjectsStorage.getPlayers()) {
                    if (!player.isInBlockList(activeChar) && !player.isBlockAll() && player.getEvent(DominionSiegeEvent.class) == siegeEvent) {
                        player.sendPacket(cs);
                    }
                }
                break;
            case MPCC_ROOM:
                final MatchingRoom r2 = activeChar.getMatchingRoom();
                if (r2 != null && r2.getType() == MatchingRoom.CC_MATCHING) {
                    r2.broadCast(cs);
                }
                break;
            default:
                _log.warn("Character " + activeChar.getName() + " used unknown chat type: " + chatType.ordinal() + '.');
        }
    }
}