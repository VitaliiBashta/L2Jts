package org.mmocore.gameserver.handler.bbs;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BbsHandlerHolder extends AbstractHolder {
    private static final Logger _log = LoggerFactory.getLogger(BbsHandlerHolder.class);
    private static final BbsHandlerHolder _instance = new BbsHandlerHolder();

    private final Map<String, IBbsHandler> _handlers = new HashMap<>();
    private final StatsSet _properties = new StatsSet();

    private BbsHandlerHolder() {
        //
    }

    public static BbsHandlerHolder getInstance() {
        return _instance;
    }

    public void registerHandler(final IBbsHandler commHandler) {
        for (final String bypass : commHandler.getBypassCommands()) {
            if (_handlers.containsKey(bypass)) {
                _log.warn("CommunityBoard: dublicate bypass registered! First handler: " + _handlers.get(bypass).getClass().getSimpleName() + " second: " + commHandler.getClass()
                        .getSimpleName());
            }

            _handlers.put(bypass, commHandler);
        }
    }

    public void removeHandler(final IBbsHandler handler) {
        for (final String bypass : handler.getBypassCommands()) {
            _handlers.remove(bypass);
        }
        _log.info("CommunityBoard: " + handler.getClass().getSimpleName() + " unloaded.");
    }

    public Optional<IBbsHandler> getCommunityHandler(final String bypass) {
        if (!CBasicConfig.COMMUNITYBOARD_ENABLED || _handlers.isEmpty()) {
            return Optional.empty();
        }
        final Optional<Map.Entry<String, IBbsHandler>> value = _handlers.entrySet().stream().filter(e -> bypass.contains(e.getKey())).findFirst();
        return value.isPresent() ? Optional.of(value.get().getValue()) : Optional.empty();
    }

    public void setProperty(final String name, final String val) {
        _properties.set(name, val);
    }

    public void setProperty(final String name, int val) {
        _properties.set(name, val);
    }

    public int getIntProperty(final String name) {
        return _properties.getInteger(name, 0);
    }

    @Override
    public int size() {
        return _handlers.size();
    }

    @Override
    public void clear() {
        _handlers.clear();
    }
}
