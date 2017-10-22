package eu.epitech.jcoinche.jcoincheclient.client.networking;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.bootstrap.Bootstrap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.ConnectException;

public class Connection {
    private boolean shouldConnect = true;
    private static String Port;
    private static String Host;
    private SelfSignedCertificate ssc;
    private SslContext sslCtx;
    private Bootstrap bootsrap;
    private static EventLoopGroup loopGroup;
    private static Channel channel;

    public boolean shouldConnect() {
        return shouldConnect;
    }

    public void setShouldConnect(boolean shouldConnect) {
        this.shouldConnect = shouldConnect;
    }

    static String getPort() {
        return Port;
    }

    public void setPort(String Port) {
        if (Port.isEmpty())
            Connection.Port = "8090";
        else
            Connection.Port = Port;
    }

    static String getHost() {
        return Host;
    }

    public void setHost(String Host) {
        if (Host.isEmpty())
            Connection.Host = "localhost";
        else
            Connection.Host = Host;
    }

    public SelfSignedCertificate getSsc() {
        return ssc;
    }

    public void setSsc(SelfSignedCertificate ssc) {
        this.ssc = ssc;
    }

    public SslContext getSslCtx() {
        return sslCtx;
    }

    public void setSslCtx(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    public Bootstrap getBootsrap() {
        return bootsrap;
    }

    public void setBootsrap(Bootstrap bootsrap) {
        this.bootsrap = bootsrap;
    }

    public static EventLoopGroup getLoopGroup() {
        return loopGroup;
    }

    public static void setLoopGroup(EventLoopGroup loopGroup) {
        Connection.loopGroup = loopGroup;
    }

    public static Channel getChannel() {
        return channel;
    }

    public static void setChannel(Channel channel) {
        Connection.channel = channel;
    }

    public void connect () throws ConnectException {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            bootsrap = new Bootstrap();
            loopGroup = new NioEventLoopGroup();
            bootsrap.group(loopGroup).channel(NioSocketChannel.class).handler(new ClientInitializer(sslCtx));
//            System.out.println("Please input server's host (enter set default values)");
//            setHost(in.readLine());
//            System.out.println("Please input server's port (enter set default values)");
//            setPort(in.readLine());
            channel = bootsrap.connect(Host, Integer.parseInt(Port)).sync().channel();

        }
        catch (Exception e) {
            System.out.println("Can't connect to " + Host + ":" + Port);
        }
    }
}
