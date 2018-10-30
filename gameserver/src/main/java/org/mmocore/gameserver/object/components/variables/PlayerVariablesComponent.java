package org.mmocore.gameserver.object.components.variables;

import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.database.dao.impl.variables.PlayerVariablesDAO;
import org.mmocore.gameserver.object.Player;

/**
 * @author Java-man
 */
public class PlayerVariablesComponent extends AbstractVariablesComponent<PlayerVariables> {
    public PlayerVariablesComponent(final Player player, boolean isCanUseDao) {
        super(player.getObjectId(), PlayerVariablesDAO.getInstance(), isCanUseDao);
    }

    public PlayerVariablesComponent(final Player player) {
        this(player, true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // TODO Здесь обязятельно выставлять все стандартные параметры, иначе будут NPE
        if (get(PlayerVariables.LANG) == null) {
            set(PlayerVariables.LANG, ServerConfig.DEFAULT_LANG, -1L);
        }
    }
}
