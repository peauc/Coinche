package eu.epitech.jcoinche.jcoincheserver.server;

import eu.epitech.jcoinche.jcoincheclient.client.utils.MessageFactory;
import eu.epitech.jcoinche.protocol.Coinche;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static eu.epitech.jcoinche.jcoincheserver.server.Game.GameState.AWAITING_PLAYERS;
import static eu.epitech.jcoinche.jcoincheserver.server.Game.GameState.BIDDING;
import static eu.epitech.jcoinche.jcoincheserver.server.Game.GameState.GAME;

public class Game {

	public enum GameState {
		AWAITING_PLAYERS,
		BIDDING,
		GAME,
		SCORES;
	}

	private ArrayList<Player> players = new ArrayList<>();
	private Team[] teams;
	private GameState state;
	private CardManager cm;
	private BiddingPhaseManager bm;
	private ArrayList<Trick> tricks;
	private int currentPlayerIndex;
	private int roundNb;

	public Game() {
		resetGame();
	}

	public void start() {
		this.createTeams();
		this.bm = new BiddingPhaseManager(this.teams[0], this.teams[1]);
		this.cm.mix();
		this.cm.distributeToAll(players);
		this.state = BIDDING;
		this.promptToAll("Bidding phase start.");
		this.players.get(this.currentPlayerIndex).prompt("It's your turn to play:");
	}

	private void changeNameAndTellPlayers(Player player, Coinche.Event message) {
		Coinche.Message m;
		String	exName;

		if (Objects.equals(message.getArgument(0), "")) {
			m = MessageFactory.createMessageAndReply(400, "You thought that would work didn't you");
			player.getChctx().writeAndFlush(m);
			return ;
		}
		if (isNameTaken(message.getArgument(0))) {
			m = MessageFactory.createMessageAndReply(400, "Someone use that username already");
			player.getChctx().writeAndFlush(m);
			return;
		}
		if (player.getName() == "")
			exName = "Player" + players.indexOf(player);
		else
			exName = player.getName();
		promptToAllButPlayer(exName + " is now known as " + message.getArgument(0), player);
		player.setName(message.getArgument(0));
		System.out.println(player.getName());
		m = MessageFactory.createMessageAndReply(200, "You are now known as " + player.getName());
		player.getChctx().writeAndFlush(m);
	}

	private boolean isNameTaken(String argument) {
		for (Player p : players) {
			if (Objects.equals(p.getName(), argument))
				return (true);
		}
		return (false);
	}

	public void handlePlay(Coinche.Message message, Player player) {
		if (message.getType() == Coinche.Message.Type.EVENT && message.getEvent().getType() == Coinche.Event.Type.NAME) {
			changeNameAndTellPlayers(player, message.getEvent());
			if (this.checkIfGameCanStart()) {
				this.start();
			}
			return;
		}
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
					System.out.println("GAME HAS STARTED YOUHOU");
					this.bm.handleBiddingTurn(message.getEvent(), player);
					if (this.bm.getHasEnded()) {
						this.currentPlayerIndex = 0;
						this.state = GAME;
						this.promptToAll("Game phase start.");
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

	private Boolean checkIfGameCanStart() {
		if (players.size() == 4) {
			for (Player p : players) {
				if (Objects.equals(p.getName(), ""))
					return (false);
			}
			return (true);
		}
		return (false);
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
			player.sendHand();
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

	private void resetGame() {
		this.teams = new Team[2];
		this.state = GameState.AWAITING_PLAYERS;
		this.cm = new CardManager();
		this.currentPlayerIndex = 0;
		this.tricks = new ArrayList<>();
		this.roundNb = 0;
		for (Player p : players) {

			p.revoveHand();
		}
	}

	public void removePlayer(Player p) {
	    players.remove(p);
	    promptToAll("Player " + p.getName() + " left the game.\nGame is stopping\n");
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

	public void promptToAllButPlayer(String toPrompt, Player p) {
		Coinche.Message message;

		if (p.getName() == "")
			p.setName("Player" + this.players.indexOf(p));
		message = Coinche.Message.newBuilder().setType(Coinche.Message.Type.PROMPT).setPrompt(Coinche.Prompt.newBuilder().addToDisplay(toPrompt).build()).build();
		for (Player p2 : players) {
			if (p2 != p) {
				p2.getChctx().writeAndFlush(message);
			}
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
		int takersMult = 1;
		int passersMult = 1;
		int temp;

		bonus = this.teams[0].validateAnnounces(this.teams[1].validateAnnounces(bonus, 2, this.players), 1, this.players);
		this.teams[0].setRoundScore(this.teams[0].getRoundScore() + bonus);
		if (this.teams[0].getContractOptional().isPresent()) {
			takers = this.teams[0];
			passers = this.teams[1];
		} else {
			takers = this.teams[1];
			passers = this.teams[0];
		}
		if (passers.hasCoinched()) {
			takersMult = 2;
			passersMult = 0;
		} else if (takers.hasSurcoinched()) {
			takersMult = 4;
			passersMult = 0;
		}
		if (takers.hasValidatedContract()) {
			takers.validateContract();
			promptToAll("The contract has been fulfilled");
		} else {
			promptToAll("The contract has not been fulfilled.");
			temp = takersMult;
			takersMult = passersMult;
			passersMult = temp;
		}
		takers.setTotalScore(takers.getTotalScore() + takers.getRoundScore() * takersMult + takers.getBonusRoundScore());
		takers.setRoundScore(0);
		takers.setBonusRoundScore(0);
		passers.setTotalScore(passers.getTotalScore() + (passers.getRoundScore() + takers.getContractOptional().get().getScore() + ((takers.isCapot()) ? 250 : 0)) * passersMult + passers.getBonusRoundScore());
		passers.setRoundScore(0);
		passers.setBonusRoundScore(0);
	}
}
