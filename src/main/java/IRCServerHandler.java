import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handler embedded into the netty pipeline.
 */
public class IRCServerHandler extends SimpleChannelInboundHandler<String> {

    private static final Pattern LOGIN_COMMAND = Pattern.compile("^/login (\\w+) (\\w+)$");
    private static final Pattern JOIN_COMMAND = Pattern.compile("^/join (\\w+)$");
    private static final Pattern LEAVE_COMMAND = Pattern.compile("^/leave$");
    private static final Pattern USERS_COMMAND = Pattern.compile("^/users$");
    private final IRCSever ircSever;

    /**
     * Construct a new handler, which delegates all user events to the given {@link IRCSever} instance
     *
     * @param ircSever the server to delegate all events
     */
    IRCServerHandler(IRCSever ircSever) {
        this.ircSever = ircSever;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, String request) {
        String reqWithTrim = request.trim();
        Matcher leave = LEAVE_COMMAND.matcher(reqWithTrim);
        if (leave.matches()) {
            ctx.close();
            return;
        }
        Matcher login = LOGIN_COMMAND.matcher(reqWithTrim);
        if (login.matches()) {
            ircSever.handleLogin(ctx, login.group(1), login.group(2));
            return;
        }
        Matcher join = JOIN_COMMAND.matcher(reqWithTrim);
        if (join.matches()) {
            ircSever.handleJoin(ctx, join.group(1));
            return;
        }
        Matcher users = USERS_COMMAND.matcher(reqWithTrim);
        if (users.matches()) {
            ircSever.handleUsers(ctx);
            return;
        }

        // at this point we can assume that the user sent a common text message
        ircSever.handleInComingMessage(ctx, request);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}