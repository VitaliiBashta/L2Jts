package org.mmocore.gameserver.configuration.config.gmAccess;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import org.mmocore.gameserver.model.base.PlayerAccess;

/**
 * Create by Mangol on 08.12.2015.
 * TODO: переписать этот бред сумашедшего.
 */
@Deprecated
public class GmAccessConfig {
    public static final String GM_PERSONAL_ACCESS_FILE = "configuration/gm/GMAccess.xml";
    public static final String GM_ACCESS_FILES_DIR = "configuration/gm/GMAccess.d/";
    public static final TMap<Integer, PlayerAccess> gmlist = new THashMap<>();
}
