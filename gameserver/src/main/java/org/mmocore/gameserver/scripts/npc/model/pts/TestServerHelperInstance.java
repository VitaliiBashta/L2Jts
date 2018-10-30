package org.mmocore.gameserver.scripts.npc.model.pts;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author KilRoy
 */
public class TestServerHelperInstance extends NpcInstance {
    private static final int[] merchant = {31756, 31757};

    public TestServerHelperInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public boolean isMerchantNpc() {
        if (ArrayUtils.contains(merchant, getNpcId()))
            return true;
        return false;
    }
}