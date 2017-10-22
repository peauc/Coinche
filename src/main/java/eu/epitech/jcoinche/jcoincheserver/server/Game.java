package eu.epitech.jcoinche.jcoincheserver.server;

import eu.epitech.jcoinche.protocol.Coinche;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Map;

import static eu.epitech.jcoinche.jcoincheserver.server.Game.GameState.AWAITING_PLAYERS;
import static eu.epitech.jcoinche.jcoincheserver.server.Game.GameState.BIDDING;
import static eu.epitech.jcoinche.jcoincheserver.server.Game.GameState.GAME;

import java.util.Optional;

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
		this.cm.mix();
		this.cm.distributeToAll(players);
		this.state = BIDDING;
	}

	public void handlePlay(Coinche.Message message, Player player) {
		if (this.state == AWAITING_PLAYERS) {
			Coinche.Reply reply = Coinche.Reply.newBuilder()
					.setNumber(404)
					.setMessage("The game is not ready.")
					.build();
			this.sendReply(reply, player);
			return;
		} else if (this.handleCommonCommands(message, player)) {
			return;
		} else {
			if (!player.equals(this.players.get(this.currentPlayerIndex))) {
				Coinche.Reply reply = Coinche.Reply.newBuilder()
						.setNumber(403)
						.setMessage("It's not your turn to play.")
						.build();
				this.sendReply(reply, player);
				return;
			} else {
				if (state == BIDDING) {
					this.bm.handleBiddingTurn(message.getEvent(), player);
					if (this.bm.getHasEnded()) {
						this.currentPlayerIndex = 0;
						this.state = GAME;
					} else if (this.bm.isOperationSuccess()) {
						this.currentPlayerIndex++;
						if (this.currentPlayerIndex >= this.players.size())
							this.currentPlayerIndex = 0;
					}
				} else if (state == GAME) {
					if (this.tricks.size() == 0)
						this.promptToAll("Round " + this.roundNb + " starting !");

				}
			}
		}
	}

	public boolean handleCommonCommands(Coinche.Message message, Player player) {
		if (message.getType() != Coinche.Message.Type.EVENT || !message.hasEvent()) {
			Coinche.Reply reply = Coinche.Reply.newBuilder()
					.setNumber(402)
					.setMessage("Invalid message.")
					.build();
			this.sendReply(reply, player);
			return (true);
		} else if (message.getEvent().getType() == Coinche.Event.Type.HAND) {
			Coinche.Reply reply = Coinche.Reply.newBuilder()
					.setNumber(200)
					.setMessage("SUCCESS")
					.build();
			this.sendReply(reply, player);
			this.players.get(this.currentPlayerIndex).sendHand();
			return (true);
		}
		return (false);
	}

	public void sendReply(Coinche.Reply reply, Player player)
	{
		player.sendMessage(Coinche.Message.newBuilder()
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

	//TODO
	private void resetGame() {

	}

	public void removePlayer(Player p) {
	    players.remove(p);
	    resetGame();
	}

	public Player countainPlayer(ChannelHandlerContext ctx) {
		for (Player p : players) {
			if (p.getChctx() == ctx) {
				return (p);
			}
		}
		return (null);
	}

	public Player countainPlayer(Player pl) {
		for (Player p : players) {
			if (p == pl)
				return (p);
		}
		return (null);
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

	public void promptToAll(String toPrompt) {
		Coinche.Message message = Coinche.Message.newBuilder()
				.setType(Coinche.Message.Type.PROMPT)
				.setPrompt(Coinche.Prompt.newBuilder()
					.addToDisplay(toPrompt)
					.build())
				.build();
		for (Player player : this.players) {
			player.sendMessage(message);
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

	public void countScores() {
		Team takers;
		Team passers;
		int bonus = 0;

		bonus = this.teams[0].validateAnnounces(this.teams[1].validateAnnounces(bonus));
		this.teams[0].setRoundScore(this.teams[0].getRoundScore() + bonus);
		if (this.teams[0].getContractOptional().isPresent()) {
			takers = this.teams[0];
			passers = this.teams[1];
		} else {
			takers = this.teams[1];
			passers = this.teams[0];
		}
		if (takers.hasValidatedContract()) {
			takers.validateContract(passers.hasCoinched(), passers.hasSurcoinched());
			takers.setTotalScore(takers.getTotalScore() + takers.getRoundScore() * 2 + takers.getBonusRoundScore());
			takers.setRoundScore(0);
		}
	}
}
