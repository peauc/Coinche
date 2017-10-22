package eu.epitech.jcoinche.jcoincheclient.client.utils;

import eu.epitech.jcoinche.protocol.Coinche;

public class Utils {
    public static void HandToString(Coinche.Hand hand) {
        Integer i = 0;

        for (Coinche.Card card : hand.getCardList()) {
            System.out.println("Card number " + i++ + " : " + card.getTypeValue() + " of "+ card.getType().name());
        }
    }
}
