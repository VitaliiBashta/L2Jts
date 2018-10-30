package org.mmocore.authserver.network.lineage;

import org.mmocore.authserver.configuration.config.LoginConfig;
import org.mmocore.authserver.manager.IpBanManager;
import org.mmocore.authserver.manager.ThreadPoolManager;
import org.mmocore.authserver.network.lineage.serverpackets.Init;
import org.mmocore.commons.net.nio.impl.IAcceptFilter;
import org.mmocore.commons.net.nio.impl.IClientFactory;
import org.mmocore.commons.net.nio.impl.IMMOExecutor;
import org.mmocore.commons.net.nio.impl.MMOConnection;
import org.mmocore.commons.threading.RunnableImpl;

import java.nio.channels.SocketChannel;

public class SelectorHelper implements IMMOExecutor<L2LoginClient>, IClientFactory<L2LoginClient>, IAcceptFilter {
    @Override
    public void execute(Runnable r) {
        ThreadPoolManager.getInstance().execute(r);
    }

    @Override
    public L2LoginClient create(MMOConnection<L2LoginClient> con) {
        final L2LoginClient client = new L2LoginClient(con);
        client.sendPacket(new Init(client));
        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() {
                client.closeNow(false);
            }
        }, LoginConfig.LOGIN_TIMEOUT);
        return client;
    }

    @Override
    public boolean accept(SocketChannel sc) {
        return !IpBanManager.getInstance().isIpBanned(sc.socket().getInetAddress().getHostAddress());
    }
}