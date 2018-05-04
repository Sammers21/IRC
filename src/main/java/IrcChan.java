import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.LinkedList;

public class IrcChan {
    private final String name;
    private final int storeLastMessages;
    private final int maxClients;
    private final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final LinkedList<String> lastMessages = new LinkedList();

    public IrcChan(String name, int storeLastMessages, int maxClients) {
        this.name = name;
        this.storeLastMessages = storeLastMessages;
        this.maxClients = maxClients;
    }

    public void addUser(User user) {
        IrcChan ircChan = user.ircChan();
        Channel userChannel = user.channel();
        boolean joinSuccess;
        synchronized (channels) {
            if (channels.size() + 1 > maxClients) {
                joinSuccess = false;
            } else {
                if (ircChan != null && ircChan != this) {
                    ircChan.channels.remove(userChannel);
                }
                user.setIrcChan(this);
                channels.add(userChannel);
                joinSuccess = true;
            }
        }
        if (joinSuccess) {
            userChannel.writeAndFlush(String.format("You have successfully entered %s channel%s", name, IRCSever.DELIM));
            synchronized (lastMessages) {
                for (String message : lastMessages) {
                    userChannel.writeAndFlush(message);
                }
            }
        } else {
            userChannel.writeAndFlush(String.format("Sorry, channel %s is already full%s", name, IRCSever.DELIM));
        }
    }

    public void postNewMessageFromUser(String message, User user) {
        String msgToPost = String.format("[%s]: %s%s", user.login(), message, IRCSever.DELIM);
        synchronized (lastMessages) {
            if (lastMessages.size() + 1 > storeLastMessages) {
                lastMessages.removeLast();
            }
            lastMessages.addFirst(msgToPost);
        }
        channels.writeAndFlush(msgToPost, channel -> channel != user.channel());
    }

    public String name() {
        return name;
    }

    public ChannelGroup channels() {
        return channels;
    }
}