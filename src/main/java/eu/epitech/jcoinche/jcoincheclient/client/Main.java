package eu.epitech.jcoinche.jcoincheclient.client;

import eu.epitech.jcoinche.jcoincheclient.client.StandardInputHandler.Parser;
import eu.epitech.jcoinche.jcoincheclient.client.networking.Connection;
import eu.epitech.jcoinche.jcoincheclient.client.utils.BufferedPacket;
import eu.epitech.jcoinche.jcoincheclient.client.utils.Utils;
import eu.epitech.jcoinche.protocol.Coinche;

import java.net.ConnectException;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) throws Exception {
        Connection connect = new Connection();
        try {
            connect.connect();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Parser parser = new Parser(Connection.getChannel());
                    while (parser.shouldParse()) {
                        parser.Parse();
                    }
                    Coinche.Message message = BufferedPacket.get_packet();
                    if (message != null) {
                        switch (message.getType()) {
                            case PROMPT: {
                                for (String s : message.getPrompt().getToDisplayList()) {
                                    System.out.println(s);
                                }
                                break ;
                            }
                            case HAND: {
                                Utils.HandToString(message.getHand());
                                break ;
                            }
                            case REPLY: {
                                switch (message.getReply().getNumber()) {
                                    case 200: { }
                                    default: {
                                        System.err.println("[SERVER] " + message.getReply().getMessage());
                                    }
                                }
                            }
                        }
                        if (message.equals(BufferedPacket.get_packet()))
                            BufferedPacket.set_packet(null);
                    }
                }
            }, 1000, 1000);
        }
        catch (ConnectException e) {
            System.exit(84);
        }
    }
}
