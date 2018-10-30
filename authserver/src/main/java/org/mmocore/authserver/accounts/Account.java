package org.mmocore.authserver.accounts;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.mmocore.authserver.component.Premium;
import org.mmocore.authserver.database.dao.impl.AccountsDAO;
import org.mmocore.commons.net.utils.Net;
import org.mmocore.commons.net.utils.NetList;

import java.util.HashMap;
import java.util.Map;

public class Account {
    private final String login;
    private final NetList allowedIpList = new NetList();
    private final Map<Integer, Pair<Integer, int[]>> serversInfo = new HashMap<>(2);
    private int accountId;
    private String passwordHash;
    private String allowedIP;
    private int accessLevel;
    private int checkEmail = -1;
    private String l2email;
    private int banExpire;
    private String lastIP;
    private int lastAccess;
    private int lastServer;
    private int reportPoints;
    private Premium premium;

    public Account(final String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(final int id) {
        this.accountId = id;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(final String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getAllowedIP() {
        return allowedIP;
    }

    public void setAllowedIP(final String allowedIP) {
        this.allowedIpList.clear();
        this.allowedIP = allowedIP;

        if (allowedIP.isEmpty()) {
            return;
        }

        final String[] masks = allowedIP.split("[\\s,;]+");
        for (final String mask : masks) {
            this.allowedIpList.add(Net.valueOf(mask));
        }
    }

    public boolean isAllowedIP(final String ip) {
        return allowedIpList.isEmpty() || allowedIpList.isInRange(ip);
    }

    public int getCheckEmail() {
        return checkEmail;
    }

    public void setCheckEmail(final int checkEmail) {
        this.checkEmail = checkEmail;
    }

    public String getEmail() {
        return l2email;
    }

    public void setEmail(final String email) {
        this.l2email = email;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(final int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public int getBanExpire() {
        return banExpire;
    }

    public void setBanExpire(final int banExpire) {
        this.banExpire = banExpire;
    }

    public String getLastIP() {
        return lastIP;
    }

    public void setLastIP(final String lastIP) {
        this.lastIP = lastIP;
    }

    public int getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(final int lastAccess) {
        this.lastAccess = lastAccess;
    }

    public int getLastServer() {
        return lastServer;
    }

    public void setLastServer(final int lastServer) {
        this.lastServer = lastServer;
    }

    public int getReportPoints() {
        return reportPoints;
    }

    public void setReportPoints(final int reportPoints) {
        this.reportPoints = reportPoints;
    }

    public void addAccountInfo(final int serverId, final int size, final int[] deleteChars) {
        serversInfo.put(serverId, new ImmutablePair<>(size, deleteChars));
    }

    public Pair<Integer, int[]> getAccountInfo(final int serverId) {
        return serversInfo.get(serverId);
    }

    @Override
    public String toString() {
        return login;
    }

    public void restore() {
        AccountsDAO.getInstance().restore(this);
    }

    public void save() {
        AccountsDAO.getInstance().save(this);
    }

    public void update() {
        AccountsDAO.getInstance().update(this);
    }

    public Premium getPremium() {
        return premium;
    }

    public void setPremium(Premium premium) {
        this.premium = premium;
    }
}
