package eu.epitech.jcoinche.jcoincheserver.server;

import eu.epitech.jcoinche.protocol.Coinche;

import java.util.Optional;

public class BiddingPhaseManager {

	private int biddingPassTurn;
	private Optional<String> toDisplayOptional;
	private Team[] teams = new Team[2];

	public BiddingPhaseManager(Team team1, Team team2) {
		this.teams[0] = team1;
		this.teams[1] = team2;
		this.biddingPassTurn = 0;
	}

	public int handleBiddingTurn(Coinche.Event message, Player player) {
		this.toDisplayOptional = Optional.empty();

		switch (message.getType()) {
			case CONTRACT:
				this.handleBiddingContract(message, player);
				break;
			case PASS:
				this.handleBiddingPass(message, player);
				break;
			case COINCHE:
				this.handleBiddingCoinche(message, player);
				break;
			case SURCOINCHE:
				this.handleBiddingSurcoinche(message, player);
				break;
		}
		return (0);
	}

	private void handleBiddingSurcoinche(Coinche.Event message, Player player) {
	}

	private void handleBiddingCoinche(Coinche.Event message, Player player) {
	}

	private void handleBiddingContract(Coinche.Event message, Player player) {

	}

	private void handleBiddingPass(Coinche.Event message, Player player) {
		this.toDisplayOptional = Optional.of(player.getName() + " has passed.");
	}
}
