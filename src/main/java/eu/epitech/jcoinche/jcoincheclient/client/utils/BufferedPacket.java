package eu.epitech.jcoinche.jcoincheclient.client.utils;

import eu.epitech.jcoinche.protocol.Coinche;

public class BufferedPacket {
    private static Coinche.Message _packet;

    public static Coinche.Message get_packet() {
        return _packet;
    }

    public static void set_packet(Coinche.Message _packet) {
        BufferedPacket._packet = _packet;
    }
}
