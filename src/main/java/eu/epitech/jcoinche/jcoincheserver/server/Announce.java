package eu.epitech.jcoinche.jcoincheserver.server;

import eu.epitech.jcoinche.protocol.Coinche;

import java.util.ArrayList;

import static eu.epitech.jcoinche.jcoincheserver.server.Announce.Type.CARRE;
import static eu.epitech.jcoinche.protocol.Coinche.Card.Type.*;

public class Announce {

	public class AnnounceCard {

		private boolean validated;
		private Coinche.Card card;

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
	}

	public enum Type {
		CARRE(5, 4),
		CENT(3, 5),
		CINQUANTE(4, 4),
		TIERCE(5, 3);

		public final int minimalCardValue;
		public final int nbOfcards;

		Type(int minimalCardValue, int nbOfcards) {
			this.minimalCardValue = minimalCardValue;
			this.nbOfcards = nbOfcards;
		}
	}

	Player player;
	ArrayList<AnnounceCard> cardsToValidate;
	boolean order;

	public Announce (Type type, Coinche.Card card) {
		this.order = type != CARRE;

		if (type == CARRE) {
			this.order = false;
			this.cardsToValidate.add(new AnnounceCard(Coinche.Card.newBuilder()
					.setType(DIAMONDS)
					.setValue(card.getValue())
					.build()));
			this.cardsToValidate.add(new AnnounceCard(Coinche.Card.newBuilder()
					.setType(HEARTS)
					.setValue(card.getValue())
					.build()));
			this.cardsToValidate.add(new AnnounceCard(Coinche.Card.newBuilder()
					.setType(CLUBS)
					.setValue(card.getValue())
					.build()));
			this.cardsToValidate.add(new AnnounceCard(Coinche.Card.newBuilder()
					.setType(SPADES)
					.setValue(card.getValue())
					.build()));
		} else {
			this.order = true;
		}
	}
}
