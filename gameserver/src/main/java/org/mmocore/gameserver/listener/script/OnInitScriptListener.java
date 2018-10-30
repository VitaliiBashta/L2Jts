package org.mmocore.gameserver.listener.script;

import org.mmocore.gameserver.listener.ScriptListener;

/**
 * @author VISTALL
 * @date 1:06/19.08.2011
 */
@FunctionalInterface
public interface OnInitScriptListener extends ScriptListener {
    void onInit();
}
