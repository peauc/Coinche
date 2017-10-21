package eu.epitech.jcoinche.jcoincheserver.server;

import eu.epitech.jcoinche.protocol.Coinche;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BiddingPhaseManager {

	private int biddingPassTurn;
	private Map<Team, ArrayList<String>> toPrompt;
	private Team[] teams = new Team[2];
	private int end;
	private Coinche.Reply reply;

	public BiddingPhaseManager(Team team1, Team team2) {
		this.teams[0] = team1;
		this.teams[1] = team2;
		this.biddingPassTurn = 0;
		this.end = 0;
	}

	public int handleBiddingTurn(Coinche.Event message, Player player) {
		this.toPrompt = new HashMap<>();
		this.toPrompt.put(this.teams[0], new ArrayList<String>());
		this.toPrompt.put(this.teams[1], new ArrayList<String>());
		this.reply = null;

		switch (message.getType()) {
			case CONTRACT:
				this.handleBiddingContract(message, player);
				break;
			case PASS:
				this.handleBiddingPass(message, player);
				break;
			case COINCHE:
				this.handleBiddingCoinche(message, player);
				break;
			case SURCOINCHE:
				this.handleBiddingSurcoinche(message, player);
				break;
			default:
				this.reply = Coinche.Reply.newBuilder()
						.setNumber(401)
						.setMessage("You cannot perform this action while in the bidding phase.")
						.build();
		}
		return (end);
	}

	private void handleBiddingSurcoinche(Coinche.Event message, Player player) {
		Team ownTeam;
		Team oppositeTeam;

		ownTeam = (teams[0].isMember(player)) ? teams[0] : teams[1];
		oppositeTeam = (ownTeam == teams[0]) ? teams[1] : teams[0];
		if (!oppositeTeam.HasCoinched()) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(481)
					.setMessage("the opposite team didn't use COINCHE")
					.build();
		} else {
			ownTeam.setHasSurcoinched(true);
			this.toPrompt.get(ownTeam).add("Your team has surcoinched");
			this.toPrompt.get(oppositeTeam).add("The opposite team has surcoinched");
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(200)
					.setMessage("success")
					.build();
		}
	}

	private void handleBiddingCoinche(Coinche.Event message, Player player) {
		Team ownTeam;
		Team oppositeTeam;

		ownTeam = (teams[0].isMember(player)) ? teams[0] : teams[1];
		oppositeTeam = (ownTeam == teams[0]) ? teams[1] : teams[0];
		if (!oppositeTeam.getContractOptional().isPresent()) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(471)
					.setMessage("the opposite team has no valid contract for you to COINCHE")
					.build();
		} else {
			ownTeam.setHasCoinched(true);
			this.toPrompt.get(ownTeam).add("Your team has coinched");
			this.toPrompt.get(oppositeTeam).add("The opposite team has coinched, do you want to surcoinche ? (SURCOINCHE/PASS)");
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(200)
					.setMessage("success")
					.build();
		}
	}

	private void handleBiddingContract(Coinche.Event message, Player player) {
		Team ownTeam;
		Team oppositeTeam;

		ownTeam = (teams[0].isMember(player)) ? teams[0] : teams[1];
		oppositeTeam = (ownTeam == teams[0]) ? teams[1] : teams[0];
		if (oppositeTeam.HasCoinched()) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(471)
					.setMessage("the opposite team has used COINCHE, you can only answer by SURCOINCHE or PASS")
					.build();
		} else if (message.getContract().isInitialized() && message.getContract().getScore() < 80) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(472)
					.setMessage("invalid contract, score must be higher than 80")
					.build();
		} else if (message.getContract().isInitialized() && message.getContract().getScore() > 160) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(473)
					.setMessage("invalid contract, score must be lower than 160 or equal to 250 (capot)")
					.build();
		} else if (oppositeTeam.getContractOptional().isPresent() &&
				message.getContract().isInitialized() &&
				oppositeTeam.getContractOptional().get().getScore() + 10 > message.getContract().getScore()) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(474)
					.setMessage("invalid contract, score must be higher than the previous valid contract + 9 (" +
							oppositeTeam.getContractOptional().get().getScore() + ")")
					.build();
		} else {
			ownTeam.setContractOptional(Optional.of(message.getContract()));
			if (message.getContract().getScore() == 250) {
				this.toPrompt.get(ownTeam).add("Your team is now CAPOT");
				this.toPrompt.get(oppositeTeam).add("The opposite team is CAPOT, do you want to COINCHE ? (COINCHE/PASS)");
			} else {
				this.toPrompt.get(ownTeam).add("Your team has made a new contract. score: " + message.getContract().getScore() + ", type: " + message.getContract().getTypeValue());
				this.toPrompt.get(oppositeTeam).add("The opposite team has made a new contract. score: " + message.getContract().getScore() + ", type: " + message.getContract().getTypeValue());
			}
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(200)
					.setMessage("success")
					.build();
			this.biddingPassTurn = 0;
		}
	}

	private void handleBiddingPass(Coinche.Event message, Player player) {
		Team ownTeam;
		Team oppositeTeam;

		ownTeam = (teams[0].isMember(player)) ? teams[0] : teams[1];
		oppositeTeam = (ownTeam == teams[0]) ? teams[1] : teams[0];

		this.toPrompt.get(ownTeam).add("Your team has passed");
		this.toPrompt.get(oppositeTeam).add("The opposite team has passed");
		if (this.biddingPassTurn == 2) {
			this.toPrompt.get(ownTeam).add("End of the bidding phase");
			this.toPrompt.get(oppositeTeam).add("End of the bidding phase");
			this.end = 1;
		}
		this.reply = Coinche.Reply.newBuilder()
				.setNumber(200)
				.setMessage("success")
				.build();
	}

	public Map<Team, ArrayList<String>> getToPrompt() {
		return toPrompt;
	}

	public Coinche.Reply getReply() {
		return reply;
	}
}
