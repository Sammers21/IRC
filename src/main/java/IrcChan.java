import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.LinkedList;

/**
 * Represent a channel on the server.
 */
public class IrcChan {

    private final String name;
    private final int storeLastMessages;
    private final int maxClients;
    private final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final LinkedList<String> lastMessages = new LinkedList();

    /**
     * Construct a new channel.
     *
     * @param name              channel name
     * @param storeLastMessages how many last messages should be stored
     * @param maxClients        how many clients can be in the channel at the same time
     */
    public IrcChan(String name, int storeLastMessages, int maxClients) {
        this.name = name;
        this.storeLastMessages = storeLastMessages;
        this.maxClients = maxClients;
    }

    /**
     * Add the users to the channel.
     *
     * @param user user to add
     */
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

    /**
     * Post the message to the channel.
     *
     * @param message message to post
     * @param user    message sender
     */
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

    /**
     * @return channel name
     */
    public String name() {
        return name;
    }

    /**
     * @return current connections
     */
    public ChannelGroup channels() {
        return channels;
    }
}