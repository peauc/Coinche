package eu.epitech.jcoinche.jcoincheserver.server;

import eu.epitech.jcoinche.protocol.Coinche;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Map;

import static eu.epitech.jcoinche.jcoincheserver.server.Game.GameState.BIDDING;

public class Game {

	public enum GameState {
		AWAITING_PLAYERS,
		BIDDING,
		GAME,
		SCORES;
	}

	private ArrayList<Player> players;
	private Team[] teams;
	private GameState state;
	private CardManager cm;
	private BiddingPhaseManager bm;
	private ArrayList<Trick> tricks;
	private int currentPlayerIndex;
	private int roundNb;

	public Game() {
		this.players = new ArrayList<>();
		this.teams = new Team[2];
		this.state = GameState.AWAITING_PLAYERS;
		this.cm = new CardManager();
		this.currentPlayerIndex = 0;
		this.tricks = new ArrayList<>();
		this.roundNb = 0;
	}

	public void start() {
		this.createTeams();
		this.bm = new BiddingPhaseManager(this.teams[0], this.teams[1]);
		this.state = BIDDING;
		this.run();
	}

	public void run() {
	}

	public void sendReply(Coinche.Reply reply)
	{
		this.players.get(this.currentPlayerIndex).sendMessage(Coinche.Message.newBuilder()
		.setType(Coinche.Message.Type.REPLY)
		.setReply(reply)
		.build());
	}

	public void sendGameInfos(Map<Team, ArrayList<String>> toPrompt)
	{
		for(Map.Entry<Team, ArrayList<String>> entry : toPrompt.entrySet()) {
			Team team = entry.getKey();
			Player[] players = team.getPlayers();
			ArrayList<String> infos = entry.getValue();
			for (int i = 0; i < players.length; i++) {
				players[i].sendMessage(Coinche.Message.newBuilder()
					.setType(Coinche.Message.Type.PROMPT)
					.setPrompt(Coinche.Prompt.newBuilder()
						.addAllToDisplay(infos)
						.build())
					.build());
			}
		}
	}

	private void createTeams() {
		this.teams[0] = new Team(this.players.get(0), this.players.get(2));
		this.teams[1] = new Team(this.players.get(1), this.players.get(3));
	}

	public void removePlayer(Player p) {
		players.remove(p);
	}

	public Boolean countainPlayer(ChannelHandlerContext ctx) {
		for (Player p : players) {
			if (p.getChctx() == ctx) {
				return (true);
			}
		}
		return (false);
	}

	public Boolean countainPlayer(Player pl) {
		for (Player p : players) {
			if (p == pl)
				return (true);
		}
		return (false);
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

	public void checkAnnounces() {
		int winner;
		if (this.teams[0].getAnnounces().size() == 0 || this.teams[1].getAnnounces().size() == 0)
			return;
		if (teams[0].getBestAnnounce().compareTo(teams[1].getBestAnnounce()) > 0) {
			teams[1].getAnnounces().clear();
			winner = 0;
		} else {
			teams[0].getAnnounces().clear();
			winner = 1;
		}
		this.tricks.get(this.tricks.size() - 1).addToPrompt(this.teams[winner], "Your team has the best announce !");
		this.tricks.get(this.tricks.size() - 1).addToPrompt(this.teams[(winner == 1) ? 0 : 1], "The opposite team has the best announce !");
		for (Announce announce : this.teams[winner].getAnnounces()) {
			this.tricks.get(this.tricks.size() - 1).addToPrompt(this.teams[winner],
					"Your team must realize a " + announce.getType().name() + " with a " +
							announce.getCardsToValidate().get(announce.getCardsToValidate().size() - 1).getCard().getValue().name() + " of " +
							announce.getCardsToValidate().get(announce.getCardsToValidate().size() - 1).getCard().getType().name()
			);
			this.tricks.get(this.tricks.size() - 1).addToPrompt(this.teams[winner],
					"The opposite team must realize a " + announce.getType().name() + " with a " +
							announce.getCardsToValidate().get(announce.getCardsToValidate().size() - 1).getCard().getValue().name() + " of " +
							announce.getCardsToValidate().get(announce.getCardsToValidate().size() - 1).getCard().getType().name()
			);
		}
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
