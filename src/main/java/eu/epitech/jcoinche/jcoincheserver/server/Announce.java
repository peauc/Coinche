package eu.epitech.jcoinche.jcoincheserver.server;

import eu.epitech.jcoinche.protocol.Coinche;

import java.util.ArrayList;

import static eu.epitech.jcoinche.jcoincheserver.server.Announce.Type.CARRE;
import static eu.epitech.jcoinche.protocol.Coinche.Card.Value.JACK;
import static eu.epitech.jcoinche.protocol.Coinche.Card.Value.NINE;

public class Announce {

	public class AnnounceCard {

		private boolean validated;
		private final Coinche.Card card;

		public AnnounceCard(Coinche.Card card) {
			this.validated = false;
			this.card = card;
		}

		public void validate() {
			this.validated = true;
		}

		public boolean isValidated() {
			return validated;
		}

		public Coinche.Card getCard() {
			return card;
		}

		public int compareTo(AnnounceCard other) {
			return (other.card.getValueValue() - this.card.getValueValue());
		}
	}

	public enum Type {
		CARRE(5, 4, 100),
		CENT(3, 5, 100),
		CINQUANTE(4, 4, 50),
		TIERCE(5, 3, 20);

		public final int minimalCardValue;
		public final int nbOfcards;
		public final int reward;

		Type(int minimalCardValue, int nbOfcards, int reward) {
			this.minimalCardValue = minimalCardValue;
			this.nbOfcards = nbOfcards;
			this.reward = reward;
		}
	}

	private final Player player;
	private final boolean order;
	private final Type type;
	private ArrayList<AnnounceCard> cardsToValidate;
	private int reward;

	public Announce (Type type, Coinche.Card card, Player player) {
		this.player = player;
		this.type = type;
		this.reward = type.reward;

		if (type == CARRE) {
			this.order = false;
			if (card.getValue() == JACK)
				this.reward += 100;
			else if (card.getValue() == NINE)
				this.reward += 50;
			for (int i = 0; i < 4; i++) {
				this.cardsToValidate.add(new AnnounceCard(Coinche.Card.newBuilder()
						.setTypeValue(i)
						.setValue(card.getValue())
						.build()));
			}
		} else {
			this.order = true;
			for (int i = card.getValueValue() + type.nbOfcards - 1; i >= card.getValueValue(); i--) {
				this.cardsToValidate.add(new AnnounceCard(Coinche.Card.newBuilder()
						.setType(card.getType())
						.setValueValue(i)
						.build()));
			}
		}
	}

	public boolean validate(Coinche.Card card) {
		for (AnnounceCard aCardsToValidate : this.cardsToValidate) {
			if (aCardsToValidate.getCard().getValue() == card.getValue() &&
					aCardsToValidate.getCard().getType() == card.getType()) {
				aCardsToValidate.validate();
				return (true);
			} else if (this.order) {
				return (false);
			}
		}
		return (false);
	}

	public boolean isComplete() {
		for (AnnounceCard aCardsToValidate : this.cardsToValidate) {
			if (!aCardsToValidate.isValidated())
				return (false);
		}
		return (true);
	}

	public int compareTo(Announce announce)
	{
		if (announce.type != this.type)
			return (announce.type.ordinal() - this.type.ordinal());
		else
			return (this.cardsToValidate.get(0).compareTo(announce.cardsToValidate.get(0)));
	}

	public Type getType() {
		return type;
	}

	public int getReward() {
		return reward;
	}

	public ArrayList<AnnounceCard> getCardsToValidate() {
		return cardsToValidate;
	}
}