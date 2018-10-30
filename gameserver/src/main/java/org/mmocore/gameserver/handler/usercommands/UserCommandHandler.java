package org.mmocore.gameserver.handler.usercommands;

import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.commons.utils.ReflectionUtils;


public class UserCommandHandler extends AbstractHolder {
    private static final UserCommandHandler _instance = new UserCommandHandler();
    private final TIntObjectHashMap<IUserCommandHandler> _datatable = new TIntObjectHashMap<>();

    private UserCommandHandler() {
        ReflectionUtils.loadClassesWithConsumer("org.mmocore.gameserver.handler.usercommands.impl", IUserCommandHandler.class,
                this::registerUserCommandHandler);
    }

    public static UserCommandHandler getInstance() {
        return _instance;
    }

    public void registerUserCommandHandler(final IUserCommandHandler handler) {
        final int[] ids = handler.getUserCommandList();
        for (final int element : ids) {
            _datatable.put(element, handler);
        }
    }

    public IUserCommandHandler getUserCommandHandler(final int userCommand) {
        return _datatable.get(userCommand);
    }

    @Override
    public int size() {
        return _datatable.size();
    }

    @Override
    public void clear() {
        _datatable.clear();
    }
}
