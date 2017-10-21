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
	private int totalScore;
	private int roundScore;

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

	public void setNbTricksRealised(int nbTricksRealised) {
		this.nbTricksRealised = nbTricksRealised;
	}

	public boolean isCapot() {
		return isCapot;
	}

	public void setCapot(boolean capot) {
		isCapot = capot;
	}

	public boolean HasCoinched() {
		return hasCoinched;
	}

	public void setHasCoinched(boolean hasCoinched) {
		this.hasCoinched = hasCoinched;
	}

	public boolean HasSurcoinched() {
		return hasSurcoinched;
	}

	public void setHasSurcoinched(boolean hasSurcoinched) {
		this.hasSurcoinched = hasSurcoinched;
	}

	public Optional<Coinche.Contract> getContractOptional() {
		return contractOptional;
	}

	public void setContractOptional(Optional<Coinche.Contract> contractOptional) {
		this.contractOptional = contractOptional;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public int getRoundScore() {
		return roundScore;
	}

	public void setRoundScore(int roundScore) {
		this.roundScore = roundScore;
	}
}
