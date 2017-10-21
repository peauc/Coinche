package eu.epitech.jcoinche.jcoincheserver.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext ctx;

    public ServerInitializer(SslContext ctx) {
        System.out.print("Server is initializing\n");
        this.ctx = ctx;
    }

    public void initChannel(SocketChannel ch) throws Exception {
        System.out.print("Initchannel\n");
        ChannelPipeline pipeline = ch.pipeline();

        //pipeline.addLast(ctx.newHandler(ch.alloc()));
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());
        pipeline.addLast("playerHandler", new PlayerHandler());
    }
}
