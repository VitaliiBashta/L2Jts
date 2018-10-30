package org.mmocore.gameserver.manager;

import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerMessageStack {
    private static PlayerMessageStack _instance;

    private final Map<Integer, List<IBroadcastPacket>> _stack = new ConcurrentHashMap<>();

    public PlayerMessageStack() {
        //TODO: загрузка из БД
    }

    public static PlayerMessageStack getInstance() {
        if (_instance == null) {
            _instance = new PlayerMessageStack();
        }
        return _instance;
    }

    public void mailto(final int char_obj_id, final IBroadcastPacket message) {
        final Player cha = GameObjectsStorage.getPlayer(char_obj_id);
        if (cha != null) {
            cha.sendPacket(message);
            return;
        }

        final List<IBroadcastPacket> messages;
        if (_stack.containsKey(char_obj_id)) {
            messages = _stack.remove(char_obj_id);
        } else {
            messages = new ArrayList<>();
        }
        messages.add(message);
        //TODO: сохранение в БД
        _stack.put(char_obj_id, messages);
    }

    public void CheckMessages(final Player cha) {
        if (!_stack.containsKey(cha.getObjectId())) {
            return;
        }
        final List<IBroadcastPacket> messages = _stack.remove(cha.getObjectId());

        if (messages == null || messages.isEmpty()) {
            return;
        }
        //TODO: удаление из БД
        for (final IBroadcastPacket message : messages) {
            cha.sendPacket(message);
        }
    }
}