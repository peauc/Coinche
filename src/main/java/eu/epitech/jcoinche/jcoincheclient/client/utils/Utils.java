package eu.epitech.jcoinche.jcoincheclient.client.utils;

import eu.epitech.jcoinche.protocol.Coinche;

public class Utils {
    public static void HandToString(Coinche.Hand hand) {
        Integer i = 0;

        for (Coinche.Card card : hand.getCardList()) {
            System.out.println("Card number " + i++ + " : " + card.getTypeValue() + " of "+ card.getType().name());
        }
    }

    public static String getArguments(String line) {
        Integer pos;

        pos = line.indexOf(" ") + 1;
        if (pos == -1)
            return ("");
        return (line.substring(pos));
    }
}
