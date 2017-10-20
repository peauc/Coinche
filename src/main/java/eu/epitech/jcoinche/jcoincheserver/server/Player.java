package eu.epitech.jcoinche.jcoincheserver.server;

import eu.epitech.jcoinche.protocol.Coinche;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;

public class Player {
	private ChannelHandlerContext chctx;
	private String name;
	private ArrayList<Coinche.Card> hand = new ArrayList<>();

	public Player(ChannelHandlerContext chctx, String name) {
		this.chctx = chctx;
		this.name = name;
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

	public void addToHand(Coinche.Card card) {
		this.hand.add(card);
	}

	public void removeFromHand(Coinche.Card card) {
		this.hand.remove(card);
	}
}
