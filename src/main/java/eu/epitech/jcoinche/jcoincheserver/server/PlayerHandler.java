package eu.epitech.jcoinche.jcoincheserver.server;

import eu.epitech.jcoinche.protocol.Coinche;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class    PlayerHandler extends SimpleChannelInboundHandler<Coinche.Message> {

    private static GameManager gm = new GameManager();
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        System.out.println("Client is going away :'(");
        gm.removePlayerAndStopGame(ctx);
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws UnknownHostException {
        Player p = new Player(ctx, "");
        gm.addPlayerToGame(p);
        Coinche.Message message = Coinche.Message.newBuilder().setType(Coinche.Message.Type.PROMPT).setPrompt(Coinche.Prompt.newBuilder().addToDisplay("Welcome to our Coinche Server hosted by " + InetAddress.getLocalHost().getHostName() + "\nRemember to chose a nickname by using \"NAME yourNickname\"").build()).build();
        ctx.writeAndFlush(message);
        System.out.println("New client connected and greeted");
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Coinche.Message message) throws Exception {
        Game g;
        Player p;

        if ((p = gm.findPlayerByCongext(ctx)) == null) {
            System.out.println("Unknown player");
            return;
        }
        if ((g = gm.findPlayerGame(p)) == null) {
            System.out.println("Unknown game");
            return;
        }
        g.handlePlay(message, p);
        System.out.println(message.toString());
    }
}
