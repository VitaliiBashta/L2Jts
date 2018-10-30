package org.mmocore.gameserver.network.telnet;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.mmocore.gameserver.configuration.config.TelnetConfig;

import java.nio.charset.Charset;

/**
 * @author Kolobrodik
 */
public class TelnetServerInitializer extends ChannelInitializer<SocketChannel> {
    private static final StringDecoder DECODER = new StringDecoder(Charset.forName(TelnetConfig.TELNET_DEFAULT_ENCODING));
    private static final StringEncoder ENCODER = new StringEncoder(Charset.forName(TelnetConfig.TELNET_DEFAULT_ENCODING));
    private static final TelnetServerHandler SERVERHANDLER = new TelnetServerHandler();

    @Override
    public void initChannel(final SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();

        // Add the text line codec combination first,
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        // the encoder and decoder are static as these are sharable
        pipeline.addLast("decoder", DECODER);
        pipeline.addLast("encoder", ENCODER);

        // and then business logic.
        pipeline.addLast("handler", SERVERHANDLER);
    }
}
