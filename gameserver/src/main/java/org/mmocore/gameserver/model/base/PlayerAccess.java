package org.mmocore.gameserver.model.base;

public class PlayerAccess {
    public int PlayerID;
    public boolean IsGM = false;
    public final String GMAllowedIP = "not_used";
    public final boolean CanUseGMCommand = false;
    public final boolean CanAnnounce = false;
    // Право банить чат
    public final boolean CanBanChat = false;
    // Право снимать бан чата
    public final boolean CanUnBanChat = false;
    // Право выдавать штрафы
    public boolean CanChatPenalty = false;
    // Задержка в секундах между банами, -1 отключено
    public int BanChatDelay = -1;
    // Максимально разрешенный срок, -1 отключено
    public final int BanChatMaxValue = -1;
    // Сколько банов в день разрешено, -1 отключено
    public final int BanChatCountPerDay = -1;
    // Выдавать модератору бонус раз в сутки, -1 отключено
    public final int BanChatBonusId = -1;
    // Количество бонусов
    public final int BanChatBonusCount = -1;
    public boolean CanCharBan = false;
    public boolean CanCharUnBan = false;
    public final boolean CanBan = false;
    public boolean CanUnBan = false;
    public final boolean CanTradeBanUnban = false;
    public boolean CanUseBanPanel = false;
    public final boolean CanUseEffect = false;
    public final boolean UseGMShop = false;
    public boolean CanDelete = false;
    public final boolean CanKick = false;
    public final boolean Menu = false;
    public final boolean GodMode = false;
    public final boolean CanEditChar = false;
    public final boolean CanEditCharAll = false;
    public final boolean CanEditPledge = false;
    public final boolean CanViewChar = false;
    public final boolean CanEditNPC = false;
    public boolean CanViewNPC = false;
    public final boolean CanTeleport = false;
    public final boolean CanRestart = false;
    public final boolean CanSnoop = false;
    public final boolean MonsterRace = false;
    public final boolean Rider = false;
    public final boolean FastUnstuck = false;
    public final boolean ResurectFixed = false;
    public final boolean Door = false;
    public final boolean Res = false;
    public final boolean PeaceAttack = false;
    public final boolean Heal = false;
    public boolean Unblock = false;
    public final boolean UseInventory = true;
    public final boolean UseTrade = true;
    public final boolean CanAttack = true;
    public final boolean CanEvaluate = true;
    public boolean CanJoinParty = true;
    public final boolean CanJoinClan = true;
    public final boolean UseWarehouse = true;
    public final boolean UseShop = true;
    public boolean UseTeleport = true;
    public boolean BlockInventory = false;
    public final boolean CanChangeClass = false;
    public final boolean CanGmEdit = false;
    public final boolean IsEventGm = false;
    public final boolean CanReload = false;
    public final boolean CanRename = false;
    public boolean CanJail = false;
    public final boolean CanPolymorph = false;
    public final boolean isGameModerator = false;
    public final boolean SmartGuard = false;

    public PlayerAccess() {
    }
}