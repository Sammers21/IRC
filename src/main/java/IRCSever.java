import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

import java.util.concurrent.ConcurrentHashMap;

public class IRCSever {

    public static String DELIM = "\r\n";

    private static final String LOGIN_PLEASE = "Before sending a message you should be authorized";
    private static final String JOIN_A_CHANNEL_PLEASE = "Before sending a message you should join a channel";
    private static final String SHOULD_BE_IN_A_CHANNEL_FOR_REQUESTING_LIST_USERS = "In order to get the user list you should join a channel";
    private static final String INVALID_PASSWORD = "Invalid password";
    private static final String ALREADY_AUTHORIZED = "You already authorized on another device";

    private static final int ALLOWED_CLIENTS_IN_ONE_CHANNEL = 2;
    private static final int LAST_MESSAGES_STORED = 10;

    private ConcurrentHashMap<String, User> loginAndUser = new ConcurrentHashMap<>();
    private ConcurrentHashMap<ChannelId, User> channelAndUser = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, IrcChan> ircChannels = new ConcurrentHashMap<>();

    void handleInComingMessage(ChannelHandlerContext ctx, String message) {
        User user = channelAndUser.get(ctx.channel().id());
        if (user != null) {
            if (user.ircChan() != null) {
                user.ircChan().postNewMessageFromUser(message, user);
            } else {
                ctx.writeAndFlush(JOIN_A_CHANNEL_PLEASE + DELIM);
            }
        } else {
            ctx.writeAndFlush(LOGIN_PLEASE + DELIM);
        }
    }

    void handleUsers(ChannelHandlerContext ctx) {
        User user = channelAndUser.get(ctx.channel().id());
        if (user != null) {
            IrcChan ircChan = user.ircChan();
            if (ircChan != null) {
                for (Channel channel : ircChan.channels()) {
                    User relatedToChannelUser = channelAndUser.get(channel.id());
                    if (relatedToChannelUser != null) {
                        ctx.write(relatedToChannelUser.login() + DELIM);
                    }
                }
            } else {
                ctx.writeAndFlush(SHOULD_BE_IN_A_CHANNEL_FOR_REQUESTING_LIST_USERS + DELIM);
            }
            ctx.flush();
        } else {
            ctx.writeAndFlush(LOGIN_PLEASE + DELIM);
        }
    }

    void handleJoin(ChannelHandlerContext ctx, String channelName) {
        User user = channelAndUser.get(ctx.channel().id());
        if (user != null) {
            IrcChan ircChan = ircChannels.computeIfAbsent(channelName, s -> new IrcChan(s, LAST_MESSAGES_STORED, ALLOWED_CLIENTS_IN_ONE_CHANNEL));
            ircChan.addUser(user);
        } else {
            ctx.writeAndFlush(LOGIN_PLEASE + DELIM);
        }
    }

    void handleLogin(ChannelHandlerContext ctx, String login, String password) {
        User previous = loginAndUser.putIfAbsent(login, new User(login, password));
        // User is exist
        if (previous != null) {
            if (previous.password().equals(password)) {
                loginTheConnection(ctx, login);
            } else {
                ctx.writeAndFlush(INVALID_PASSWORD + DELIM);
            }
        }
        // new login is created
        else {
            loginTheConnection(ctx, login);
        }
    }

    private void loginTheConnection(ChannelHandlerContext ctx, String login) {
        User user = loginAndUser.get(login);
        Channel channel = ctx.channel();
        synchronized (user) {
            if (user.channel() != null && user.channel() != ctx.channel()) {
                ctx.writeAndFlush(ALREADY_AUTHORIZED);
                return;
            }
            user.setChannel(channel);
            channelAndUser.put(channel.id(), user);
        }

        // disable login session on disconnect
        channel.closeFuture().addListener(v -> {
            synchronized (user) {
                channelAndUser.remove(channel.id());
                user.setChannel(null);
            }
        });
    }

}