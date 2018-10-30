package org.mmocore.authserver.network.xmlrpc.handler;

import org.mmocore.authserver.accounts.Account;
import org.mmocore.authserver.database.dao.impl.AccountsDAO;
import org.mmocore.authserver.manager.IpBanManager;
import org.mmocore.commons.crypt.PBKDF2Hash;
import org.mmocore.commons.jdbchelper.NoResultException;
import org.mmocore.commons.net.xmlrpc.handler.Handler;

/**
 * @author KilRoy
 */
public class AccountHandler extends Handler {
    public String requestAccountInfo(final String login, final String password, final String ip) {
        try {
            if (IpBanManager.getInstance().isIpBanned(ip)) {
                return ACC_BANNED;
            }
            final Account acc = new Account(login);
            acc.restore();
            if (acc.getPasswordHash() == null) {
                return NOT_FIND;
            }
            if (acc.getCheckEmail() != 1) {
                return EMAIL_NOT_CHECKED;
            }
            if (!acc.isAllowedIP(ip)) {
                return IP_LOCK_NOT_VALID;
            }
            if (acc.getAccessLevel() < 0) {
                return ACC_BANNED;
            }
            if (!PBKDF2Hash.validatePassword(password, acc.getPasswordHash())) {
                return NOT_VALID;
            } else {
                return String.valueOf(acc.getAccountId());
            }
        } catch (Exception e) {
            LOGGER.error("AccountHandler.requestAccountInfo(String, String, String) return exception: ", e);
            return ERROR;
        }
    }

    public String setAccountInfo(final int accountId, final long date) {
        System.out.println("ID: " + accountId + " password: " + date);
        try {
            //AccountsDAO.getInstance().setLastAccesTime(accountId, date);
        } catch (Exception e) {
            LOGGER.error("AccountHandler.requestAccountInfo(String, String, String) return exception: ", e);
            return ERROR;
        }
        return OK;
    }

    public String addAccountOnSite(final String login, final String password) {
        try {
            final Account acc = new Account(login);
            acc.restore();
            if (acc.getPasswordHash() == null) {
                return NOT_FIND;
            } else if (!PBKDF2Hash.validatePassword(password, acc.getPasswordHash())) {
                return NOT_VALID;
            } else {
                return OK;
            }
        } catch (Exception e) {
            LOGGER.error("AccountHandler.createAccount(String, String) return exception: ", e);
            return ERROR;
        }
    }

    public String getAccountPremiumPoint(final String account) {
        String response;
        try {
            response = String.valueOf(AccountsDAO.getInstance().requestGamePoints(account));
        } catch (NoResultException nre) {
            response = NOT_FIND;
        } catch (Exception e) {
            LOGGER.error("AccountHandler.getAccountPremiumPoint(String) return exception: ", e);
            response = ERROR;
        }
        return response;
    }
}