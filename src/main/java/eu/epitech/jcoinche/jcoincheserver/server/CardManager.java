package eu.epitech.jcoinche.jcoincheserver.server;

import eu.epitech.jcoinche.protocol.Coinche;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static eu.epitech.jcoinche.jcoincheserver.server.CardManager.scoreScale.*;

public class CardManager {

	public enum scoreScale {
		STANDARD,
		TRUMP,
		ALL_TRUMPS,
		NO_TRUMPS;
	}

	private ArrayList<Coinche.Card> cardList = new ArrayList<>();
	private Map<scoreScale, HashMap<Integer, Integer>> correspondenceTable = new HashMap<scoreScale, HashMap<Integer, Integer>>();

	public CardManager() {
		List<String> typeList = Arrays.asList("DIAMONDS", "HEARTS", "CLUBS", "SPADES");
		List<String> valueList = Arrays.asList("ACE", "KING", "QUEEN", "JACK", "TEN", "NINE", "EIGHT", "SEVEN");

		for (String aTypeList : typeList) {
			for (String aValueList : valueList) {
				Coinche.Card card = Coinche.Card.newBuilder()
						.setType(Coinche.Card.Type.valueOf((String) aTypeList))
						.setValue(Coinche.Card.Value.valueOf((String) aValueList))
						.build();
				this.cardList.add(card);
			}
		}

		correspondenceTable.put(STANDARD, new HashMap<Integer, Integer>());
		correspondenceTable.get(STANDARD).put(0, 11);
		correspondenceTable.get(STANDARD).put(1, 4);
		correspondenceTable.get(STANDARD).put(2, 3);
		correspondenceTable.get(STANDARD).put(3, 2);
		correspondenceTable.get(STANDARD).put(4, 10);
		correspondenceTable.get(STANDARD).put(5, 0);
		correspondenceTable.get(STANDARD).put(6, 0);
		correspondenceTable.get(STANDARD).put(7, 0);

		correspondenceTable.put(TRUMP, new HashMap<Integer, Integer>());
		correspondenceTable.get(TRUMP).put(0, 11);
		correspondenceTable.get(TRUMP).put(1, 4);
		correspondenceTable.get(TRUMP).put(2, 3);
		correspondenceTable.get(TRUMP).put(3, 20);
		correspondenceTable.get(TRUMP).put(4, 10);
		correspondenceTable.get(TRUMP).put(5, 14);
		correspondenceTable.get(TRUMP).put(6, 0);
		correspondenceTable.get(TRUMP).put(7, 0);

		correspondenceTable.put(ALL_TRUMPS, new HashMap<Integer, Integer>());
		correspondenceTable.get(ALL_TRUMPS).put(0, 6);
		correspondenceTable.get(ALL_TRUMPS).put(1, 3);
		correspondenceTable.get(ALL_TRUMPS).put(2, 1);
		correspondenceTable.get(ALL_TRUMPS).put(3, 14);
		correspondenceTable.get(ALL_TRUMPS).put(4, 5);
		correspondenceTable.get(ALL_TRUMPS).put(5, 9);
		correspondenceTable.get(ALL_TRUMPS).put(6, 0);
		correspondenceTable.get(ALL_TRUMPS).put(7, 0);

		correspondenceTable.put(NO_TRUMPS, new HashMap<Integer, Integer>());
		correspondenceTable.get(NO_TRUMPS).put(0, 19);
		correspondenceTable.get(NO_TRUMPS).put(1, 4);
		correspondenceTable.get(NO_TRUMPS).put(2, 3);
		correspondenceTable.get(NO_TRUMPS).put(3, 2);
		correspondenceTable.get(NO_TRUMPS).put(4, 10);
		correspondenceTable.get(NO_TRUMPS).put(5, 0);
		correspondenceTable.get(NO_TRUMPS).put(6, 0);
		correspondenceTable.get(NO_TRUMPS).put(7, 0);
	}

	public void mix() {
		ArrayList<Coinche.Card> newCardList = new ArrayList<>();

		while (this.cardList.size() > 0) {
			int randomNum = ThreadLocalRandom.current().nextInt(0, this.cardList.size());
			newCardList.add(this.cardList.get(randomNum));
			this.cardList.remove(randomNum);
		}
		this.cardList = new ArrayList<Coinche.Card>(newCardList);
	}

	public void distributeToAll(ArrayList<Player> playerList) {
		List<Integer> nbCardsToDistribute = Arrays.asList(3, 2, 3);
		ArrayList<Coinche.Card> cardsToDistribute = new ArrayList<>(this.cardList);

		for (int n = 0; n < 3; n++) {
			for (Player aPlayerList : playerList) {
				for (int j = 0; j < nbCardsToDistribute.get(n); j++) {
					aPlayerList.addToHand(cardsToDistribute.get(0));
					cardsToDistribute.remove(0);
				}
			}
		}
	}
}