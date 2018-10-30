package org.mmocore.gameserver.handler.telnetcommands.impl;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.apache.commons.io.FileUtils;
import org.mmocore.gameserver.handler.telnetcommands.ITelnetCommandHandler;
import org.mmocore.gameserver.handler.telnetcommands.TelnetCommand;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TelnetDebug implements ITelnetCommandHandler {
    private final Set<TelnetCommand> _commands = new LinkedHashSet<>();

    public TelnetDebug() {
        _commands.add(new TelnetCommand("dumpnpc", "dnpc") {
            @Override
            public String getUsage() {
                return "dumpnpc";
            }

            @Override
            public String handle(final String[] args) {
                final StringBuilder sb = new StringBuilder();

                int total = 0;
                int maxId = 0, maxCount = 0;

                final TIntObjectHashMap<List<NpcInstance>> npcStats = new TIntObjectHashMap<>();

                for (final NpcInstance npc : GameObjectsStorage.getNpcs()) {
                    List<NpcInstance> list;
                    final int id = npc.getNpcId();

                    if ((list = npcStats.get(id)) == null) {
                        npcStats.put(id, list = new ArrayList<>());
                    }

                    list.add(npc);

                    if (list.size() > maxCount) {
                        maxId = id;
                        maxCount = list.size();
                    }

                    total++;
                }

                sb.append("Total NPCs: ").append(total).append('\n');
                sb.append("Maximum NPC ID: ").append(maxId).append(" count : ").append(maxCount).append('\n');

                final TIntObjectIterator<List<NpcInstance>> itr = npcStats.iterator();

                while (itr.hasNext()) {
                    itr.advance();
                    final int id = itr.key();
                    final List<NpcInstance> list = itr.value();
                    sb.append("=== ID: ").append(id).append(' ').append(" Count: ").append(list.size()).append(" ===").append('\n');

                    for (final NpcInstance npc : list) {
                        try {
                            sb.append("AI: ");

                            if (npc.hasAI()) {
                                sb.append(npc.getAI().getClass().getName());
                            } else {
                                sb.append("none");
                            }

                            sb.append(", ");

                            if (npc.getReflectionId() > 0) {
                                sb.append("ref: ").append(npc.getReflectionId());
                                sb.append(" - ").append(npc.getReflection().getName());
                            }

                            sb.append("loc: ").append(npc.getLoc());
                            sb.append(", ");
                            sb.append("spawned: ");
                            sb.append(npc.isVisible());
                            sb.append('\n');
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    new File("stats").mkdir();
                    FileUtils.writeStringToFile(new File("stats/NpcStats-" + new SimpleDateFormat("MMddHHmmss").format(System.currentTimeMillis()) + ".txt"), sb.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return "NPC stats saved.\n";
            }

        });

        _commands.add(new TelnetCommand("asrestart") {
            @Override
            public String getUsage() {
                return "asrestart";
            }

            @Override
            public String handle(final String[] args) {
                AuthServerCommunication.getInstance().restart();

                return "Restarted.\n";
            }

        });
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return _commands;
    }
}