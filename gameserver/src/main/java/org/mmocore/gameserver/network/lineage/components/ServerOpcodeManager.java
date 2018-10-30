package org.mmocore.gameserver.network.lineage.components;

import gnu.trove.map.hash.THashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author: Camelion, KilRoy
 * @date: 01.08.13/11:00
 */
public class ServerOpcodeManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerOpcodeManager.class);

    private static final FirstServerPacketOpcode SWITCH_OPCODE = FirstServerPacketOpcode._0xFE;
    private static final ServerOpcodeManager INSTANCE = new ServerOpcodeManager();
    private final Map<Class<? extends GameServerPacket>, FirstServerPacketOpcode> firstOpcodes;
    private final Map<Class<? extends GameServerPacket>, SecondServerPacketOpcode> secondOpcodes;

    private ServerOpcodeManager() {
        firstOpcodes = new THashMap<>(FirstServerPacketOpcode.values().length + SecondServerPacketOpcode.values().length);
        secondOpcodes = new THashMap<>(SecondServerPacketOpcode.values().length);

        init();
    }

    public static ServerOpcodeManager getInstance() {
        return INSTANCE;
    }

    private void init() {
        for (final FirstServerPacketOpcode opcode : FirstServerPacketOpcode.values()) {
            if (opcode.clazz != null) {
                firstOpcodes.put(opcode.clazz, opcode);
            }
        }

        for (final SecondServerPacketOpcode opcode : SecondServerPacketOpcode.values()) {
            if (opcode.clazz != null) {
                firstOpcodes.put(opcode.clazz, SWITCH_OPCODE);
                secondOpcodes.put(opcode.clazz, opcode);
            }
        }

        LOGGER.info("ServerOpcodeManager: load ServerPackets={}, and Extended(FE)={}", firstOpcodes.size() - secondOpcodes.size(), secondOpcodes.size());
    }

    public FirstServerPacketOpcode getFirstOpcode(final Class<? extends GameServerPacket> clazz) {
        return firstOpcodes.get(clazz);
    }

    public SecondServerPacketOpcode getSecondOpcode(final Class<? extends GameServerPacket> clazz) {
        return secondOpcodes.get(clazz);
    }
}