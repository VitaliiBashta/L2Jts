package org.mmocore.gameserver.handler.admincommands.impl;


import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.ai.CharacterAI;
import org.mmocore.gameserver.data.scripts.Scripts;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.manager.RaidBossSpawnManager;
import org.mmocore.gameserver.model.SimpleSpawner;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.world.World;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AdminSpawn implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanEditNPC) {
            return false;
        }
        final StringTokenizer st;
        final NpcInstance target;
        final Spawner spawn;
        final NpcInstance npc;

        switch (command) {
            case admin_show_spawns:
                activeChar.sendPacket(new HtmlMessage(5).setFile("admin/spawns.htm"));
                break;
            case admin_spawn_index:
                try {
                    final String val = fullString.substring(18);
                    activeChar.sendPacket(new HtmlMessage(5).setFile("admin/spawns/" + val + ".htm"));
                } catch (StringIndexOutOfBoundsException e) {
                }
                break;
            case admin_spawn1:
                st = new StringTokenizer(fullString, " ");
                try {
                    st.nextToken();
                    final String id = st.nextToken();
                    int mobCount = 1;
                    if (st.hasMoreTokens()) {
                        mobCount = Integer.parseInt(st.nextToken());
                    }
                    spawnMonster(activeChar, id, 0, mobCount);
                } catch (Exception e) {
                    // Case of wrong monster data
                }
                break;
            case admin_spawn:
            case admin_spawn_monster:
                st = new StringTokenizer(fullString, " ");
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
                    // Case of wrong monster data
                }
                break;
            case admin_setai:
                if (activeChar.getTarget() == null || !activeChar.getTarget().isNpc()) {
                    activeChar.sendAdminMessage("Please select target NPC or mob.");
                    return false;
                }

                st = new StringTokenizer(fullString, " ");
                st.nextToken();
                if (!st.hasMoreTokens()) {
                    activeChar.sendAdminMessage("Please specify AI name.");
                    return false;
                }
                final String aiName = st.nextToken();
                target = (NpcInstance) activeChar.getTarget();

                Constructor<?> aiConstructor = null;
                try {
                    if (!"npc".equalsIgnoreCase(aiName)) {
                        aiConstructor = Class.forName("org.mmocore.gameserver.ai." + aiName).getConstructors()[0];
                    }
                } catch (Exception e) {
                    try {
                        aiConstructor = Scripts.getInstance().getClasses().get("ai." + aiName).getConstructors()[0];
                    } catch (Exception e1) {
                        activeChar.sendAdminMessage("This type AI not found.");
                        return false;
                    }
                }

                if (aiConstructor != null) {
                    try {
                        target.setAI((CharacterAI) aiConstructor.newInstance(target));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    target.getAI().startAITask();
                }
                break;
            case admin_setaiparam:
                if (activeChar.getTarget() == null || !activeChar.getTarget().isNpc()) {
                    activeChar.sendAdminMessage("Please select target NPC or mob.");
                    return false;
                }

                st = new StringTokenizer(fullString, " ");
                st.nextToken();

                if (!st.hasMoreTokens()) {
                    activeChar.sendAdminMessage("Please specify AI parameter name.");
                    activeChar.sendAdminMessage("USAGE: //setaiparam <param> <value>");
                    return false;
                }

                final String paramName = st.nextToken();
                if (!st.hasMoreTokens()) {
                    activeChar.sendAdminMessage("Please specify AI parameter value.");
                    activeChar.sendAdminMessage("USAGE: //setaiparam <param> <value>");
                    return false;
                }
                final String paramValue = st.nextToken();
                target = (NpcInstance) activeChar.getTarget();
                target.setParameter(paramName, paramValue);
                target.decayMe();
                target.spawnMe();
                activeChar.sendAdminMessage("AI parameter " + paramName + " succesfully setted to " + paramValue);
                break;
            case admin_dumpparams:
                if (activeChar.getTarget() == null || !activeChar.getTarget().isNpc()) {
                    activeChar.sendAdminMessage("Please select target NPC or mob.");
                    return false;
                }
                target = (NpcInstance) activeChar.getTarget();
                final MultiValueSet<String> set = target.getParameters();
                if (!set.isEmpty()) {
                    System.out.println("Dump of Parameters:\r\n" + set.toString());
                } else {
                    System.out.println("Parameters is empty.");
                }
                break;
            case admin_setheading:
                final GameObject obj = activeChar.getTarget();
                if (!obj.isNpc()) {
                    activeChar.sendAdminMessage("Target is incorrect!");
                    return false;
                }

                npc = (NpcInstance) obj;
                npc.setHeading(activeChar.getHeading());
                npc.decayMe();
                npc.spawnMe();
                activeChar.sendAdminMessage("New heading : " + activeChar.getHeading());

                spawn = npc.getSpawn();
                if (spawn == null) {
                    activeChar.sendAdminMessage("Spawn for this npc == null!");
                    return false;
                }
                break;
            case admin_generate_loc:
                if (wordList.length < 2) {
                    activeChar.sendAdminMessage("Incorrect argument count!");
                    return false;
                }

                final int id = Integer.parseInt(wordList[1]);
                int id2 = 0;
                if (wordList.length > 2) {
                    id2 = Integer.parseInt(wordList[2]);
                }

                int min_x = Integer.MIN_VALUE;
                int min_y = Integer.MIN_VALUE;
                int min_z = Integer.MIN_VALUE;
                int max_x = Integer.MAX_VALUE;
                int max_y = Integer.MAX_VALUE;
                int max_z = Integer.MAX_VALUE;

                String name = "";

                for (final NpcInstance _npc : World.getAroundNpc(activeChar)) {
                    if (_npc.getNpcId() == id || _npc.getNpcId() == id2) {
                        name = _npc.getName();
                        min_x = Math.min(min_x, _npc.getX());
                        min_y = Math.min(min_y, _npc.getY());
                        min_z = Math.min(min_z, _npc.getZ());
                        max_x = Math.max(max_x, _npc.getX());
                        max_y = Math.max(max_y, _npc.getY());
                        max_z = Math.max(max_z, _npc.getZ());
                    }
                }

                min_x -= 500;
                min_y -= 500;
                max_x += 500;
                max_y += 500;

                System.out.println("(0,'" + name + "'," + min_x + ',' + min_y + ',' + min_z + ',' + max_z + ",0),");
                System.out.println("(0,'" + name + "'," + min_x + ',' + max_y + ',' + min_z + ',' + max_z + ",0),");
                System.out.println("(0,'" + name + "'," + max_x + ',' + max_y + ',' + min_z + ',' + max_z + ",0),");
                System.out.println("(0,'" + name + "'," + max_x + ',' + min_y + ',' + min_z + ',' + max_z + ",0),");

                System.out.println("delete from spawnlist where npc_templateid in (" + id + ", " + id2 + ')' + //
                        " and locx <= " + min_x + //
                        " and locy <= " + min_y + //
                        " and locz <= " + min_z + //
                        " and locx >= " + max_x + //
                        " and locy >= " + max_y + //
                        " and locz >= " + max_z + //
                        ';');
                break;
            case admin_dumpspawn:
                st = new StringTokenizer(fullString, " ");
                try {
                    st.nextToken();
                    final String id3 = st.nextToken();
                    final int respawnTime = 30;
                    final int mobCount = 1;
                    spawnMonster(activeChar, id3, respawnTime, mobCount);
                    try {
                        new File("dumps").mkdir();
                        final File f = new File("dumps/spawndump.txt");
                        if (!f.exists()) {
                            f.createNewFile();
                        }
                        final FileWriter writer = new FileWriter(f, true);
                        writer.write("<spawn count=\"1\" respawn=\"60\" respawn_random=\"0\" period_of_day=\"none\">\n\t" + "<point x=\"" + activeChar.getLoc().x + "\" y=\"" + activeChar.getLoc().y + "\" z=\"" + activeChar.getLoc().z + "\" h=\"" + activeChar.getLoc().h + "\" />\n\t" + "<npc id=\"" + Integer.parseInt(id3) + "\" /><!--" + NpcHolder.getInstance().getTemplate(Integer.parseInt(id3)).getName() + "-->\n" + "</spawn>\n");
                        writer.close();
                    } catch (Exception e) {

                    }
                } catch (Exception e) {
                    // Case of wrong monster data
                }
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

            if (RaidBossSpawnManager.getInstance().isDefined(template.getNpcId())) {
                activeChar.sendAdminMessage("Raid Boss " + template.name + " already spawned.");
            } else {
                spawn.init();
                if (respawnTime == 0) {
                    spawn.stopRespawn();
                }
                activeChar.sendAdminMessage("Created " + template.name + " on " + target.getObjectId() + '.');
            }
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
        admin_show_spawns,
        admin_spawn,
        admin_spawn_monster,
        admin_spawn_index,
        admin_spawn1,
        admin_setheading,
        admin_setai,
        admin_setaiparam,
        admin_dumpparams,
        admin_generate_loc,
        admin_dumpspawn
    }
}