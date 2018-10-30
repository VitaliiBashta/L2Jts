package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.commons.lang.StatsUtils;
import org.mmocore.gameserver.ai.CharacterAI;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.model.SimpleSpawner;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.instances.RaidBossInstance;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.mmocore.gameserver.world.WorldRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class handles following admin commands: - help path = shows
 * admin/path file to char, should not be used by GM's directly
 */
public class AdminServer implements IAdminCommandHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServer.class);

    public static void showHelpPage(final Player activeChar, final String filename) {
        final HtmlMessage adminReply = new HtmlMessage(5);
        adminReply.setFile("admin/" + filename);
        activeChar.sendPacket(adminReply);
    }

    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().Menu) {
            return false;
        }

        switch (command) {
            case admin_server:
                try {
                    final String val = fullString.substring(13);
                    showHelpPage(activeChar, val);
                } catch (StringIndexOutOfBoundsException e) {
                    // case of empty filename
                }
                break;
            case admin_check_actor:
                final GameObject obj = activeChar.getTarget();
                if (obj == null) {
                    activeChar.sendAdminMessage("target == null");
                    return false;
                }

                if (!obj.isCreature()) {
                    activeChar.sendAdminMessage("target is not a character");
                    return false;
                }

                final Creature target = (Creature) obj;
                final CharacterAI ai = target.getAI();
                if (ai == null) {
                    activeChar.sendAdminMessage("ai == null");
                    return false;
                }

                final Creature actor = ai.getActor();
                if (actor == null) {
                    activeChar.sendAdminMessage("actor == null");
                    return false;
                }

                activeChar.sendAdminMessage("actor: " + actor);
                break;
            case admin_setvar:
                if (wordList.length != 3) {
                    activeChar.sendAdminMessage("Incorrect argument count!!!");
                    return false;
                }
                ServerVariables.set(wordList[1], wordList[2]);
                activeChar.sendAdminMessage("Value changed.");
                break;
            case admin_set_ai_interval:
                if (wordList.length != 2) {
                    activeChar.sendAdminMessage("Incorrect argument count!!!");
                    return false;
                }
                final int interval = Integer.parseInt(wordList[1]);
                int count = 0;
                int count2 = 0;
                for (final NpcInstance npc : GameObjectsStorage.getNpcs()) {
                    if (npc == null || npc instanceof RaidBossInstance) {
                        continue;
                    }
                    final CharacterAI char_ai = npc.getAI();
                    if (char_ai instanceof DefaultAI) {
                        try {
                            final Field field = DefaultAI.class.getDeclaredField("AI_TASK_DELAY");
                            field.setAccessible(true);
                            field.set(char_ai, interval);

                            if (char_ai.isActive()) {
                                char_ai.stopAITask();
                                count++;
                                final WorldRegion region = npc.getCurrentRegion();
                                if (region != null && region.isActive()) {
                                    char_ai.startAITask();
                                    count2++;
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                }
                activeChar.sendAdminMessage(count + " AI stopped, " + count2 + " AI started");
                break;
            case admin_spawn2: // игнорирует запрет на спавн рейдбоссов
                final StringTokenizer st = new StringTokenizer(fullString, " ");
                try {
                    st.nextToken();
                    final String id = st.nextToken();
                    int respawnTime = 30;
                    int mobCount = 1;
                    if (st.hasMoreTokens()) {
                        mobCount = Integer.parseInt(st.nextToken());
                    }
                    if (st.hasMoreTokens()) {
                        respawnTime = Integer.parseInt(st.nextToken());
                    }
                    spawnMonster(activeChar, id, respawnTime, mobCount);
                } catch (Exception e) {
                }
                break;
            case admin_memory_usage:
                LOGGER.info("=====================DEBUG=====================");
                final String memUsage = String.valueOf(StatsUtils.getMemUsage());
                for (final String line : memUsage.split("\n")) {
                    LOGGER.info(line);
                }
                LOGGER.info("=====================DEBUG=====================");
                activeChar.sendAdminMessage("MaxAllocatedMemory: = " + StatsUtils.getMemMaxMb());
                activeChar.sendAdminMessage("MaxUsedMemory: = " + StatsUtils.getMemUsedMb());
                activeChar.sendAdminMessage("MaxFreeMemory: = " + StatsUtils.getMemFreeMb());
                break;
        }

        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void spawnMonster(final Player activeChar, String monsterId, final int respawnTime, final int mobCount) {
        GameObject target = activeChar.getTarget();
        if (target == null) {
            target = activeChar;
        }

        final Pattern pattern = Pattern.compile("[0-9]*");
        final Matcher regexp = pattern.matcher(monsterId);
        final NpcTemplate template;
        if (regexp.matches()) {
            // First parameter was an ID number
            final int monsterTemplate = Integer.parseInt(monsterId);
            template = NpcHolder.getInstance().getTemplate(monsterTemplate);
        } else {
            // First parameter wasn't just numbers so go by name not ID
            monsterId = monsterId.replace('_', ' ');
            template = NpcHolder.getInstance().getTemplateByName(monsterId);
        }

        if (template == null) {
            activeChar.sendAdminMessage("Incorrect monster template.");
            return;
        }

        try {
            final SimpleSpawner spawn = new SimpleSpawner(template);
            spawn.setLoc(target.getLoc());
            spawn.setAmount(mobCount);
            spawn.setHeading(activeChar.getHeading());
            spawn.setRespawnDelay(respawnTime);
            spawn.setReflection(activeChar.getReflection());
            spawn.init();
            if (respawnTime == 0) {
                spawn.stopRespawn();
            }
            activeChar.sendAdminMessage("Created " + template.name + " on " + target.getObjectId() + '.');
        } catch (Exception e) {
            activeChar.sendAdminMessage("Target is not ingame.");
        }
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_server,
        admin_check_actor,
        admin_setvar,
        admin_set_ai_interval,
        admin_spawn2,
        admin_memory_usage
    }
}