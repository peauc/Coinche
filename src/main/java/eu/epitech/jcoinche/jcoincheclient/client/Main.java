package eu.epitech.jcoinche.jcoincheclient.client;

import com.google.protobuf.Descriptors;
import eu.epitech.jcoinche.jcoincheclient.client.networking.Connection;
import eu.epitech.jcoinche.jcoincheclient.client.utils.BufferedPacket;
import eu.epitech.jcoinche.protocol.Coinche;

import javax.management.Descriptor;
import java.net.ConnectException;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("HelloClient");

        Connection connect = new Connection();
        try {
            connect.connect();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Coinche.Message message = BufferedPacket.get_packet();
                    if (message != null) {
                        switch (message.getType()) {
                            case PROMPT: {
                                for (String s : message.getPrompt().getToDisplayList()) {
                                    System.out.println(s);
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
