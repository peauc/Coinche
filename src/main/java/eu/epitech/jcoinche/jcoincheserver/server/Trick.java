package eu.epitech.jcoinche.jcoincheserver.server;

import eu.epitech.jcoinche.protocol.Coinche;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Trick {
	private Map<Player, Coinche.Card> cardsPlayed;
	private Player winner;
	private int scoreEarned;
	private Map<Team, ArrayList<String>> toPrompt;
	private Coinche.Reply reply;
	private Team[] teams = new Team[2];
	private int trickNb;
	private CardManager cm;
	private Coinche.Card.Type chosenColor;
	private int turn;
	private boolean hasEnded;
	private boolean operationSuccess;

	public Trick(Team team1, Team team2, int trickNb, CardManager cm) {
		this.cardsPlayed = new HashMap<>();
		this.teams[0] = team1;
		this.teams[1] = team2;
		this.trickNb = trickNb;
		this.cm = cm;
		this.turn = 0;
		this.hasEnded = false;
	}

	public void handleTrickTurn(Player player, Coinche.Event message) {
		this.toPrompt = new HashMap<>();
		this.toPrompt.put(this.teams[0], new ArrayList<String>());
		this.toPrompt.put(this.teams[1], new ArrayList<String>());
		this.reply = null;
		this.operationSuccess = false;

		switch (message.getType()) {
			case PLAY:
				handlePlay(message, player);
				break;
			case ANNOUNCE:
				handleAnnounce(message, player);
				break;
			case BELOTE:
				handleBelote(message, player);
				break;
			case REBELOTE:
				handleRebelote(message, player);
				break;
			default:
				this.reply = Coinche.Reply.newBuilder()
						.setNumber(401)
						.setMessage("You cannot perform this action while in the game phase.")
						.build();
				return;
		}
		if (turn == 4)
			handleTrickEnd();
	}

	public void handleTrickEnd() {
		Player winner = this.cm.getHighestCard(this.cardsPlayed);
		Coinche.Card highestCard = this.cardsPlayed.get(winner);
		Team ownTeam;
		Team oppositeTeam;

		this.hasEnded = true;
		ownTeam = (teams[0].isMember(winner)) ? teams[0] : teams[1];
		oppositeTeam = (ownTeam == teams[0]) ? teams[1] : teams[0];
		ownTeam.setTricksWon(ownTeam.getTricksWon() + 1);
		ownTeam.setRoundScore(ownTeam.getRoundScore() + this.cm.getCardScore(highestCard) + ((this.trickNb == 7) ? 10 : 0));
		this.toPrompt.get(ownTeam).add(winner.getName() + " has won the trick and earned " + this.cm.getCardScore(highestCard) + ((this.trickNb == 7) ? 10 : 0));
		this.toPrompt.get(oppositeTeam).add(winner.getName() + " has won the trick and earned " + this.cm.getCardScore(highestCard) + ((this.trickNb == 7) ? 10 : 0));
	}

	private void handleRebelote(Coinche.Event message, Player player) {
		Team ownTeam;
		Team oppositeTeam;

		ownTeam = (teams[0].isMember(player)) ? teams[0] : teams[1];
		oppositeTeam = (ownTeam == teams[0]) ? teams[1] : teams[0];
		if (player.isBelote() != Player.beloteState.DONE) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(451)
					.setMessage("To announce a REBELOTE you must first complete a BELOTE.")
					.build();
			return;
		}
		this.toPrompt.get(ownTeam).add(player.getName() + " has announced a REBELOTE");
		this.toPrompt.get(oppositeTeam).add(player.getName() + " has announced a REBELOTE");
		player.setRebelote(Player.beloteState.DECLARED);
		this.reply = Coinche.Reply.newBuilder()
				.setNumber(200)
				.setMessage("SUCCESS")
				.build();
	}

	private void handleBelote(Coinche.Event message, Player player) {
		Team ownTeam;
		Team oppositeTeam;

		ownTeam = (teams[0].isMember(player)) ? teams[0] : teams[1];
		oppositeTeam = (ownTeam == teams[0]) ? teams[1] : teams[0];
		if (player.isBelote() == Player.beloteState.DECLARED) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(452)
					.setMessage("You already announced a BELOTE")
					.build();
			return;
		}
		this.toPrompt.get(ownTeam).add(player.getName() + " has announced a BELOTE");
		this.toPrompt.get(oppositeTeam).add(player.getName() + " has announced a BELOTE");
		player.setBelote(Player.beloteState.DECLARED);
		this.reply = Coinche.Reply.newBuilder()
				.setNumber(200)
				.setMessage("SUCCESS")
				.build();
	}

	private void handleAnnounce(Coinche.Event message, Player player) {
		Team ownTeam;
		Team oppositeTeam;

		ownTeam = (teams[0].isMember(player)) ? teams[0] : teams[1];
		oppositeTeam = (ownTeam == teams[0]) ? teams[1] : teams[0];
		if (this.trickNb != 0) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(461)
					.setMessage("You can only ANNOUNCE at trick 1.")
					.build();
			return;
		} else if (message.getArgumentCount() != 1 || !message.hasCard()) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(462)
					.setMessage("Invalid ANNOUNCE.")
					.build();
			return;
		} else if (!player.hasInHand(message.getCard())) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(463)
					.setMessage("You don't have this card in your hand.")
					.build();
			return;
		}
		Announce announce;
		switch (message.getArgument(0)) {
			case "CARRE":
				announce = new Announce(Announce.Type.CARRE, message.getCard(), player);
				break;
			case "CENT":
				announce = new Announce(Announce.Type.CENT, message.getCard(), player);
				break;
			case "CINQUANTE":
				announce = new Announce(Announce.Type.CINQUANTE, message.getCard(), player);
				break;
			case "TIERCE":
				announce = new Announce(Announce.Type.TIERCE, message.getCard(), player);
				break;
			default:
				this.reply = Coinche.Reply.newBuilder()
						.setNumber(462)
						.setMessage("Invalid ANNOUNCE.")
						.build();
				return;
		}
		ownTeam.addAnnounce(announce);
		this.toPrompt.get(ownTeam).add(player.getName() + " has just made an ANNOUNCE of strength " + announce.getReward());
		this.toPrompt.get(oppositeTeam).add(player.getName() + " has just made an ANNOUNCE of strength " + announce.getReward());
		this.reply = Coinche.Reply.newBuilder()
				.setNumber(200)
				.setMessage("SUCCESS")
				.build();
	}

	private void handlePlay(Coinche.Event message, Player player) {
		Team ownTeam;
		Team oppositeTeam;
		Player ally;
		boolean hasEntameInHand = false;
		boolean trumpHasBeenPlayed = false;
		boolean hasTrumpInHand = false;
		boolean hasHigherTrumpInHand = false;

		ownTeam = (teams[0].isMember(player)) ? teams[0] : teams[1];
		oppositeTeam = (ownTeam == teams[0]) ? teams[1] : teams[0];
		if (ownTeam.getPlayers()[0] == player)
			ally = ownTeam.getPlayers()[1];
		else
			ally = ownTeam.getPlayers()[0];
		if (!player.hasInHand(message.getCard())) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(441)
					.setMessage("You don't have this card in your hand.")
					.build();
			return;
		}
		if (turn != 0) {
			hasEntameInHand = player.hasEntameInHand(this.chosenColor);
			trumpHasBeenPlayed = trumpHasBeenPlayed();
			hasTrumpInHand = player.hasTrumpInHand(this.cm.getCurrentTrump());
			Player currentWinner = this.cm.getHighestCard(this.cardsPlayed);
			if (hasTrumpInHand && currentWinner != null)
				hasHigherTrumpInHand = player.hasHigherTrumpInHand(this.cm, this.cardsPlayed.get(currentWinner));
			if (message.getCard().getType() == this.chosenColor) {
				cardIsValid(ownTeam, oppositeTeam, message, player);
			} else {
				if (hasEntameInHand) {
					this.reply = Coinche.Reply.newBuilder()
							.setNumber(442)
							.setMessage("You must always play a card that corresponds to the entame if you have one.")
							.build();
				} else {
					if (trumpHasBeenPlayed && hasHigherTrumpInHand && this.cm.compareCards(message.getCard(), this.cardsPlayed.get(currentWinner)) < 0) {
						this.reply = Coinche.Reply.newBuilder()
								.setNumber(442)
								.setMessage("You have to play a trump higher than the one on the (virtual) table.")
								.build();
					} else if (trumpHasBeenPlayed) {
						this.cardIsValid(ownTeam, oppositeTeam, message, player);
					} else {
						if (ownTeam.isMember(currentWinner)) {
							this.cardIsValid(ownTeam, oppositeTeam, message, player);
						} else if (hasTrumpInHand && this.cm.compareCards(message.getCard(), this.cardsPlayed.get(currentWinner)) < 0) {
							this.reply = Coinche.Reply.newBuilder()
									.setNumber(443)
									.setMessage("You have to play a trump card.")
									.build();
						} else {
							this.cardIsValid(ownTeam, oppositeTeam, message, player);
						}
					}
				}
			}
		} else {
			this.chosenColor = message.getCard().getType();
			this.cardIsValid(ownTeam, oppositeTeam, message, player);
		}
	}

	public void cardIsValid(Team ownTeam, Team oppositeTeam, Coinche.Event message, Player player) {
		if (player.isBelote() == Player.beloteState.DECLARED) {
			if (this.cm.isTrumpQueen(message.getCard())) {
				player.setBelote(Player.beloteState.DONE);
			} else {
				player.setBelote(Player.beloteState.UNDECLARED);
				this.toPrompt.get(ownTeam).add(player.getName() + " has not completed his BELOTE, the opposite team will get a 20 points bonus");
				this.toPrompt.get(ownTeam).add(player.getName() + " has not completed his BELOTE, your team will get a 20 points bonus");
			}
		}
		if (player.isRebelote() == Player.beloteState.DECLARED) {
			if (this.cm.isTrumpQueen(message.getCard())) {
				player.setRebelote(Player.beloteState.DONE);
			} else {
				player.setRebelote(Player.beloteState.UNDECLARED);
				oppositeTeam.setBonusRoundScore(oppositeTeam.getBonusRoundScore() + 20);
				this.toPrompt.get(ownTeam).add(player.getName() + " has not completed his REBELOTE, the opposite team will get a 20 points bonus");
				this.toPrompt.get(ownTeam).add(player.getName() + " has not completed his REBELOTE, your team will get a 20 points bonus");
			}
		}

		this.turn++;
		this.chosenColor = message.getCard().getType();
		this.cardsPlayed.put(player, message.getCard());
		this.toPrompt.get(ownTeam).add(player.getName() + " has just played a " + message.getCard().getValue().name() + " of " + message.getCard().getType().name());
		this.toPrompt.get(oppositeTeam).add(player.getName() + " has just played a " + message.getCard().getValue().name() + " of " + message.getCard().getType().name());
		this.reply = Coinche.Reply.newBuilder()
				.setNumber(200)
				.setMessage("SUCCESS")
				.build();
		this.operationSuccess = true;
		for (Announce announce : ownTeam.getAnnounces()) {
			if (announce.validate(message.getCard()))
				return;
		}
	}

	public boolean trumpHasBeenPlayed() {
		for (Coinche.Card card : cardsPlayed.values()) {
			if (Objects.equals(this.cm.getCurrentTrump().name(), card.getType().name()))
				return (true);
		}
		return (false);
	}

	public void addToPrompt(Team team, String message) {
		this.toPrompt.get(team).add(message);
	}

	public Map<Team, ArrayList<String>> getToPrompt() {
		return toPrompt;
	}

	public boolean isHasEnded() {
		return hasEnded;
	}

	public boolean isOperationSuccess() {
		return operationSuccess;
	}
}
