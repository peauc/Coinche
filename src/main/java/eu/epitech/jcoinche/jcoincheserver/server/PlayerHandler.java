package eu.epitech.jcoinche.jcoincheserver.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class    PlayerHandler extends SimpleChannelInboundHandler<String> {

    private static GameManager gm = new GameManager();
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        System.out.print("Client is going away :'(");
        gm.removePlayerAndStopGame(ctx);
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        Player p = new Player(ctx, "");

        gm.addPlayerToGame(p);
        ctx.writeAndFlush("Hello from server :)\n");
        System.out.print("Channel Active\n");
    }
    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, String message) throws Exception {
        System.out.println(message);
    }
}
