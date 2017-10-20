package eu.epitech.jcoinche.jcoincheserver.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext ctx;

    public ServerInitializer(SslContext ctx) {
        System.out.print("Hello Server\n");
        this.ctx = ctx;
    }

    public void initChannel(SocketChannel ch) {
        System.out.print("Initchannel\n");
        ChannelPipeline pipeline = ch.pipeline();

        //pipeline.addLast(ctx.newHandler(ch.alloc()));
        pipeline.addLast("playerHandler", new PlayerHandler());
    }
}
