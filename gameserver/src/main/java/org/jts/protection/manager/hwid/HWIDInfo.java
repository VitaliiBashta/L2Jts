package org.jts.protection.manager.hwid;

import org.jts.protection.manager.HWIDBanManager.BanType;

/**
 * @author KilRoy
 */
public class HWIDInfo {
    private final int id;
    private String HWID;
    private String login;
    private BanType banType;

    public HWIDInfo(final int id) {
        this.id = id;
    }

    public int getIdInList() {
        return id;
    }

    public String getHWID() {
        return HWID;
    }

    public void setHWID(final String HWID) {
        this.HWID = HWID;
    }

    public void setHWIDBanned(final String HWID) {
        this.HWID = HWID;
    }

    public BanType getBanType() {
        return banType;
    }

    public void setBanType(final BanType banType) {
        this.banType = banType;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(final String login) {
        this.login = login;
    }
}