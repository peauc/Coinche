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

	public Trick(Team team1, Team team2, int trickNb, CardManager cm) {
		this.teams[0] = team1;
		this.teams[1] = team2;
		this.trickNb = trickNb;
		this.cm = cm;
		this.turn = -1;
	}

	public void handleTrickTurn(Player player, Coinche.Event message) {
		this.toPrompt = new HashMap<>();
		this.toPrompt.put(this.teams[0], new ArrayList<String>());
		this.toPrompt.put(this.teams[1], new ArrayList<String>());
		this.reply = null;
		this.turn++;

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
		}
	}

	public void handleTrickEnd() {
		Player winner = this.cm.getHighestCard(this.cardsPlayed);
		Coinche.Card highestCard = this.cardsPlayed.get(winner);
		Team ownTeam;
		Team oppositeTeam;

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
		if (!player.isBelote()) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(451)
					.setMessage("To announce a REBELOTE you must first complete a BELOTE.")
					.build();
			return;
		}
		this.toPrompt.get(ownTeam).add(player.getName() + " has announced a REBELOTE");
		this.toPrompt.get(oppositeTeam).add(player.getName() + " has announced a REBELOTE");
		player.setBelote(false);
		player.setRebelote(true);
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
		if (player.isBelote()) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(452)
					.setMessage("You already announced a BELOTE")
					.build();
			return;
		}
		this.toPrompt.get(ownTeam).add(player.getName() + " has announced a BELOTE");
		this.toPrompt.get(oppositeTeam).add(player.getName() + " has announced a BELOTE");
		player.setBelote(true);
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
			if (message.getCard().getType() != this.chosenColor && hasEntameInHand) {
				this.reply = Coinche.Reply.newBuilder()
						.setNumber(441)
						.setMessage("You always have to play a card that corresponds to the entame if you can.")
						.build();
				return;
			}
		} else {
			this.chosenColor = message.getCard().getType();
		}
		this.cardsPlayed.put(player, message.getCard());
		this.toPrompt.get(ownTeam).add(player.getName() + " has just played a " + message.getCard().getValue().name() + " of " + message.getCard().getType().name());
		this.toPrompt.get(oppositeTeam).add(player.getName() + " has just played a " + message.getCard().getValue().name() + " of " + message.getCard().getType().name());
		this.reply = Coinche.Reply.newBuilder()
				.setNumber(200)
				.setMessage("SUCCESS")
				.build();
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
}
