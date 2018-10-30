package org.mmocore.gameserver.network.telnet;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.mmocore.gameserver.configuration.config.TelnetConfig;

import java.net.InetSocketAddress;

/**
 * @author Kolobrodik
 */
public class TelnetServer {
    // Configure the server.
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    public TelnetServer() throws InterruptedException {
        final InetSocketAddress address = new InetSocketAddress("*".equals(TelnetConfig.TELNET_HOSTNAME) ? null : TelnetConfig.TELNET_HOSTNAME, TelnetConfig.TELNET_PORT);

        final ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new TelnetServerInitializer());

        b.bind(address).sync();
    }

    public void shutdown() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
