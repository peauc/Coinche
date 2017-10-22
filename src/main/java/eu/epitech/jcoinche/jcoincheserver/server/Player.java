package eu.epitech.jcoinche.jcoincheserver.server;

import eu.epitech.jcoinche.protocol.Coinche;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Objects;

public class Player {

	public enum beloteState {
		UNDECLARED,
		DECLARED,
		DONE
	}

	private ChannelHandlerContext chctx;
	private String name;
	private ArrayList<Coinche.Card> hand;
	private beloteState belote;
	private beloteState rebelote;

	public Player(ChannelHandlerContext chctx, String name) {
		this.hand = new ArrayList<>();
		this.chctx = chctx;
		this.name = name;
		this.belote = beloteState.UNDECLARED;
		this.rebelote = beloteState.UNDECLARED;
	}

	public void sendMessage(Coinche.Message message) {
		this.chctx.writeAndFlush(message);
	}

	public void sendHand() {
		Coinche.Hand hand = Coinche.Hand.newBuilder()
				.addAllCard(this.hand)
				.build();
		Coinche.Message message = Coinche.Message.newBuilder()
				.setType(Coinche.Message.Type.HAND)
				.setHand(hand)
				.build();
		this.sendMessage(message);
	}

	public ChannelHandlerContext getChctx() {
		return chctx;
	}

	public void setChctx(ChannelHandlerContext chctx) {
		this.chctx = chctx;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Coinche.Card> getHand() {
		return hand;
	}

	public void setHand(ArrayList<Coinche.Card> hand) {
		this.hand = hand;
	}

	public boolean hasInHand(Coinche.Card card) {
		for (Coinche.Card aHand : this.hand) {
			if (card.getType() == aHand.getType() && card.getValue() == aHand.getValue()) {
				return (true);
			}
		}
		return (false);
	}

	public boolean hasEntameInHand(Coinche.Card.Type entame) {
		for (Coinche.Card card : this.hand) {
			if (card.getType() == entame)
				return (true);
		}
		return (false);
	}

	public boolean hasTrumpInHand(CardManager.Trump trump) {
		for (Coinche.Card card : this.hand) {
			if (Objects.equals(card.getType().name(), trump.name()))
				return (true);
		}
		return (false);
	}

	public boolean hasHigherTrumpInHand(CardManager cm, Coinche.Card other) {
		for (Coinche.Card card : this.hand) {
			if (Objects.equals(cm.getCurrentTrump().name(), card.getType().name()) && cm.compareCards(card, other) > 0)
				return (true);
		}
		return (false);
	}

	public void prompt(String toPrompt) {
		Coinche.Message message = Coinche.Message.newBuilder()
				.setType(Coinche.Message.Type.PROMPT)
				.setPrompt(Coinche.Prompt.newBuilder()
						.addToDisplay(toPrompt)
						.build())
				.build();
		this.sendMessage(message);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!Player.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		final Player other = (Player)obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		if (this.chctx != other.chctx) {
			return false;
		}
		return true;
	}

	public void addToHand(Coinche.Card card) {
		this.hand.add(card);
	}

	public void removeFromHand(Coinche.Card card) {
		this.hand.remove(card);
	}

	public beloteState isBelote() {
		return belote;
	}

	public void setBelote(beloteState belote) {
		this.belote = belote;
	}

	public beloteState isRebelote() {
		return rebelote;
	}

	public void setRebelote(beloteState rebelote) {
		this.rebelote = rebelote;
	}
}
