package org.mmocore.gameserver.scripts.ai.pts.events;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_br_berdandi extends ai_br_wooldie {
    public ai_br_berdandi(NpcInstance actor) {
        super(actor);
        FSTRING_VALUE = 1;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}