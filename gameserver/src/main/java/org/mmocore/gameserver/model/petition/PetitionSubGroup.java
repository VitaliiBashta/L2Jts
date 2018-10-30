package org.mmocore.gameserver.model.petition;

import org.mmocore.gameserver.data.scripts.Scripts;
import org.mmocore.gameserver.handler.petition.IPetitionHandler;

/**
 * @author VISTALL
 * @date 7:32/25.07.2011
 */
public class PetitionSubGroup extends PetitionGroup {
    private final IPetitionHandler _handler;

    public PetitionSubGroup(final int id, final String handler) {
        super(id);

        final Class<?> clazz = Scripts.getInstance().getClasses().get("handler.petition." + handler);

        try {
            _handler = (IPetitionHandler) clazz.newInstance();
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public IPetitionHandler getHandler() {
        return _handler;
    }
}
