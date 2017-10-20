package eu.epitech.jcoinche.jcoincheclient.client.networking;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.SslContext;

public class ClientHandler extends SimpleChannelInboundHandler<String> {
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush("HelloWorld\n");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg);
    }
}
