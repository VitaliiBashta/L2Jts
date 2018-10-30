package org.mmocore.gameserver.object.components.player;

/**
 * @author VISTALL
 * @date 0:09/21.08.2011
 */
public class AccountPlayerInfo {
    private final long _createTime;
    private final String _name;

    public AccountPlayerInfo(int createTime, String name) {
        _createTime = createTime * 1000L;
        _name = name;
    }

    public long getCreateTime() {
        return _createTime;
    }

    public String getName() {
        return _name;
    }
}
