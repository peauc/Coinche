package eu.epitech.jcoinche.jcoincheclient.client.networking;

import eu.epitech.jcoinche.jcoincheclient.client.utils.BufferedPacket;
import eu.epitech.jcoinche.protocol.Coinche;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.SslContext;

import java.io.BufferedReader;
import java.net.InetAddress;

public class ClientHandler extends SimpleChannelInboundHandler<Coinche.Message> {
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Server did not respond in time");
        System.exit(84);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Coinche.Message msg) throws Exception {
        System.out.println("Client Channel Read0");
        System.out.println(msg.toString());
        BufferedPacket.set_packet(msg);
    }
}
