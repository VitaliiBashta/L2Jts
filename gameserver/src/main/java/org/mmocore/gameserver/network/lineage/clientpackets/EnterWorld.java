package org.mmocore.gameserver.network.lineage.clientpackets;

import org.apache.commons.lang3.tuple.Pair;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.ExtConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.StringHolder;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.database.dao.impl.CharacterQuestDAO;
import org.mmocore.gameserver.database.dao.impl.MailDAO;
import org.mmocore.gameserver.listener.actor.player.OnAnswerListener;
import org.mmocore.gameserver.listener.actor.player.impl.PlayerChatListener;
import org.mmocore.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import org.mmocore.gameserver.listener.actor.player.impl.SevenSignListener;
import org.mmocore.gameserver.manager.*;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.base.InvisibleType;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.entity.events.impl.ClanHallAuctionEvent;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.mail.Mail;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.SubUnit;
import org.mmocore.gameserver.model.pledge.UnitMember;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.RequestPlayerGamePoint;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.network.lineage.serverpackets.ConfirmDlg;
import org.mmocore.gameserver.network.lineage.serverpackets.GoodsInventory.ExGoodsInventoryChangedNotify;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.TeleportToLocation;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.skills.AbnormalEffect;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.*;
import org.mmocore.gameserver.world.World;

import java.util.Calendar;

public class EnterWorld extends L2GameClientPacket {
    private static final PlayerChatListener chatListener = new PlayerChatListener();
    private static final SevenSignListener sevenSignListener = new SevenSignListener();

    private static void notifyClanMembers(final Player activeChar) {
        final Clan clan = activeChar.getClan();
        final SubUnit subUnit = activeChar.getSubUnit();
        if (clan == null || subUnit == null) {
            return;
        }

        final UnitMember member = subUnit.getUnitMember(activeChar.getObjectId());
        if (member == null) {
            return;
        }

        member.setPlayerInstance(activeChar, false);

        final int sponsor = activeChar.getSponsor();
        final int apprentice = activeChar.getApprentice();
        final L2GameServerPacket msg = new SystemMessage(SystemMsg.CLAN_MEMBER_S1_HAS_LOGGED_INTO_GAME).addName(activeChar);
        final PledgeShowMemberListUpdate memberUpdate = new PledgeShowMemberListUpdate(activeChar);
        for (final Player clanMember : clan.getOnlineMembers(activeChar.getObjectId())) {
            clanMember.sendPacket(memberUpdate);
            if (clanMember.getObjectId() == sponsor) {
                clanMember.sendPacket(new SystemMessage(SystemMsg.YOUR_APPRENTICE_C1_HAS_LOGGED_OUT).addName(activeChar));
            } else if (clanMember.getObjectId() == apprentice) {
                clanMember.sendPacket(new SystemMessage(SystemMsg.YOUR_SPONSOR_C1_HAS_LOGGED_IN).addName(activeChar));
            } else {
                clanMember.sendPacket(msg);
            }
        }

        if (!activeChar.isClanLeader()) {
            return;
        }

        final ClanHall clanHall = clan.getHasHideout() > 0 ? ResidenceHolder.getInstance().getResidence(ClanHall.class, clan.getHasHideout()) : null;
        if (clanHall == null || clanHall.getAuctionLength() != 0) {
            return;
        }

        if (clanHall.getSiegeEvent().getClass() != ClanHallAuctionEvent.class) {
            return;
        }

        if (clan.getWarehouse().getCountOf(ItemTemplate.ITEM_ID_ADENA) < clanHall.getRentalFee()) {
            activeChar.sendPacket(new SystemMessage(SystemMsg.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_ME_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW).addNumber(clanHall.getRentalFee()));
        }
    }

    @Override
    protected void readImpl() {
        //readS(); - клиент всегда отправляет строку "narcasse"
    }

    @Override
    protected void runImpl() {
        final GameClient client = getClient();
        final Player activeChar = client.getActiveChar();

        if (activeChar == null) {
            client.closeNow(false);
            return;
        }

        GameStats.incrementPlayerEnterGame();
        Log.auth(activeChar);
        activeChar.setEnterHwid(client.getHWID());

        final boolean first = activeChar.entering;
        GameMasterService(first, activeChar);
        if (first) {
            activeChar.setOnlineStatus(true);
            if (activeChar.isLogoutStarted()) {
                client.closeNow(false);
                return;
            }
            activeChar.setNonAggroTime(Long.MAX_VALUE);
            activeChar.spawnMe();

            if (activeChar.isInStoreMode()) {
                if (!TradeHelper.checksIfCanOpenStore(activeChar, activeChar.getPrivateStoreType())) {
                    activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
                    activeChar.standUp();
                    activeChar.broadcastCharInfo();
                }
            }

            activeChar.setRunning();
            activeChar.standUp();
            if (!activeChar.isLoadingAfterCarrier())
                activeChar.startTimers();
        }

        activeChar.sendPacket(new ExBR_PremiumState(activeChar, activeChar.getPremiumAccountComponent().hasBonus()));

        CharacterQuestDAO.getInstance().select(activeChar);
        activeChar.getMacroComponent().sendUpdate();
        activeChar.sendPacket(new SSQInfo(), new HennaInfo(activeChar), new ExGetBookMarkInfo(activeChar));
        activeChar.sendItemList(false);
        activeChar.sendPacket(new ShortCutInit(activeChar), new SkillList(activeChar), new SkillCoolTime(activeChar));
        activeChar.sendPacket(SystemMsg.WELCOME_TO_THE_WORLD_OF_LINEAGE_II);

        AnnouncementUtils.showAnnouncements(activeChar);

        if (first) {
            activeChar.getListeners().onEnter();
        }
        activeChar.addListener(chatListener);
        activeChar.addListener(sevenSignListener);
        SevenSigns.getInstance().sendCurrentPeriodMsg(activeChar);

        if (first && activeChar.getCreateTime() > 0) {
            final Calendar create = Calendar.getInstance();
            create.setTimeInMillis(activeChar.getCreateTime());
            final Calendar now = Calendar.getInstance();

            int day = create.get(Calendar.DAY_OF_MONTH);
            if (create.get(Calendar.MONTH) == Calendar.FEBRUARY && day == 29) {
                day = 28;
            }

            final int myBirthdayReceiveYear = activeChar.getPlayerVariables().getInt(PlayerVariables.MY_BIRTHDAY_RECEIVE_YEAR, 0);
            if (create.get(Calendar.MONTH) == now.get(Calendar.MONTH) && create.get(Calendar.DAY_OF_MONTH) == day) {
                if ((myBirthdayReceiveYear == 0 && create.get(Calendar.YEAR) != now.get(Calendar.YEAR)) || myBirthdayReceiveYear > 0 && myBirthdayReceiveYear != now.get(Calendar.YEAR)) {
                    final Mail mail = new Mail();
                    mail.setSenderId(1);
                    mail.setSenderName(StringHolder.getInstance().getString(activeChar, "birthday.npc"));
                    mail.setReceiverId(activeChar.getObjectId());
                    mail.setReceiverName(activeChar.getName());
                    mail.setTopic(StringHolder.getInstance().getString(activeChar, "birthday.title"));
                    mail.setBody(StringHolder.getInstance().getString(activeChar, "birthday.text"));

                    final ItemInstance item = ItemFunctions.createItem(21169);
                    item.setLocation(ItemInstance.ItemLocation.MAIL);
                    item.setCount(1L);
                    item.save();

                    mail.addAttachment(item);
                    mail.setUnread(true);
                    mail.setType(Mail.SenderType.BIRTHDAY);
                    mail.setExpireTime(720 * 3600 + (int) (System.currentTimeMillis() / 1000L));
                    mail.save();

                    activeChar.getPlayerVariables().set(PlayerVariables.MY_BIRTHDAY_RECEIVE_YEAR, String.valueOf(now.get(Calendar.YEAR)), -1);
                }
            }
        }

        if (activeChar.getClan() != null) {
            notifyClanMembers(activeChar);

            activeChar.sendPacket(activeChar.getClan().listAll());
            activeChar.sendPacket(new PledgeShowInfoUpdate(activeChar.getClan()), new PledgeSkillList(activeChar.getClan()));
        }

        // engage and notify Partner
        if (first && ServerConfig.ALLOW_WEDDING) {
            CoupleManager.getInstance().engage(activeChar);
            CoupleManager.getInstance().notifyPartner(activeChar);
        }

        if (first) {
            activeChar.getFriendComponent().notifyFriends(true);
            activeChar.restoreDisableSkills();
        }

        activeChar.sendPacket(new L2FriendList(activeChar), new ExStorageMaxCount(activeChar), new QuestList(activeChar), new ExBasicActionList(), new EtcStatusUpdate(activeChar));
        if (activeChar.getEvent(DominionSiegeEvent.class) != null) {
            activeChar.sendPacket(ExDominionChannelSet.ACTIVE);
        }

        activeChar.checkHpMessages(activeChar.getMaxHp(), activeChar.getCurrentHp());
        activeChar.checkDayNightMessages();

        if (AllSettingsConfig.PETITIONING_ALLOWED) {
            PetitionManager.getInstance().checkPetitionMessages(activeChar);
        }

        if (!first) {
            if (activeChar.isCastingNow()) {
                final Creature castingTarget = activeChar.getCastingTarget();
                final SkillEntry castingSkill = activeChar.getCastingSkill();
                final long animationEndTime = activeChar.getAnimationEndTime();
                if (castingSkill != null && castingTarget != null && castingTarget.isCreature() && activeChar.getAnimationEndTime() > 0) {
                    sendPacket(new MagicSkillUse(activeChar, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0));
                }
            }

            if (activeChar.isInBoat()) {
                activeChar.sendPacket(activeChar.getBoat().getOnPacket(activeChar, activeChar.getInBoatPosition()));
            }

            if (activeChar.isMoving || activeChar.isFollow) {
                sendPacket(activeChar.movePacket());
            }

            if (activeChar.getMountNpcId() != 0) {
                sendPacket(new Ride(activeChar));
            }

            if (activeChar.isFishing()) {
                activeChar.stopFishing();
            }
        }

        activeChar.entering = false;
        activeChar.sendUserInfo(true);

        if (activeChar.isSitting()) {
            activeChar.sendPacket(new ChangeWaitType(activeChar, ChangeWaitType.WT_SITTING));
        }
        if (activeChar.getPrivateStoreType() != Player.STORE_PRIVATE_NONE) {
            if (activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_BUY) {
                sendPacket(new PrivateStoreMsgBuy(activeChar));
            } else if (activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_SELL) {
                sendPacket(new PrivateStoreMsgSell(activeChar));
            } else if (activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_SELL_PACKAGE) {
                sendPacket(new ExPrivateStoreWholeMsg(activeChar));
            } else if (activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_MANUFACTURE) {
                sendPacket(new RecipeShopMsg(activeChar));
            }
        }

        if (activeChar.isDead()) {
            sendPacket(new Die(activeChar));
        }

        activeChar.getPlayerVariables().remove(PlayerVariables.OFFLINE);

        // на всякий случай
        activeChar.sendActionFailed();

        PlayerMessageStack.getInstance().CheckMessages(activeChar);

        sendPacket(ClientSetTime.STATIC, new ExSetCompassZoneCode(activeChar));

        final Pair<Integer, OnAnswerListener> entry = activeChar.getAskListener(false);
        if (entry != null && entry.getValue() instanceof ReviveAnswerListener) {
            sendPacket(new ConfirmDlg(SystemMsg.C1_IS_MAKING_AN_ATTEMPT_TO_RESURRECT_YOU_IF_YOU_CHOOSE_THIS_PATH_S2_EXPERIENCE_WILL_BE_RETURNED_FOR_YOU, 0).addString("Other player").addString("some"));
        }

        if (activeChar.isCursedWeaponEquipped()) {
            CursedWeaponsManager.getInstance().showUsageTime(activeChar, activeChar.getCursedWeaponEquippedId());
        }

        if (!first) {
            //Персонаж вылетел во время просмотра
            if (activeChar.isInObserverMode()) {
                if (activeChar.getObserverMode() == Player.OBSERVER_STARTING) {
                    if (activeChar.getOlympiadObserveGame() != null) {
                        sendPacket(new TeleportToLocation(activeChar, activeChar.getObservePoint().getLoc()));
                    } else {
                        sendPacket(new ObserverStart(activeChar.getObservePoint().getLoc()));
                    }
                } else if (activeChar.getObserverMode() == Player.OBSERVER_LEAVING) {
                    activeChar.returnFromObserverMode();
                } else if (activeChar.getOlympiadObserveGame() != null) {
                    activeChar.leaveOlympiadObserverMode(true);
                } else {
                    activeChar.leaveObserverMode();
                }
            } else if (activeChar.isVisible()) {
                World.showObjectsToPlayer(activeChar, false);
            }

            if (activeChar.getServitor() != null) {
                sendPacket(new PetInfo(activeChar.getServitor()));
            }

            if (activeChar.isInParty()) {
                Servitor member_pet;
                //sends new member party window for all members
                //we do all actions before adding member to a list, this speeds things up a little
                sendPacket(new PartySmallWindowAll(activeChar.getParty(), activeChar));

                for (final Player member : activeChar.getParty().getPartyMembers()) {
                    if (member != activeChar) {
                        sendPacket(new PartySpelled(member, true));
                        if ((member_pet = member.getServitor()) != null) {
                            sendPacket(new PartySpelled(member_pet, true));
                        }

                        sendPacket(RelationChanged.update(activeChar, member, activeChar));
                    }
                }

                // Если партия уже в СС, то вновь прибывшем посылаем пакет открытия окна СС
                if (activeChar.getParty().isInCommandChannel()) {
                    sendPacket(ExMPCCOpen.STATIC);
                }
            }

            for (final int shotId : activeChar.getAutoSoulShot()) {
                sendPacket(new ExAutoSoulShot(shotId, true));
            }

            for (final Effect e : activeChar.getEffectList().getAllFirstEffects()) {
                if (e.getSkill().getTemplate().isToggle()) {
                    sendPacket(new MagicSkillLaunched(activeChar.getObjectId(), e.getSkill().getId(), e.getSkill().getLevel(), activeChar.getObjectId()));
                }
            }

            activeChar.broadcastCharInfo();
        } else {
            activeChar.sendUserInfo(); // Отобразит права в клане
        }

        if (activeChar.isLoadingAfterCarrier())
            World.showObjectsToPlayer(activeChar, false);

        activeChar.updateEffectIcons();
        activeChar.updateStats();

        if (AllSettingsConfig.ALT_PCBANG_POINTS_ENABLED) {
            activeChar.sendPacket(new ExPCCafePointInfo(activeChar, 0, 1, 2, 12));
        }

        if (!activeChar.getPremiumAccountComponent().getPremiumItemList().isEmpty()) {
            activeChar.sendPacket(ExtConfig.EX_GOODS_INVENTORY_ENABLED ? ExGoodsInventoryChangedNotify.STATIC : ExNotifyPremiumItem.STATIC);
        }

        activeChar.getRecommendationComponent().sendVoteSystemInfo(activeChar);
        activeChar.sendPacket(new ExReceiveShowPostFriend(activeChar));
        activeChar.getNevitComponent().onEnterWorld();

        checkNewMail(activeChar);

        if (getClient() != null) {
            activeChar.setLastIp(getClient().getIpAddr());
        }

        if (ExtConfig.EX_ENABLE_AUTO_HUNTING_REPORT)
            BotReportManager.getInstance().onEnter(activeChar);

        if (false) {
            String page = HtmCache.getInstance().getHtml("welcome.htm", activeChar)
                    .replaceFirst("%name%", activeChar.getName());
            Functions.show(page, activeChar, null);
        }

        if (ExtConfig.EX_CHANGE_NAME_DIALOG) {
            final Clan clan = activeChar.getClan();
            if (clan == null || clan.getLeaderId(Clan.SUBUNIT_MAIN_CLAN) != activeChar.getObjectId()) {
                return;
            }

            final String name = clan.getUnitName(Clan.SUBUNIT_MAIN_CLAN);
            if (!Util.isMatchingRegexp(name, ServerConfig.CLAN_NAME_TEMPLATE)) {
                activeChar.sendPacket(new ExNeedToChangeName(ExNeedToChangeName.TYPE_CLAN_NAME, ExNeedToChangeName.REASON_INVALID, name));
            } else if (ClanTable.getInstance().getClansSizeByName(name) > 1) {
                activeChar.sendPacket(new ExNeedToChangeName(ExNeedToChangeName.TYPE_CLAN_NAME, ExNeedToChangeName.REASON_EXISTS, name));
            }
        }
        if (activeChar.isLoadingAfterCarrier())
            activeChar.stopLoadingAfterCarrier();
        AuthServerCommunication.getInstance().sendPacket(new RequestPlayerGamePoint(activeChar));
    }

    private void GameMasterService(final boolean first, final Player activeChar) {
        if (!activeChar.isGM() || !activeChar.getPlayerAccess().CanUseEffect)
            return;
        if (ServerConfig.GM_ENTER_INVISIBLE) {
            activeChar.setInvisibleType(InvisibleType.NORMAL);
            activeChar.sendAdminMessage("You are invisible.");
        }
        if (ServerConfig.GM_ENTER_INVULNERABLE) {
            activeChar.setIsInvul(true);
            activeChar.startAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
            if (activeChar.getServitor() != null) {
                activeChar.getServitor().setIsInvul(true);
                activeChar.getServitor().startAbnormalEffect(AbnormalEffect.S_INVULNERABLE);
            }
            activeChar.sendAdminMessage("You are invulnerable.");
        }
        if (ServerConfig.GM_IGNORE_FRIEND_REQUEST) {
            activeChar.setMessageRefusal(true);
            activeChar.sendAdminMessage("You can't be invited to friendship.");
        }
        if (ServerConfig.GM_IGNORE_PRIVATE_MESSAGES) {
            activeChar.setBlockAll(true);
            activeChar.sendAdminMessage("You can't take private messages.");
        }
        if (first && ServerConfig.SHOW_GM_LOGIN) {
            AnnouncementUtils.announceToAll("Game Master " + activeChar.getName() + " is logged in.");
        }
    }

    private void checkNewMail(final Player activeChar) {
        for (final Mail mail : MailDAO.getInstance().getReceivedMailByOwnerId(activeChar.getObjectId())) {
            if (mail.isUnread() && !mail.isNewMail()) {
                sendPacket(ExNoticePostArrived.STATIC_FALSE);
                break;
            }
        }
    }
}