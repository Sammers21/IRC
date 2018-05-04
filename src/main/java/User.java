import io.netty.channel.Channel;

/**
 * Represent a user on the sever
 */
public class User {

    private final String login;
    private final String password;
    private volatile IrcChan ircChan;
    private volatile Channel channel;

    /**
     * Create a new user
     *
     * @param login    the login for the user
     * @param password the account password
     */
    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    /**
     * @return the channel user subscribed on,
     * or {@code null} if user is not subscribed on a channel.
     */
    public IrcChan ircChan() {
        return ircChan;
    }

    /**
     * @return the user login
     */
    public String login() {
        return login;
    }

    /**
     * @return the user password
     */
    public String password() {
        return password;
    }

    /**
     * @return connection associated with the user
     */
    public Channel channel() {
        return channel;
    }

    public void setIrcChan(IrcChan ircChan) {
        this.ircChan = ircChan;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
