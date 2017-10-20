package eu.epitech.jcoinche.jcoincheclient.client.networking;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;

import io.netty.channel.socket.SocketChannel;

public class ClientInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext ssc;

    public ClientInitializer(SslContext sslCtx) {
        System.out.println("Client is initializing");
        this.ssc = sslCtx;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
//        if (ssc != null) {
//            //pipeline.addLast(ssc.newHandler(ch.alloc(), Connection.getPort(), Integer.parseInt(Connection.getHost())));
//        }
//
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());
        pipeline.addLast("clientHandler", new ClientHandler());
    }
}
