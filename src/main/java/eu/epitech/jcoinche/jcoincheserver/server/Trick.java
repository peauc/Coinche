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
	private boolean isFirstTrick;
	private CardManager cm;

	public Trick(Team team1, Team team2, boolean isFirstTrick, CardManager cm) {
		this.teams[0] = team1;
		this.teams[1] = team2;
		this.isFirstTrick = isFirstTrick;
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

	}

	private void handlePlay(Coinche.Event message, Player player) {

	}
}
