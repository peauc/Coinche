package eu.epitech.jcoinche.jcoincheserver.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class PlayerHandler extends SimpleChannelInboundHandler {

    PlayerHandler() {
        System.out.print("PlayerHandler\n");
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

    }
}
