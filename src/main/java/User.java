import io.netty.channel.Channel;

public class User {
    private String login;
    private String password;
    private IrcChan ircChan;
    private Channel channel;


    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public IrcChan ircChan() {
        return ircChan;
    }

    public void setIrcChan(IrcChan ircChan) {
        this.ircChan = ircChan;
    }

    public Channel channel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String login() {
        return login;
    }

    public String password() {
        return password;
    }
}
