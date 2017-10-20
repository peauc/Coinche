package eu.epitech.jcoinche.jcoincheserver.server;

import eu.epitech.jcoinche.protocol.Coinche;

import java.util.ArrayList;

public class Game {

	public enum GameState {
		AWAITING_PLAYERS,
		BIDDING,
		GAME,
		SCORES;
	}

	private ArrayList<Player> players = new ArrayList<>();
	private Team[] teams;
	private GameState state = GameState.AWAITING_PLAYERS;

	public void run() {
	}

	public boolean isReady() {
		if (players.size() != 4)
			return (false);
		for (Player player : players) {
			if (player.getName() == null || player.getName().length() == 0)
				return (false);
		}
		return (true);
	}

	public GameState getState() {
		return state;
	}

	public boolean isFull() {
		return players.size() == 4;
	}

	public int addPlayer(Player player) {
		if (!this.isFull()) {
			players.add(player);
			return (0);
		}
		return (-1);
	}
}
