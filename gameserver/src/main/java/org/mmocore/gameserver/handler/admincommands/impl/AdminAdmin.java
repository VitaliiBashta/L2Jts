package org.mmocore.gameserver.handler.admincommands.impl;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jts.dataparser.data.holder.AuctionDataHolder;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.configuration.loader.ConfigLoader;
import org.mmocore.gameserver.data.xml.holder.DoorHolder;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.impl.ItemsDAO;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.manager.GameTimeManager;
import org.mmocore.gameserver.manager.SoDManager;
import org.mmocore.gameserver.manager.SoIManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.events.LastHero.LastHero;
import org.mmocore.gameserver.skills.AbnormalEffect;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.io.File;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class AdminAdmin implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;
        final StringTokenizer st;

        if (activeChar.getPlayerAccess().Menu) {
            switch (command) {
                case admin_upquest:

                    try {
                        ConfigLoader.reloading();
                        DatabaseFactory.getInstance().getConnection().close();

                        final Map<String, Integer> map = new HashMap<>();

                        final Connection con = DatabaseFactory.getInstance().getConnection();

                        //
                        Statement statement = con.createStatement();
                        statement.execute("ALTER TABLE character_quests ADD COLUMN quest_id INT NOT NULL AFTER char_id");

                        DbUtils.close(statement);
                        //
                        System.out.println("1");
                        //
                        statement = con.createStatement();
                        final ResultSet rset = statement.executeQuery("SELECT name FROM character_quests");
                        while (rset.next()) {
                            final String questName = rset.getString("name");
                            //System.out.println("questName:"+questName);
                            final Integer questId = map.get(questName);
                            if (questId == null) {
                                map.put(questName, Integer.parseInt(questName.split("_")[1]));
                            }
                        }
                        DbUtils.close(statement, rset);
                        //
                        System.out.println("2");
                        //
                        for (final Map.Entry<String, Integer> entry : map.entrySet()) {
                            final PreparedStatement temp = con.prepareStatement("UPDATE character_quests SET quest_id=? WHERE name=?");
                            temp.setInt(1, entry.getValue());
                            temp.setString(2, entry.getKey());
                            temp.execute();

                            DbUtils.close(temp);
                        }
                        //
                        System.out.println("3");
                        //
                        statement = con.createStatement();
                        statement.execute("ALTER TABLE character_quests DROP PRIMARY KEY, ADD PRIMARY KEY(char_id, quest_id, var), DROP COLUMN name");
                        DbUtils.close(statement);
                        //
                        System.out.println("Done 4");
                        DbUtils.close(con);
                    } catch (Exception ex) {
                        System.out.println("0 Error:" + ex);
                    }


                    break;
                case admin_admin:
                    activeChar.sendPacket(new HtmlMessage(5).setFile("admin/admin.htm"));
                    break;
                case admin_play_sounds:
                    if (wordList.length == 1) {
                        activeChar.sendPacket(new HtmlMessage(5).setFile("admin/songs/songs.htm"));
                    } else {
                        try {
                            activeChar.sendPacket(new HtmlMessage(5).setFile("admin/songs/songs" + wordList[1] + ".htm"));
                        } catch (StringIndexOutOfBoundsException e) {
                        }
                    }
                    break;
                case admin_play_sound:
                    try {
                        playAdminSound(activeChar, wordList[1]);
                    } catch (StringIndexOutOfBoundsException e) {
                    }
                    break;
                case admin_silence:
                    if (activeChar.getMessageRefusal()) // already in message refusal mode
                    {
                        activeChar.setMessageRefusal(false);
                        activeChar.sendPacket(SystemMsg.MESSAGE_ACCEPTANCE_MODE);
                        activeChar.sendEtcStatusUpdate();
                    } else {
                        activeChar.setMessageRefusal(true);
                        activeChar.sendPacket(SystemMsg.MESSAGE_REFUSAL_MODE);
                        activeChar.sendEtcStatusUpdate();
                    }
                    break;
                case admin_tradeoff:
                    try {
                        if ("on".equalsIgnoreCase(wordList[1])) {
                            activeChar.setTradeRefusal(true);
                            activeChar.sendAdminMessage("tradeoff enabled");
                        } else if ("off".equalsIgnoreCase(wordList[1])) {
                            activeChar.setTradeRefusal(false);
                            activeChar.sendAdminMessage("tradeoff disabled");
                        }
                    } catch (Exception ex) {
                        if (activeChar.getTradeRefusal()) {
                            activeChar.sendAdminMessage("tradeoff currently enabled");
                        } else {
                            activeChar.sendAdminMessage("tradeoff currently disabled");
                        }
                    }
                    break;
                case admin_show_html:
                    final String html = wordList[1];
                    try {
                        if (html != null) {
                            activeChar.sendPacket(new HtmlMessage(5).setFile("admin/" + html));
                        } else {
                            activeChar.sendAdminMessage("Html page not found");
                        }
                    } catch (Exception npe) {
                        activeChar.sendAdminMessage("Html page not found");
                    }
                    break;
                case admin_setnpcstate:
                    if (wordList.length < 2) {
                        activeChar.sendAdminMessage("USAGE: //setnpcstate state");
                        return false;
                    }
                    final int state;
                    final GameObject target = activeChar.getTarget();
                    try {
                        state = Integer.parseInt(wordList[1]);
                    } catch (NumberFormatException e) {
                        activeChar.sendAdminMessage("You must specify state");
                        return false;
                    }
                    if (!target.isNpc()) {
                        activeChar.sendAdminMessage("You must target an NPC");
                        return false;
                    }
                    final NpcInstance npc = (NpcInstance) target;
                    npc.setNpcState(state);
                    break;
                case admin_setareanpcstate:
                    try {
                        final String val = fullString.substring(15).trim();

                        final String[] vals = val.split(" ");
                        final int range = NumberUtils.toInt(vals[0], 0);
                        final int astate = vals.length > 1 ? NumberUtils.toInt(vals[1], 0) : 0;

                        for (final NpcInstance n : activeChar.getAroundNpc(range, 200)) {
                            n.setNpcState(astate);
                        }
                    } catch (Exception e) {
                        activeChar.sendAdminMessage("Usage: //setareanpcstate [range] [state]");
                    }
                    break;
                case admin_showmovie:
                    if (wordList.length < 2) {
                        activeChar.sendAdminMessage("USAGE: //showmovie id");
                        return false;
                    }
                    final int id;
                    try {
                        id = Integer.parseInt(wordList[1]);
                    } catch (NumberFormatException e) {
                        activeChar.sendAdminMessage("You must specify id");
                        return false;
                    }
                    activeChar.showQuestMovie(id);
                    break;
                case admin_setzoneinfo:
                    if (wordList.length < 2) {
                        activeChar.sendAdminMessage("USAGE: //setzoneinfo id");
                        return false;
                    }
                    final int stateid;
                    try {
                        stateid = Integer.parseInt(wordList[1]);
                    } catch (NumberFormatException e) {
                        activeChar.sendAdminMessage("You must specify id");
                        return false;
                    }
                    activeChar.broadcastPacket(new ExChangeClientEffectInfo(stateid));
                    break;
                case admin_eventtrigger:
                    if (wordList.length < 2) {
                        activeChar.sendAdminMessage("USAGE: //eventtrigger id");
                        return false;
                    }
                    final int triggerid;
                    try {
                        triggerid = Integer.parseInt(wordList[1]);
                    } catch (NumberFormatException e) {
                        activeChar.sendAdminMessage("You must specify id");
                        return false;
                    }
                    activeChar.broadcastPacket(new EventTrigger(triggerid, true));
                    break;
                case admin_sendeventstate:
                    if (wordList.length < 3) {
                        activeChar.sendAdminMessage("USAGE: //sendeventstate id state");
                        return false;
                    }
                    final int eventId, eventState;
                    try {
                        eventId = Integer.parseInt(wordList[1]);
                        eventState = Integer.parseInt(wordList[2]);
                    } catch (NumberFormatException e) {
                        activeChar.sendAdminMessage("You must specify id and state");
                        return false;
                    }
                    activeChar.broadcastPacket(new ExBR_BroadcastEventState(eventId, eventState));
                    break;
                case admin_debug:
                    final GameObject ob = activeChar.getTarget();
                    if (!ob.isPlayer()) {
                        activeChar.sendAdminMessage("Only player target is allowed");
                        return false;
                    }
                    final Player pl = ob.getPlayer();
                    final List<String> _s = new ArrayList<>();
                    _s.add("==========TARGET STATS:");
                    _s.add("==Magic Resist: " + pl.calcStat(Stats.MAGIC_RESIST, null, null));
                    _s.add("==Magic Power: " + pl.calcStat(Stats.MAGIC_POWER, 1, null, null));
                    _s.add("==Skill Power: " + pl.calcStat(Stats.SKILL_POWER, 1, null, null));
                    _s.add("==Cast Break Rate: " + pl.calcStat(Stats.CAST_INTERRUPT, 1, null, null));

                    _s.add("==========Powers:");
                    _s.add("==Bleed: " + pl.calcStat(Stats.BLEED_POWER, 1, null, null));
                    _s.add("==Poison: " + pl.calcStat(Stats.POISON_POWER, 1, null, null));
                    _s.add("==Stun: " + pl.calcStat(Stats.STUN_POWER, 1, null, null));
                    _s.add("==Root: " + pl.calcStat(Stats.ROOT_POWER, 1, null, null));
                    _s.add("==Mental: " + pl.calcStat(Stats.MENTAL_POWER, 1, null, null));
                    _s.add("==Sleep: " + pl.calcStat(Stats.SLEEP_POWER, 1, null, null));
                    _s.add("==Paralyze: " + pl.calcStat(Stats.PARALYZE_POWER, 1, null, null));
                    _s.add("==Cancel: " + pl.calcStat(Stats.CANCEL_POWER, 1, null, null));
                    _s.add("==Debuff: " + pl.calcStat(Stats.DEBUFF_POWER, 1, null, null));

                    _s.add("==========PvP Stats:");
                    _s.add("==Phys Attack Dmg: " + pl.calcStat(Stats.PVP_PHYS_DMG_BONUS, 1, null, null));
                    _s.add("==Phys Skill Dmg: " + pl.calcStat(Stats.PVP_PHYS_SKILL_DMG_BONUS, 1, null, null));
                    _s.add("==Magic Skill Dmg: " + pl.calcStat(Stats.PVP_MAGIC_SKILL_DMG_BONUS, 1, null, null));
                    _s.add("==Phys Attack Def: " + pl.calcStat(Stats.PVP_PHYS_DEFENCE_BONUS, 1, null, null));
                    _s.add("==Phys Skill Def: " + pl.calcStat(Stats.PVP_PHYS_SKILL_DEFENCE_BONUS, 1, null, null));
                    _s.add("==Magic Skill Def: " + pl.calcStat(Stats.PVP_MAGIC_SKILL_DEFENCE_BONUS, 1, null, null));

                    _s.add("==========Reflects:");
                    _s.add("==Phys Dmg Chance: " + pl.calcStat(Stats.REFLECT_AND_BLOCK_DAMAGE_CHANCE, null, null));
                    _s.add("==Phys Skill Dmg Chance: " + pl.calcStat(Stats.REFLECT_AND_BLOCK_PSKILL_DAMAGE_CHANCE, null, null));
                    _s.add("==Magic Skill Dmg Chance: " + pl.calcStat(Stats.REFLECT_AND_BLOCK_MSKILL_DAMAGE_CHANCE, null, null));
                    _s.add("==Counterattack: Phys Dmg Chance: " + pl.calcStat(Stats.REFLECT_DAMAGE_PERCENT, null, null));
                    _s.add("==Counterattack: Phys Skill Dmg Chance: " + pl.calcStat(Stats.REFLECT_PSKILL_DAMAGE_PERCENT, null, null));
                    _s.add("==Counterattack: Magic Skill Dmg Chance: " + pl.calcStat(Stats.REFLECT_MSKILL_DAMAGE_PERCENT, null, null));

                    _s.add("==========MP Consume Rate:");
                    _s.add("==Magic Skills: " + pl.calcStat(Stats.MP_MAGIC_SKILL_CONSUME, 1, null, null));
                    _s.add("==Phys Skills: " + pl.calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, 1, null, null));
                    _s.add("==Music: " + pl.calcStat(Stats.MP_DANCE_SKILL_CONSUME, 1, null, null));

                    _s.add("==========Shield:");
                    _s.add("==Shield Defence: " + pl.calcStat(Stats.SHIELD_DEFENCE, null, null));
                    _s.add("==Shield Defence Rate: " + pl.calcStat(Stats.SHIELD_RATE, null, null));
                    _s.add("==Shield Defence Angle: " + pl.calcStat(Stats.SHIELD_ANGLE, null, null));

                    _s.add("==========Etc:");
                    _s.add("==Fatal Blow Rate: " + pl.calcStat(Stats.FATALBLOW_RATE, null, null));
                    _s.add("==Phys Skill Evasion Rate: " + pl.calcStat(Stats.PSKILL_EVASION, null, null));
                    _s.add("==Counterattack Rate: " + pl.calcStat(Stats.COUNTER_ATTACK, null, null));
                    _s.add("==Pole Attack Angle: " + pl.calcStat(Stats.POLE_ATTACK_ANGLE, null, null));
                    _s.add("==Pole Target Count: " + pl.calcStat(Stats.POLE_TARGET_COUNT, 1, null, null));
                    _s.add("==========DONE.");

                    for (final String s : _s) {
                        activeChar.sendAdminMessage(s);
                    }
                    break;
                case admin_uievent:
                    if (wordList.length < 5) {
                        activeChar.sendAdminMessage("USAGE: //uievent isHide doIncrease startTime endTime Text");
                        return false;
                    }
                    final boolean hide;
                    final boolean increase;
                    final int startTime;
                    final int endTime;
                    final String text;
                    try {
                        hide = Boolean.parseBoolean(wordList[1]);
                        increase = Boolean.parseBoolean(wordList[2]);
                        startTime = Integer.parseInt(wordList[3]);
                        endTime = Integer.parseInt(wordList[4]);
                        text = wordList[5];
                    } catch (NumberFormatException e) {
                        activeChar.sendAdminMessage("Invalid format");
                        return false;
                    }
                    activeChar.broadcastPacket(new ExSendUIEvent(activeChar, hide, increase, startTime, endTime, text));
                    break;
                case admin_opensod:
                    if (wordList.length < 1) {
                        activeChar.sendAdminMessage("USAGE: //opensod minutes");
                        return false;
                    }
                    SoDManager.openSeed(Integer.parseInt(wordList[1]) * 60 * 1000L);
                    break;
                case admin_closesod:
                    SoDManager.closeSeed();
                    break;
                case admin_setsoistage:
                    if (wordList.length < 1) {
                        activeChar.sendAdminMessage("USAGE: //setsoistage stage[1-5]");
                        return false;
                    }
                    SoIManager.setCurrentStage(Integer.parseInt(wordList[1]));
                    break;
                case admin_soinotify:
                    if (wordList.length < 1) {
                        activeChar.sendAdminMessage("USAGE: //soinotify [1-3]");
                        return false;
                    }
                    switch (Integer.parseInt(wordList[1])) {
                        case 1:
                            SoIManager.notifyCohemenesKill();
                            break;
                        case 2:
                            SoIManager.notifyEkimusKill();
                            break;
                        case 3:
                            SoIManager.notifyHoEDefSuccess();
                            break;
                    }
                    break;
                case admin_forcenpcinfo:
                    final GameObject obj2 = activeChar.getTarget();
                    if (!obj2.isNpc()) {
                        activeChar.sendAdminMessage("Only NPC target is allowed");
                        return false;
                    }
                    ((NpcInstance) obj2).broadcastCharInfo();
                    break;
                case admin_loc:
                    activeChar.sendMessage("Coords: X:" + activeChar.getLoc().x + " Y:" + activeChar.getLoc().y + " Z:" + activeChar.getLoc().z + " H:" + activeChar.getLoc().h);
                    break;
                case admin_locdump:
                    st = new StringTokenizer(fullString, " ");
                    try {
                        st.nextToken();
                        try {
                            new File("dumps").mkdir();
                            final File f = new File("dumps/locdump.txt");
                            if (!f.exists()) {
                                f.createNewFile();
                            }
                            activeChar.sendMessage("Coords: X:" + activeChar.getLoc().x + " Y:" + activeChar.getLoc().y + " Z:" + activeChar.getLoc().z + " H:" + activeChar.getLoc().h);
                            final FileWriter writer = new FileWriter(f, true);
                            writer.write("Loc: " + activeChar.getLoc().x + ", " + activeChar.getLoc().y + ", " + activeChar.getLoc().z + '\n');
                            writer.close();
                        } catch (Exception e) {

                        }
                    } catch (Exception e) {
                        // Case of wrong monster data
                    }
                    break;
                case admin_undying:
                    if (activeChar.isUndying(null)) {
                        activeChar.setUndying(false);
                        activeChar.sendAdminMessage("Undying state has been disabled.");
                    } else {
                        activeChar.setUndying(true);
                        activeChar.sendAdminMessage("Undying state has been enabled.");
                    }
                    break;
                case admin_serverstat:
                    StringBuilder gameTime = new StringBuilder();
                    gameTime.append(GameTimeManager.getInstance().getGameHour()).append(':');
                    if (GameTimeManager.getInstance().getGameMin() < 10)
                        gameTime.append('0');
                    gameTime.append(GameTimeManager.getInstance().getGameMin());
                    String upTime = DurationFormatUtils.formatDurationWords(ManagementFactory.getRuntimeMXBean().getUptime(), true, true);
                    int initialized_npc = GameObjectsStorage.getAllNpcsSize();
                    int online_players = GameObjectsStorage.getAllPlayersSize();
                    int online_priv_store = 0;
                    int initialized_door = DoorHolder.getInstance().size();
                    long loaded_all_count_items = ItemsDAO.getInstance().getCache().size();
                    int auction_size_count = AuctionDataHolder.getInstance().size();
                    int players_in_zone = 0;
                    for (Player players : GameObjectsStorage.getPlayers()) {
                        if (!players.isInZone(ZoneType.offshore))
                            players_in_zone++;
                        if (players.isInStoreMode())
                            online_priv_store++;
                    }
                    activeChar.sendAdminMessage("Server Build date : XXXXXXXX XXXXXX Protocol Version : " + ServerConfig.MAX_PROTOCOL_REVISION + " World Time : " + gameTime);
                    activeChar.sendAdminMessage("Elapsed Time : " + upTime);
                    activeChar.sendAdminMessage("Context : [Activate: Not Realeased] User[" + online_players + ":0] Item[" + loaded_all_count_items + ":0] NPC[" + initialized_npc + ":0] UserSocket[" + online_players + ":0] Door[" + initialized_door + "] InZone[" + players_in_zone + ":0] Trade[" + online_priv_store + ":0] Auction[" + auction_size_count + ":0] AirShip[1:0] MaxSerializerTime[" + Rnd.get(60) + ']');
                    break;
                case admin_debug0: {
                    // smth debug
                    LastHero.setHero(activeChar.getTarget().getPlayer());
                    // smth debug end
                    break;
                }
                case admin_para:
                    if (activeChar.getTarget() == null || !activeChar.getTarget().isCreature()) {
                        activeChar.sendAdminMessage("Check your target!");
                        return false;
                    }
                    Creature creature = (Creature) activeChar.getTarget();
                    creature.startParalyzed();
                    creature.startAbnormalEffect(AbnormalEffect.HOLD_2);
                    break;
                case admin_unpara:
                    if (activeChar.getTarget() == null || !activeChar.getTarget().isCreature()) {
                        activeChar.sendAdminMessage("Check your target!");
                        return false;
                    }
                    Creature creature0 = (Creature) activeChar.getTarget();
                    creature0.stopParalyzed();
                    creature0.stopAbnormalEffect(AbnormalEffect.HOLD_2);
                    break;
                case admin_addtoall:
                    if (wordList.length < 2) {
                        activeChar.sendAdminMessage("Syntax: //addtoall item_id item_count");
                        return false;
                    }
                    GameObjectsStorage.getPlayers().forEach(player -> {
                        final int id0 = Integer.parseInt(wordList[1]);
                        final int count0 = Integer.parseInt(wordList[2]);
                        player.getInventory().addItem(id0, count0);
                        player.sendMessage("Admin gifts you " + count0 + " " + ItemTemplateHolder.getInstance().getTemplate(id0));
                    });
                    break;
                case admin_nokarma:
                    if (activeChar.getTarget() == null || !activeChar.getTarget().isPlayer()) {
                        activeChar.sendAdminMessage("Check your target!");
                        return false;
                    }
                    activeChar.getTarget().getPlayer().setKarma(0);
                    break;
            }
            return true;
        }

        if (activeChar.getPlayerAccess().CanTeleport) {
            switch (command) {
                case admin_show_html:
                    final String html = wordList[1];
                    try {
                        if (html != null) {
                            if (html.startsWith("tele")) {
                                activeChar.sendPacket(new HtmlMessage(5).setFile("admin/" + html));
                            } else {
                                activeChar.sendAdminMessage("Access denied");
                            }
                        } else {
                            activeChar.sendAdminMessage("Html page not found");
                        }
                    } catch (Exception npe) {
                        activeChar.sendAdminMessage("Html page not found");
                    }
                    break;
            }
            return true;
        }

        return false;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    public void playAdminSound(final Player activeChar, final String sound) {
        activeChar.broadcastPacket(new PlaySound(sound));
        activeChar.sendPacket(new HtmlMessage(5).setFile("admin/admin.htm"));
        activeChar.sendAdminMessage("Playing " + sound + '.');
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_admin,
        admin_play_sounds,
        admin_play_sound,
        admin_silence,
        admin_tradeoff,
        admin_show_html,
        admin_setnpcstate,
        admin_setareanpcstate,
        admin_showmovie,
        admin_setzoneinfo,
        admin_eventtrigger,
        admin_debug,
        admin_uievent,
        admin_opensod,
        admin_closesod,
        admin_setsoistage,
        admin_soinotify,
        admin_forcenpcinfo,
        admin_loc,
        admin_locdump,
        admin_undying,
        admin_upquest,
        admin_serverstat,
        admin_sendeventstate,
        admin_debug0,
        admin_para,
        admin_unpara,
        admin_addtoall,
        admin_nokarma
    }
}