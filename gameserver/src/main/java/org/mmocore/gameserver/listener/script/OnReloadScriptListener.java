package org.mmocore.gameserver.listener.script;

import org.mmocore.gameserver.listener.ScriptListener;

/**
 * @author VISTALL
 * @date 1:07/19.08.2011
 */
@FunctionalInterface
public interface OnReloadScriptListener extends ScriptListener {
    void onReload();
}
