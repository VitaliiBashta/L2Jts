package org.mmocore.gameserver.network.telnet;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.configuration.config.TelnetConfig;
import org.mmocore.gameserver.handler.telnetcommands.TelnetCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kolobrodik
 */
public class TelnetServerHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger _log = LoggerFactory.getLogger(TelnetServerHandler.class);

    private static final AttributeKey<Boolean> PASSWORD_ENTERED = new AttributeKey<>("password_entered");

    //The following regex splits a line into its parts, separated by spaces, unless there are quotes, in which case the quotes take precedence.
    private static final Pattern COMMAND_ARGS_PATTERN = Pattern.compile("\"([^\"]*)\"|([^\\s]+)");

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        // Send greeting for a new connection.
        ctx.write("Welcome to Lineage II GameServer telnet console.\n");
        ctx.write("It is " + new Date() + " now.\n");
        if (!TelnetConfig.TELNET_PASSWORD.isEmpty()) {
            // Ask password
            ctx.write("Password:");
            ctx.attr(PASSWORD_ENTERED).set(false);
        } else {
            ctx.write("Type 'help' to see all available commands.\n");
            ctx.attr(PASSWORD_ENTERED).set(true);
        }
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, String request) {
        // Generate and write a response.
        String response = null;
        boolean close = false;

        if (!ctx.attr(PASSWORD_ENTERED).get()) {
            if (TelnetConfig.TELNET_PASSWORD.equals(request)) {
                ctx.attr(PASSWORD_ENTERED).set(true);
                request = StringUtils.EMPTY;
            } else {
                response = "Wrong password!\n";
                close = true;
            }
        }

        if (ctx.attr(PASSWORD_ENTERED).get()) {
            if (request.isEmpty()) {
                response = "Type 'help' to see all available commands.\n";
            } else if ("exit".equals(request.toLowerCase())) {
                response = "Have a good day!\n";
                close = true;
            } else {
                final Matcher m = COMMAND_ARGS_PATTERN.matcher(request);

                m.find();
                final String command = m.group();

                final List<String> args = new ArrayList<>();
                String arg;
                while (m.find()) {
                    arg = m.group(1);
                    if (arg == null) {
                        arg = m.group(0);
                    }
                    args.add(arg);
                }

                response = TelnetCommandHandler.getInstance().tryHandleCommand(command,
                        args.toArray(new String[args.size()]));
            }
        }

        // We do not need to write a ChannelBuffer here.
        // We know the encoder inserted at TelnetServerInitializer will do the conversion.
        final ChannelFuture future = ctx.write(response);

        // Close the connection after sending 'Have a good day!'
        // if the client has sent 'exit'.
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        _log.warn("Unexpected exception from downstream.", cause);
        ctx.close();
    }
}
