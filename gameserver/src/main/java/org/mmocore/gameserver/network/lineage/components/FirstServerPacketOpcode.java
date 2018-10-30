package org.mmocore.gameserver.network.lineage.components;

import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.network.lineage.serverpackets.ItemModification.EnchantScroll.EnchantResult;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.*;

/**
 * @author: Camelion, KilRoy
 * @date: 01.08.13/0:46
 */
public enum FirstServerPacketOpcode {
    DIE(Die.class),
    REVIVE(Revive.class),
    ATTACK_OUT_OF_RANGE(AttackOutOfRange.class),
    ATTACKIN_COOL_TIME(AttackinCoolTime.class),
    ATTACK_DEAD_TARGET(AttackDeadTarget.class),
    SPAWN_ITEM(SpawnItem.class),
    _0x06, // TODO
    _0x07, // TODO
    DELETE_OBJECT(DeleteObject.class),
    CHARACTER_SELECTION_INFO(CharacterSelectionInfo.class),
    LOGIN_FAIL(LoginFail.class),
    CHARACTER_SELECTED(CharSelected.class),
    NPC_INFO(NpcInfo.class),
    NEW_CHARACTER_SUCCESS(NewCharacterSuccess.class),
    NEW_CHARACTER_FAIL(NewCharacterFail.class),
    CHARACTER_CREATE_SUCCESS(CharacterCreateSuccess.class),

    CHARACTER_CREATE_FAIL(CharacterCreateFail.class),
    ITEM_LIST(ItemList.class),
    SUN_RISE(SunRise.class),
    SUN_SET(SunSet.class),
    TRADE_START(TradeStart.class),
    _0x15, // TODO
    DROP_ITEM(DropItem.class),
    GET_ITEM(GetItem.class),
    STATUS_UPDATE(StatusUpdate.class),
    NPC_HTML_MESSAGE(NpcHtmlMessage.class),
    TRADE_OWN_ADD(TradeOwnAdd.class),
    TRADE_OTHER_ADD(TradeOtherAdd.class),
    TRADE_DONE(SendTradeDone.class),
    CHARACTER_DELETE_SUCCESS(CharacterDeleteSuccess.class),
    CHARACTER_DELETE_FAIL(CharacterDeleteFail.class),
    ACTION_FAIL(ActionFail.class),

    SERVER_CLOSE(ServerClose.class),
    INVENTORY_UPDATE(InventoryUpdate.class),
    TELEPORT_TO_LOCATION(TeleportToLocation.class),
    TARGET_SELECTED(TargetSelected.class),
    TARGET_UNSELECTED(TargetUnselected.class),
    AUTO_ATTACK_START(AutoAttackStart.class),
    AUTO_ATTACK_STOP(AutoAttackStop.class),
    SOCIAL_ACTION(SocialAction.class),
    CHANGE_MOVE_TYPE(ChangeMoveType.class),
    CHANGE_WAIT_TYPE(ChangeWaitType.class),
    MANAGE_PLEDGE_POWER(ManagePledgePower.class),
    _0x2B, // TODO
    ASK_JOIN_PLEDGE(AskJoinPledge.class),
    JOIN_PLEDGE(JoinPledge.class),
    VERSION_CHECK(VersionCheck.class),
    MTL(CharMoveToLocation.class),

    NS(NpcSay.class),
    CI(CharInfo.class),
    UI(UserInfo.class),
    ATTACK(Attack.class),
    _0x34, // TODO
    _0x35, // TODO
    _0x36, // TODO
    _0x37, // TODO
    _0x38, // TODO
    ASK_JOIN_PARTY(AskJoinParty.class),
    JOIN_PARTY(JoinParty.class),
    _0x3B, // TODO
    _0x3C, // TODO
    _0x3D, // TODO
    _0x3E, // TODO
    _0x3F, // TODO

    MAGIC_AND_SKILL_LIST(MagicAndSkillList.class),
    WARE_HOUSE_DEPOSIT_LIST(WareHouseDepositList.class),
    WARE_HOUSE_WITHDRAW_LIST(WareHouseWithdrawList.class),
    WARE_HOUSE_DONE(WareHouseDone.class),
    SHORT_CUT_REGISTER(ShortCutRegister.class),
    SHORT_CUT_INIT(ShortCutInit.class),
    _0x46, // TODO
    STOP_MOVE(StopMove.class),
    MAGIC_SKILL_USE(MagicSkillUse.class),
    MAGIC_SKILL_CANCELED(MagicSkillCanceled.class),
    SAY_2(Say2.class),
    _0x4B, // TODO
    DOOR_INFO(DoorInfo.class),
    _0x4D, // TODO
    PARTY_SMALL_WINDOW_ALL(PartySmallWindowAll.class),
    PARTY_SMALL_WINDOW_ADD(PartySmallWindowAdd.class),

    PARTY_SMALL_WINDOW_DELETE_ALL(PartySmallWindowDeleteAll.class),
    PARTY_SMALL_WINDOW_DELETE(PartySmallWindowDelete.class),
    PARTY_SMALL_WINDOW_UPDATE(PartySmallWindowUpdate.class),
    TRADE_PRESS_OWN_OK(TradePressOwnOk.class),
    MAGIC_SKILL_LAUNCHER(MagicSkillLaunched.class),
    _0x55, // TODO
    _0x56, // TODO
    _0x57, // TODO
    FRIEND_LIST(FriendList.class),
    _0x59, // TODO
    PLEDGE_SHOW_MEMBER_LIST_ALL(PledgeShowMemberListAll.class),
    PLEDGE_SHOW_MEMBER_LIST_UPDATE(PledgeShowMemberListUpdate.class),
    PLEDGE_SHOW_MEMBER_LIST_ADD(PledgeShowMemberListAdd.class),
    PLEDGE_SHOW_MEMBER_LIST_DELETE(PledgeShowMemberListDelete.class),
    _0x5E, // TODO
    SKILL_LIST(SkillList.class),

    VEHICLE_INFO(VehicleInfo.class),
    FINISH_ROTATING(FinishRotating.class),
    SYSTEM_MESSAGE(SystemMessage.class),
    START_PLEDGE_WAR(StartPledgeWar.class),
    _0x64, // TODO
    STOP_PLEDGE_WAR(StopPledgeWar.class),
    _0x66, // TODO
    SURRENDER_PLEDGE_WAR(SurrenderPledgeWar.class),
    _0x68, // TODO
    _0x69, // TODO
    PLEDGE_CREST(PledgeCrest.class),
    SETUP_GAUGE(SetupGauge.class),
    VEHICLE_DEPARTURE(VehicleDeparture.class),
    VEHICLE_CHECH_LOCATION(VehicleCheckLocation.class),
    GET_ON_VEHICLE(GetOnVehicle.class),
    GET_OFF_VEHICLE(GetOffVehicle.class),

    TRADE_REQUEST(SendTradeRequest.class),
    RESTART_RESPONSE(RestartResponse.class),
    MOVE_TO_PAWN(MoveToPawn.class),
    SSQ_INFO(SSQInfo.class),
    GAME_GUARD_QUERY(GameGuardQuery.class),
    L2_FRIEND_LIST(L2FriendList.class),
    L2_FRIEND(L2Friend.class),
    L2_FRIEND_STATUS(L2FriendStatus.class),
    L2_FRIEND_SAY(L2FriendSay.class),
    VALIDATE_LOCATION(ValidateLocation.class),
    START_LOCATING(StartRotating.class),
    SHOW_BOARD(ShowBoard.class),
    CHOOSE_INVENTORY_ITEM(ChooseInventoryItem.class),
    _0x7D, // Dummy packet
    MOVE_TO_LOCATION_IN_VEHICLE(MoveToLocationInVehicle.class),
    STOP_MOVE_IN_VEHICLE(StopMoveToLocationInVehicle.class),

    VALIDATE_LOCATION_IN_VEHICLE(ValidateLocationInVehicle.class),
    TRADE_UPDATE(TradeUpdate.class),
    TRADE_PRESS_OTHER_OK(TradePressOtherOk.class),
    FRIEND_ADD(FriendAddRequest.class),
    LOG_OUT_OK(LeaveWorld.class),
    ABNORMAL_STATUS_UPDATE(AbnormalStatusUpdate.class),
    QUEST_LIST(QuestList.class),
    ENCHANT_RESULT(EnchantResult.class),
    PLEDGE_SHOW_MEMBER_LIST_DELETE_ALL(PledgeShowMemberListDeleteAll.class),
    PLEDGE_INFO(PledgeInfo.class),
    PLEDGE_EXTENDED_INFO(PledgeExtendedInfo.class),
    _0x8B, // TODO
    RIDE(Ride.class),
    _0x8D, // Dummy packet
    PLEDGE_SHOW_INFO_UPDATE(PledgeShowInfoUpdate.class),
    CLIENT_ACTION(ClientAction.class),

    ACQUIRE_SKILL_LIST(AcquireSkillList.class),
    ACQUIRE_SKILL_INFO(AcquireSkillInfo.class),
    SERVER_OBJECT_INFO(ServerObjectInfo.class),
    GM_HIDE(GMHide.class),
    ACQUIRE_SKILL_DONE(AcquireSkillDone.class),
    GM_VIEW_CHARACTER_INFO(GMViewCharacterInfo.class),
    GM_VIEW_PLEDGE_INFO(GMViewPledgeInfo.class),
    GM_VIEW_SKILL_INFO(GMViewSkillInfo.class),
    GM_VIEW_MAGIC_INFO(GMViewMagicInfo.class), // TODO[K] - реализовать если возможно и нужно!
    GM_VIEW_QUEST_INFO(GMViewQuestInfo.class),
    GM_VIEW_ITEM_LIST(GMViewItemList.class),
    GM_VIEW_WARE_HOUSE_WITHDRAW_LIST(GMViewWarehouseWithdrawList.class),
    LIST_PARTY_WAITING(ListPartyWaiting.class),
    PARTY_ROOM_INFO(PartyRoomInfo.class),
    PLAY_SOUND(PlaySound.class),
    STATIC_OBJECT(StaticObject.class),

    PRIVATE_STORE_MANAGE(PrivateStoreManageListSell.class),
    PRIVATE_STORE_LIST(PrivateStoreListSell.class),
    PRIVATE_STORE_MSG(PrivateStoreMsgSell.class),
    SHOW_MINI_MAP(ShowMiniMap.class),
    _0xA4, // TODO
    _0xA5, // TODO
    TUTORIAL_SHOW_HTML(TutorialShowHtml.class),
    TUTORIAL_SHOW_QUESTION_MARK(TutorialShowQuestionMark.class),
    TUTORIAL_ENABLE_CLIENT_EVENT(TutorialEnableClientEvent.class),
    TUTORIAL_CLOSE_HTML(TutorialCloseHtml.class),
    SHOW_RADAR(ShowRadar.class),
    WITHDRAW_ALLIANCE(WithdrawAlliance.class),
    OUST_ALLIANCE_MEMBER_PLEDGE(OustAllianceMemberPledge.class),
    DISMISS_ALLIANCE(DismissAlliance.class),
    _0xAE, // TODO
    ALLIANCE_CREST(AllianceCrest.class),

    _0xB0, // TODO
    PET_STATUS_SHOW(PetStatusShow.class),
    PET_INFO(PetInfo.class),
    PET_ITEM_LIST(PetItemList.class),
    PET_INVENTORY_UPDATE(PetInventoryUpdate.class),
    _0xB5, // TODO
    PET_STATUS_UPDATE(PetStatusUpdate.class),
    PET_DELETE(PetDelete.class),
    DELETE_RADAR(DeleteRadar.class),
    MY_TARGET_SELECTED(MyTargetSelected.class),
    PARTY_MEMBER_POSITION(PartyMemberPosition.class),
    ASK_JOIN_ALLIANCE(AskJoinAlliance.class),
    _0xBC, // TODO
    PRIVATE_STORE_BUY_MANAGE_LIST(PrivateStoreManageListBuy.class),
    PRIVATE_STORE_BUY_LIST(PrivateStoreListBuy.class),
    PRIVATE_STORE_BUY_MSG(PrivateStoreMsgBuy.class),

    VEHICLE_START(VehicleStart.class),
    REQUEST_TIME_CHECK(RequestTimeCheck.class),
    START_ALLIANCE_WAR(StartAllianceWar.class),
    _0xC3, // TODO
    STOP_ALLIANCE_WAR(StopAllianceWar.class),
    _0xC5, // TODO
    _0xC6, // TODO
    SKILL_COOL_TIME(SkillCoolTime.class),
    PACKAGE_TO_LIST(PackageToList.class),
    CASTLE_SIEGE_INFO(CastleSiegeInfo.class),
    CASTLE_SIEGE_ATTACKER_LIST(CastleSiegeAttackerList.class),
    CASTLE_SIEGE_DEFENDER_LIST(CastleSiegeDefenderList.class),
    NICK_NAME_CHANGED(NickNameChanged.class),
    PLEDGE_STATUS_CHANGED(PledgeStatusChanged.class),
    RELATION_CHANGED(RelationChanged.class),
    EVENT_TRIGGER(EventTrigger.class),

    MULTI_SELL_LIST(MultiSellList.class),
    SET_SUMMON_REMAIN_TIME(SetSummonRemainTime.class),
    PACKAGE_SENDABLE_LIST(PackageSendableList.class),
    EARTH_QUAKE(Earthquake.class),
    FLY_TO_LOCATION(FlyToLocation.class),
    _0xD5, // TODO
    SPECIAL_CAMERA(SpecialCamera.class),
    NORMAL_CAMERA(NormalCamera.class),
    SKILL_REMAIN_SEC(SkillRemainSec.class),
    NET_PING(NetPing.class),
    DICE(Dice.class),
    SNOOP(Snoop.class),
    RECIPE_BOOK_ITEM_LIST(RecipeBookItemList.class),
    RECIPE_ITEM_MAKE_INFO(RecipeItemMakeInfo.class),
    RECIPE_SHOP_MANAGE_LIST(RecipeShopManageList.class),
    RECIPE_SHOP_SELL_LIST(RecipeShopSellList.class),

    RECIPE_SHOP_ITEM_INFO(RecipeShopItemInfo.class),
    RECIPE_SHOP_MSG(RecipeShopMsg.class),
    SHOW_CALC(ShowCalc.class),
    MON_RACE_INFO(MonRaceInfo.class),
    HENNA_ITEM_INFO(HennaItemInfo.class),
    HENNA_INFO(HennaInfo.class),
    HENNA_UNEQUIP_LIST(HennaUnequipList.class),
    HENNA_UNEQUIP_INFO(HennaUnequipInfo.class),
    MACRO_LIST(SendMacroList.class),
    BUY_LIST_SEED(BuyListSeed.class),
    SHOW_TOWN_MAP(ShowTownMap.class),
    OBSERVER_START(ObserverStart.class),
    EBSERVER_END(ObserverEnd.class),
    CHAIR_SIT(ChairSit.class),
    HENNA_EQUIP_LIST(HennaEquipList.class),
    SELL_LIST_PROCURE(SellListProcure.class),

    GM_HENNA_INFO(GMHennaInfo.class),
    RADAR_CONTROL(RadarControl.class),
    CLIENT_SET_TIME(ClientSetTime.class),
    CONFIRM_DLG(ConfirmDlg.class),
    PARTY_SPELLED(PartySpelled.class),
    SHOP_PREVIEW_LIST(ShopPreviewList.class),
    SHOP_PREVIEW_INFO(ShopPreviewInfo.class),
    CAMERA_MODE(CameraMode.class),
    SHOW_XMAS_SEAL(ShowXMasSeal.class),
    ETC_STATUS_UPDATE(EtcStatusUpdate.class),
    SHORT_BUFF_STATUS_UPDATE(ShortBuffStatusUpdate.class),
    SSQ_STATUS(SSQStatus.class),
    PETITION_VOTE(PetitionVote.class),
    AGIT_DECO_INFO(AgitDecoInfo.class),
    _0xFE; // Dummy

    final Class<? extends GameServerPacket> clazz;

    FirstServerPacketOpcode() {
        this(null);
    }

    FirstServerPacketOpcode(Class<? extends GameServerPacket> clazz) {
        this.clazz = clazz;
    }
}