package eu.epitech.jcoinche.jcoincheserver.server;

import eu.epitech.jcoinche.protocol.Coinche;

import java.util.Objects;
import java.util.Optional;

public class Team {

	private Player[] players = new Player[2];;
	private int nbTricksRealised;
	private boolean isCapot;
	private boolean hasCoinched;
	private boolean hasSurcoinched;
	private Optional<Coinche.Contract> contractOptional;

	public Team(Player player1, Player player2) {
		this.players[0] = player1;
		this.players[1] = player2;
	}

	public void setupRound(boolean isCapot, boolean hasCoinched, boolean hasSurcoinched, Optional<Coinche.Contract> contractOptional) {
		this.nbTricksRealised = 0;
		this.isCapot = isCapot;
		this.hasCoinched = hasCoinched;
		this.hasSurcoinched = hasSurcoinched;
		if (this.contractOptional.isPresent()) {
			this.contractOptional = contractOptional;
		} else {
			this.contractOptional = Optional.empty();
		}
	}

	public boolean isMember(Player player) {
		for (int i = 0; i < 2; i++) {
			if (Objects.equals(player.getName(), this.players[i].getName())) {
				return (true);
			}
		}
		return (false);
	}

	public Player[] getPlayers() {
		return players;
	}

	public int getNbTricksRealised() {
		return nbTricksRealised;
	}

	public boolean isCapot() {
		return isCapot;
	}

	public boolean HasCoinched() {
		return hasCoinched;
	}

	public boolean HasSurcoinched() {
		return hasSurcoinched;
	}
}
