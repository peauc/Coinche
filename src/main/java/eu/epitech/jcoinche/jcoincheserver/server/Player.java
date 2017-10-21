package eu.epitech.jcoinche.jcoincheserver.server;

import eu.epitech.jcoinche.protocol.Coinche;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Objects;

public class Player {
	private ChannelHandlerContext chctx;
	private String name;
	private ArrayList<Coinche.Card> hand = new ArrayList<>();
	private boolean belote;
	private boolean rebelote;

	public Player(ChannelHandlerContext chctx, String name) {
		this.chctx = chctx;
		this.name = name;
		this.belote = false;
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

	public void addToHand(Coinche.Card card) {
		this.hand.add(card);
	}

	public void removeFromHand(Coinche.Card card) {
		this.hand.remove(card);
	}

	public boolean isBelote() {
		return belote;
	}

	public void setBelote(boolean belote) {
		this.belote = belote;
	}

	public boolean isRebelote() {
		return rebelote;
	}

	public void setRebelote(boolean rebelote) {
		this.rebelote = rebelote;
	}
}
