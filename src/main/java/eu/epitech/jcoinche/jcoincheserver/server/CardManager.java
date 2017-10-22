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
	
	public enum Trump {
		UNDEFINED,
		DIAMONDS,
		HEARTS,
		CLUBS,
		SPADES,
		ALL_TRUMPS,
		NO_TRUMPS;
	}

	private ArrayList<Coinche.Card> cardList = new ArrayList<>();
	private Map<scoreScale, Map<Coinche.Card.Value, Integer>> correspondenceTable = new HashMap<>();
	private Trump currentTrump;

	public CardManager() {
		List<Coinche.Card.Type> typeList = Arrays.asList(Coinche.Card.Type.DIAMONDS, Coinche.Card.Type.HEARTS, Coinche.Card.Type.CLUBS, Coinche.Card.Type.SPADES);
		List<Coinche.Card.Value> valueList = Arrays.asList(Coinche.Card.Value.ACE, Coinche.Card.Value.KING, Coinche.Card.Value.QUEEN, Coinche.Card.Value.JACK, Coinche.Card.Value.TEN, Coinche.Card.Value.NINE, Coinche.Card.Value.EIGHT, Coinche.Card.Value.SEVEN);
		this.currentTrump = Trump.UNDEFINED;

		for (Coinche.Card.Type aTypeList : typeList) {
			for (Coinche.Card.Value aValueList : valueList) {
				Coinche.Card card = Coinche.Card.newBuilder()
						.setType(aTypeList)
						.setValue(aValueList)
						.build();
				this.cardList.add(card);
			}
		}

		correspondenceTable.put(STANDARD, new HashMap<Coinche.Card.Value, Integer>());
		correspondenceTable.get(STANDARD).put(Coinche.Card.Value.ACE, 11);
		correspondenceTable.get(STANDARD).put(Coinche.Card.Value.KING, 4);
		correspondenceTable.get(STANDARD).put(Coinche.Card.Value.QUEEN, 3);
		correspondenceTable.get(STANDARD).put(Coinche.Card.Value.JACK, 2);
		correspondenceTable.get(STANDARD).put(Coinche.Card.Value.TEN, 10);
		correspondenceTable.get(STANDARD).put(Coinche.Card.Value.NINE, 0);
		correspondenceTable.get(STANDARD).put(Coinche.Card.Value.EIGHT, 0);
		correspondenceTable.get(STANDARD).put(Coinche.Card.Value.SEVEN, 0);

		correspondenceTable.put(TRUMP, new HashMap<Coinche.Card.Value, Integer>());
		correspondenceTable.get(TRUMP).put(Coinche.Card.Value.ACE, 11);
		correspondenceTable.get(TRUMP).put(Coinche.Card.Value.KING, 4);
		correspondenceTable.get(TRUMP).put(Coinche.Card.Value.QUEEN, 3);
		correspondenceTable.get(TRUMP).put(Coinche.Card.Value.JACK, 20);
		correspondenceTable.get(TRUMP).put(Coinche.Card.Value.TEN, 10);
		correspondenceTable.get(TRUMP).put(Coinche.Card.Value.NINE, 14);
		correspondenceTable.get(TRUMP).put(Coinche.Card.Value.EIGHT, 0);
		correspondenceTable.get(TRUMP).put(Coinche.Card.Value.SEVEN, 0);

		correspondenceTable.put(ALL_TRUMPS, new HashMap<Coinche.Card.Value, Integer>());
		correspondenceTable.get(ALL_TRUMPS).put(Coinche.Card.Value.ACE, 6);
		correspondenceTable.get(ALL_TRUMPS).put(Coinche.Card.Value.KING, 3);
		correspondenceTable.get(ALL_TRUMPS).put(Coinche.Card.Value.QUEEN, 1);
		correspondenceTable.get(ALL_TRUMPS).put(Coinche.Card.Value.JACK, 14);
		correspondenceTable.get(ALL_TRUMPS).put(Coinche.Card.Value.TEN, 5);
		correspondenceTable.get(ALL_TRUMPS).put(Coinche.Card.Value.NINE, 9);
		correspondenceTable.get(ALL_TRUMPS).put(Coinche.Card.Value.EIGHT, 0);
		correspondenceTable.get(ALL_TRUMPS).put(Coinche.Card.Value.SEVEN, 0);

		correspondenceTable.put(NO_TRUMPS, new HashMap<Coinche.Card.Value, Integer>());
		correspondenceTable.get(NO_TRUMPS).put(Coinche.Card.Value.ACE, 19);
		correspondenceTable.get(NO_TRUMPS).put(Coinche.Card.Value.KING, 4);
		correspondenceTable.get(NO_TRUMPS).put(Coinche.Card.Value.QUEEN, 3);
		correspondenceTable.get(NO_TRUMPS).put(Coinche.Card.Value.JACK, 2);
		correspondenceTable.get(NO_TRUMPS).put(Coinche.Card.Value.TEN, 10);
		correspondenceTable.get(NO_TRUMPS).put(Coinche.Card.Value.NINE, 0);
		correspondenceTable.get(NO_TRUMPS).put(Coinche.Card.Value.EIGHT, 0);
		correspondenceTable.get(NO_TRUMPS).put(Coinche.Card.Value.SEVEN, 0);
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
		ArrayList<Coinche.Card> cardsToDistribute = new ArrayList<>();

		cardsToDistribute.addAll(this.cardList);
		for (int n = 0; n < 3; n++) {
			for (Player aPlayerList : playerList) {
				for (int j = 0; j < nbCardsToDistribute.get(n); j++) {
					aPlayerList.addToHand(cardsToDistribute.get(0));
					cardsToDistribute.remove(0);
				}
			}
		}
	}
	
	public int getCardScore(Coinche.Card card) {
		if (this.currentTrump == Trump.UNDEFINED)
			return (-1);
		else {
			scoreScale trumpType;
			if (this.currentTrump == Trump.DIAMONDS || this.currentTrump == Trump.HEARTS || this.currentTrump == Trump.CLUBS || this.currentTrump == Trump.SPADES)
				trumpType = STANDARD;
			else if (this.currentTrump == Trump.ALL_TRUMPS)
				trumpType = ALL_TRUMPS;
			else
				trumpType = NO_TRUMPS;
			return (this.correspondenceTable.get(trumpType).get(card.getValue()));
		}
	}

	public int compareCards(Coinche.Card card1, Coinche.Card card2) {
		if (Objects.equals(card1.getType().name(), this.currentTrump.name()) &&
				!Objects.equals(card2.getType().name(), this.currentTrump.name()))
			return (1);
		else if (Objects.equals(card2.getType().name(), this.currentTrump.name()) &&
				!Objects.equals(card1.getType().name(), this.currentTrump.name()))
			return (-1);
		else
			return (this.getCardScore(card1) - this.getCardScore(card2));
	}

	public Player getHighestCard(Map<Player, Coinche.Card> cards) {
		if (cards.size() == 0)
			return (null);
		Player max = null;
		for (Map.Entry<Player, Coinche.Card> entry : cards.entrySet()) {
			if (max == null)
				max = entry.getKey();
			if (this.compareCards(cards.get(max), entry.getValue()) < 0) {
				max = entry.getKey();
			}
		}
		return (max);
	}

	public boolean isTrumpQueen(Coinche.Card card) {
		return Objects.equals(card.getType().name(), this.getCurrentTrump().name()) && card.getValue() == Coinche.Card.Value.QUEEN;
	}

	public boolean isTrumpKing(Coinche.Card card) {
		return Objects.equals(card.getType().name(), this.getCurrentTrump().name()) && card.getValue() == Coinche.Card.Value.KING;
	}

	public Trump getCurrentTrump() {
		return currentTrump;
	}

	public void setCurrentTrump(Trump currentTrump) {
		this.currentTrump = currentTrump;
	}
}