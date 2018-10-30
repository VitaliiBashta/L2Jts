package org.mmocore.gameserver.model.entity.olympiad;

/**
 * Created by Hack
 * Date: 18.06.2016 5:58
 */
public class RegisteredPlayerInfo {
    private int objId;
    private String ip;
    private String hwid;

    public RegisteredPlayerInfo(int objId, String ip, String hwid) {
        this.objId = objId;
        this.ip = ip;
        this.hwid = hwid;
    }

    public int getObjId() {
        return objId;
    }

    public void setObjId(int objId) {
        this.objId = objId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHwid() {
        return hwid;
    }

    public void setHwid(String hwid) {
        this.hwid = hwid;
    }

    @Override
    public String toString() {
        return "Obj_id: " + objId + ", ip: " + ip + ", hwid: " + hwid;
    }
}
