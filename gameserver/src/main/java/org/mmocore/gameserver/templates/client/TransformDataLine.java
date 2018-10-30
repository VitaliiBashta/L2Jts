package org.mmocore.gameserver.templates.client;

import org.jts.dataparser.data.holder.setting.common.PlayerSex;

/**
 * Create by Mangol on 19.10.2015.
 */
public class TransformDataLine {
    private final int id;
    private final int npc_id;
    private final int item_id;
    private final PlayerSex sex;

    public TransformDataLine(PlayerSex sex, int id, int npc_id, int item_id) {
        this.sex = sex;
        this.id = id;
        this.npc_id = npc_id;
        this.item_id = item_id;
    }

    public PlayerSex getSex() {
        return sex;
    }

    public int getId() {
        return id;
    }

    public int getNpcId() {
        return npc_id;
    }

    public int getItemId() {
        return item_id;
    }
}
