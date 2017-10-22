package eu.epitech.jcoinche.jcoincheclient.client.utils;

import eu.epitech.jcoinche.protocol.Coinche;

public class MessageFactory {
    static public Coinche.Reply createReply(Integer code, String message) {
        Coinche.Reply m;
        m = Coinche.Reply.newBuilder().setMessage(message).setNumber(code).build();
        return (m);
    }

    static public Coinche.Message createMessageFromReply(Coinche.Reply reply) {
         return (Coinche.Message.newBuilder().setType(Coinche.Message.Type.REPLY).setReply(reply).build());
    }

    static public Coinche.Message createMessageAndReply(Integer code, String message) {
       Coinche.Reply m;
       m = createReply(code, message);
       return (createMessageFromReply(m));
    }
}
