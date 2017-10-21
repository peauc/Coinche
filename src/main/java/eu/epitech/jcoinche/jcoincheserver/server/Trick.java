package eu.epitech.jcoinche.jcoincheserver.server;

import eu.epitech.jcoinche.protocol.Coinche;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Trick {
	private Map<Player, Coinche.Card> cardsPlayed;
	private Player winner;
	private int scoreEarned;
	private Map<Team, ArrayList<String>> toPrompt;
	private Coinche.Reply reply;
	private Team[] teams = new Team[2];
	private int turn;
	private CardManager cm;

	public Trick(Team team1, Team team2, int turn, CardManager cm) {
		this.teams[0] = team1;
		this.teams[1] = team2;
		this.turn = turn;
		this.cm = cm;
	}

	public void handleTrickTurn(Player player, Coinche.Event message) {
		this.toPrompt = new HashMap<>();
		this.toPrompt.put(this.teams[0], new ArrayList<String>());
		this.toPrompt.put(this.teams[1], new ArrayList<String>());
		this.reply = null;

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
	}

	private void handleAnnounce(Coinche.Event message, Player player) {
		Team ownTeam;
		Team oppositeTeam;

		ownTeam = (teams[0].isMember(player)) ? teams[0] : teams[1];
		oppositeTeam = (ownTeam == teams[0]) ? teams[1] : teams[0];
		if (this.turn != 0) {
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
	}

	private void handlePlay(Coinche.Event message, Player player) {
		
	}

	public void addToPrompt(Team team, String message) {
		this.toPrompt.get(team).add(message);
	}

	public Map<Team, ArrayList<String>> getToPrompt() {
		return toPrompt;
	}
}
