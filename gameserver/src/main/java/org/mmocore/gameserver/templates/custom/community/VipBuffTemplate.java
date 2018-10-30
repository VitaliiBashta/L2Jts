package org.mmocore.gameserver.templates.custom.community;

/**
 * Created by Hack
 * Date: 02.09.2016 4:23
 */
public class VipBuffTemplate {
    private int id;
    private int level;
    private int itemId;
    private int itemCount;

    public VipBuffTemplate(int id, int level, int itemId, int itemCount) {
        this.id = id;
        this.level = level;
        this.itemId = itemId;
        this.itemCount = itemCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
