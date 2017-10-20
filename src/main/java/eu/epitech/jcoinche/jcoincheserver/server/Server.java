package eu.epitech.jcoinche.jcoincheserver.server;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.bootstrap.ServerBootstrap;


public class Server {
    static int Port = 8090;
    static EventLoopGroup gameGroup = null;

    public static void startServer() throws Exception {
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();

        gameGroup = new NioEventLoopGroup(1);
        try {
            ServerBootstrap s = new ServerBootstrap();

            s.group(gameGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ServerInitializer(sslCtx));
            s.bind(Port).sync().channel().closeFuture().sync();
        } finally {
            stopServer();
        }
    }

    public static void stopServer() {
        gameGroup.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        try {
            startServer();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(84);
        }
    }
}
