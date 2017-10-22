package eu.epitech.jcoinche.jcoincheserver.server;

import eu.epitech.jcoinche.protocol.Coinche;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class Team {

	private Player[] players = new Player[2];
	private int nbTricksRealised;
	private boolean isCapot;
	private boolean hasCoinched;
	private boolean hasSurcoinched;
	private Optional<Coinche.Contract> contractOptional;
	private int totalScore;
	private int roundScore;
	private int bonusRoundScore;
	private int tricksWon;
	private ArrayList<Announce> announces;

	public Team(Player player1, Player player2) {
		this.players[0] = player1;
		this.players[1] = player2;
	}

	public void setupRound(boolean isCapot, boolean hasCoinched, boolean hasSurcoinched, Optional<Coinche.Contract> contractOptional) {
		this.nbTricksRealised = 0;
		this.isCapot = isCapot;
		this.hasCoinched = hasCoinched;
		this.hasSurcoinched = hasSurcoinched;
		this.announces = new ArrayList<>();
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

	public Announce getBestAnnounce() {
		Announce koth;

		if (this.announces.size() == 0)
			return null;
		koth = this.announces.get(0);
		for (int i = 1; i < this.announces.size(); i++) {
			if (koth.compareTo(this.announces.get(i)) < 0) {
				koth = this.announces.get(i);
			}
		}
		return (koth);
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

	public boolean hasCoinched() {
		return hasCoinched;
	}

	public void setHasCoinched(boolean hasCoinched) {
		this.hasCoinched = hasCoinched;
	}

	public boolean hasSurcoinched() {
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

	public ArrayList<Announce> getAnnounces() {
		return announces;
	}

	public void setAnnounces(ArrayList<Announce> announces) {
		this.announces = announces;
	}

	public void addAnnounce(Announce announce) {
		this.announces.add(announce);
	}

	public int getTricksWon() {
		return tricksWon;
	}

	public void setTricksWon(int tricksWon) {
		this.tricksWon = tricksWon;
	}

	public int getBonusRoundScore() {
		return bonusRoundScore;
	}

	public void setBonusRoundScore(int bonusRoundScore) {
		this.bonusRoundScore = bonusRoundScore;
	}

	public int validateAnnounces(int bonus, int teamNb, ArrayList<Player> players) {
		int ownScore = 0;
		int otherScore = 0;
		for (Announce announce : this.announces) {
			if (announce.isComplete()) {
				for (Player player : players) {
					player.sendMessage(Coinche.Message.newBuilder()
							.setType(Coinche.Message.Type.PROMPT)
							.setPrompt(Coinche.Prompt.newBuilder()
								.addToDisplay("Team " + String.valueOf(teamNb) + "has completed an announce (type:" +announce.getType().name() + ", reward: " + announce.getReward() + ")")
								.build())
							.build());
				}
				ownScore += announce.getReward();
			} else {
				for (Player player : players) {
					player.sendMessage(Coinche.Message.newBuilder()
							.setType(Coinche.Message.Type.PROMPT)
							.setPrompt(Coinche.Prompt.newBuilder()
									.addToDisplay("Team " + String.valueOf(teamNb) + "has NOT completed an announce (type:" +announce.getType().name() + ", reward: " + announce.getReward() + "). Reward will be given to the other team.")
									.build())
							.build());
				}
				otherScore += announce.getReward();
			}
		}
		this.roundScore += ownScore + bonus;
		return (otherScore);
	}

	public boolean hasValidatedContract() {
		if (this.contractOptional.isPresent()) {
			if (this.isCapot) {
				return this.nbTricksRealised == 8;
			} else {
				return this.roundScore >= this.contractOptional.get().getScore();
			}
		} else {
			return (false);
		}
	}

	public void validateContract() {
		int score = 0;

		if (this.isCapot) {
			score = this.roundScore + 500;
		} else {
			score += this.roundScore + this.contractOptional.get().getScore();
		}
		this.totalScore += score;
	}
}