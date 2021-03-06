package eu.epitech.jcoinche.jcoincheclient.client.utils;

import eu.epitech.jcoinche.protocol.Coinche;

public class Utils {
    public static void HandToString(Coinche.Hand hand) {
        Integer i = 0;

        for (Coinche.Card card : hand.getCardList()) {
            System.out.println("[SERVER] " + i++ + " : " + card.getValue().name() + " of "+ card.getType().name());
        }
    }

    public static Boolean hasArguments(String line) {
        return (line.contains(" ") && !line.startsWith(" "));
    }

    public static String getArguments(String line) {
        Integer pos;

        pos = line.indexOf(" ") + 1;
        if (pos == -1)
            return ("");
        return (line.substring(pos));
    }

    public static boolean hasEnoughArguments(String string, int i) {
        return (string.split("\n").length == i);
    }
}
