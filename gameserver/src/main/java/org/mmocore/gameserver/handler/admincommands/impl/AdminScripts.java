package org.mmocore.gameserver.handler.admincommands.impl;

import org.apache.commons.lang3.ClassUtils;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.scripts.Scripts;
import org.mmocore.gameserver.database.dao.impl.CharacterQuestDAO;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;

import java.io.File;
import java.util.List;

public class AdminScripts implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.isGM()) {
            return false;
        }

        switch (command) {
            case admin_run_script:
            case admin_runs:
                if (wordList.length < 2)
                    return false;
                String param = wordList[1];
                if (!run(param))
                    activeChar.sendMessage("Can't run script.");
                else
                    activeChar.sendMessage("Running script...");
                break;
            case admin_sqreload:
                reloadQuestStates(activeChar);
                break;
        }
        return true;
    }

    private boolean run(String target) {
        final File file = new File(ServerConfig.DATAPACK_ROOT, "data/scripts/" + target.replace(".", "/") + ".java");
        if (!file.exists()) {
            return false;
        }

        final List<Class<?>> classes = Scripts.getInstance().load(file);
        for (Class<?> clazz : classes) {
            if (!ClassUtils.isAssignable(clazz, Runnable.class)) {
                return false;
            }

            final Runnable r;
            try {
                r = (Runnable) clazz.newInstance();
            } catch (Exception e) {
                return false;
            }

            ThreadPoolManager.getInstance().execute(r);
            return true;
        }

        return false;
    }

    private void reloadQuestStates(final Player p) {
        for (final QuestState qs : p.getAllQuestsStates()) {
            p.removeQuestState(qs.getQuest().getId());
        }
        CharacterQuestDAO.getInstance().select(p);
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_runs,
        admin_run_script,
        admin_sqreload
    }
}