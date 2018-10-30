package org.mmocore.gameserver.model.entity.events;

/**
 * @author VISTALL
 * @date 14:01/10.12.2010
 */
@FunctionalInterface
public interface EventAction {
    void call(Event event);
}
