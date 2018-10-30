package org.mmocore.authserver.manager;

import org.mmocore.authserver.accounts.Account;
import org.mmocore.authserver.configuration.config.LoginConfig;
import org.mmocore.authserver.network.lineage.SessionKey;
import org.mmocore.commons.threading.RunnableImpl;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private static final SessionManager _instance = new SessionManager();
    /**
     * Карта текущих сессий *
     */
    private final Map<SessionKey, Session> sessions = new ConcurrentHashMap<>();

    private SessionManager() {
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl() {

            @Override
            public void runImpl() {
                //Чистка просроченных сессий
                long currentMillis = System.currentTimeMillis();
                Session session;
                for (Iterator<Session> itr = sessions.values().iterator(); itr.hasNext(); ) {
                    session = itr.next();
                    if (session.getExpireTime() < currentMillis) {
                        itr.remove();
                    }
                }
            }

        }, 30000L, 30000L);
    }

    public static final SessionManager getInstance() {
        return _instance;
    }

    public Session openSession(Account account) {
        Session session = new Session(account);
        sessions.put(session.getSessionKey(), session);
        return session;
    }

    public Session closeSession(SessionKey skey) {
        return sessions.remove(skey);
    }

    public Session getSessionByName(String name) {
        for (Session session : sessions.values()) {
            if (session.account.getLogin().equalsIgnoreCase(name)) {
                return session;
            }
        }
        return null;
    }

    public static final class Session {
        private final Account account;
        private final SessionKey skey;
        private final long expireTime;

        private Session(Account account) {
            this.account = account;
            this.skey = SessionKey.create();
            this.expireTime = System.currentTimeMillis() + LoginConfig.LOGIN_TIMEOUT;
        }

        public SessionKey getSessionKey() {
            return skey;
        }

        public Account getAccount() {
            return account;
        }

        public long getExpireTime() {
            return expireTime;
        }
    }
}
