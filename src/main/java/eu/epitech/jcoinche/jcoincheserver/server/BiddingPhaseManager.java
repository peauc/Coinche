package eu.epitech.jcoinche.jcoincheserver.server;

import eu.epitech.jcoinche.protocol.Coinche;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BiddingPhaseManager {

	private int biddingPassTurn;
	private Map<Team, ArrayList<String>> toPrompt;
	private Team currentWinner;
	private Team[] teams = new Team[2];
	private boolean hasEnded;
	private Coinche.Reply reply;
	private boolean operationSuccess;

	public BiddingPhaseManager(Team team1, Team team2) {
		this.teams[0] = team1;
		this.teams[1] = team2;
		this.biddingPassTurn = 0;
		this.hasEnded = false;
	}

	public int handleBiddingTurn(Coinche.Event message, Player player) {
		this.toPrompt = new HashMap<>();
		this.toPrompt.put(this.teams[0], new ArrayList<String>());
		this.toPrompt.put(this.teams[1], new ArrayList<String>());
		this.reply = null;
		this.operationSuccess = false;

		Team ownTeam;
		Team oppositeTeam;

		ownTeam = (teams[0].isMember(player)) ? teams[0] : teams[1];
		oppositeTeam = (ownTeam == teams[0]) ? teams[1] : teams[0];
		switch (message.getType()) {
			case CONTRACT:
				this.handleBiddingContract(message, player, ownTeam, oppositeTeam);
				break;
			case PASS:
				this.handleBiddingPass(message, player, ownTeam, oppositeTeam);
				break;
			case COINCHE:
				this.handleBiddingCoinche(message, player, ownTeam, oppositeTeam);
				break;
			case SURCOINCHE:
				this.handleBiddingSurcoinche(message, player, ownTeam, oppositeTeam);
				break;
			default:
				this.reply = Coinche.Reply.newBuilder()
						.setNumber(401)
						.setMessage("You cannot perform this action while in the bidding phase.")
						.build();
				return (0);
		}
		this.handleBiddingEnd(ownTeam, oppositeTeam);
		return (0);
	}

	private void handleBiddingEnd(Team ownTeam, Team oppositeTeam) {
		if (this.biddingPassTurn >= 3) {
			this.toPrompt.get(ownTeam).add("End of the bidding phase");
			this.toPrompt.get(oppositeTeam).add("End of the bidding phase");

			String str;
			if (this.currentWinner != null && this.currentWinner.getContractOptional().isPresent()) {
				if (ownTeam == this.currentWinner) {
					str = "must fulfill this contract. (score: " + ownTeam.getContractOptional().get().getScore() +
							", trump: " + ownTeam.getContractOptional().get().getType().name() +
							", options:" + ((ownTeam.isCapot()) ? " CAPOT" : "") + ((oppositeTeam.hasCoinched()) ? " COINCHE" : "") + ((ownTeam.hasSurcoinched()) ? " SURCOINCHE" : "");
					this.toPrompt.get(ownTeam).add("Your team " + str);
					this.toPrompt.get(oppositeTeam).add("The opposite " + str);
				} else {
					str = "must fulfill this contract. (score: " + oppositeTeam.getContractOptional().get().getScore() +
							", trump: " + oppositeTeam.getContractOptional().get().getType().name() +
							", options:" + ((oppositeTeam.isCapot()) ? " CAPOT" : "") + ((ownTeam.hasCoinched()) ? " COINCHE" : "") + ((oppositeTeam.hasSurcoinched()) ? " SURCOINCHE" : "");
					this.toPrompt.get(ownTeam).add("The opposite " + str);
					this.toPrompt.get(oppositeTeam).add("Your team " + str);
				}
			}
			this.toPrompt.get(oppositeTeam).add("End of the bidding phase");
			this.hasEnded = true;
		}
	}

	private void handleBiddingSurcoinche(Coinche.Event message, Player player, Team ownTeam, Team oppositeTeam) {
		if (!oppositeTeam.hasCoinched()) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(481)
					.setMessage("The opposite team didn't use COINCHE")
					.build();
		} else {
			ownTeam.setHasSurcoinched(true);
			oppositeTeam.setHasCoinched(false);
			this.toPrompt.get(ownTeam).add("Your team has surcoinched");
			this.toPrompt.get(oppositeTeam).add("The opposite team has surcoinched");
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(200)
					.setMessage("SUCCESS")
					.build();
			this.biddingPassTurn = 3;
			this.operationSuccess = true;
		}
	}

	private void handleBiddingCoinche(Coinche.Event message, Player player, Team ownTeam, Team oppositeTeam) {
		if (!oppositeTeam.getContractOptional().isPresent()) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(471)
					.setMessage("The opposite team has no valid contract for you to COINCHE")
					.build();
		} else if (ownTeam.hasCoinched() || ownTeam.hasSurcoinched() || oppositeTeam.hasCoinched() || oppositeTeam.hasSurcoinched()) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(472)
					.setMessage("You cannot coinche at the moment.")
					.build();
		} else {
			ownTeam.setHasCoinched(true);
			this.biddingPassTurn = 2;
			this.toPrompt.get(ownTeam).add("Your team has coinched");
			this.toPrompt.get(oppositeTeam).add("The opposite team has coinched, do you want to surcoinche ? (SURCOINCHE/PASS)");
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(200)
					.setMessage("SUCCESS")
					.build();
			this.operationSuccess = true;
		}
	}

	private void handleBiddingContract(Coinche.Event message, Player player, Team ownTeam, Team oppositeTeam) {

		if (!message.getContract().isInitialized()) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(479)
					.setMessage("Invalid contract.")
					.build();
		}
		else if (oppositeTeam.hasCoinched()) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(471)
					.setMessage("The opposite team has used COINCHE, you can only answer by SURCOINCHE or PASS")
					.build();
		} else if (ownTeam.hasCoinched()) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(471)
					.setMessage("Your team has used COINCHE, you cannot submit a contract anymore")
					.build();
		} else if (message.getContract().isInitialized() && message.getContract().getScore() < 80) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(472)
					.setMessage("Invalid contract, score must be higher than 80")
					.build();
		} else if (message.getContract().isInitialized() && message.getContract().getScore() > 160) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(473)
					.setMessage("Invalid contract, score must be lower than 160 or equal to 250 (capot)")
					.build();
		} else if (oppositeTeam.isCapot()) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(474)
					.setMessage("The opposite team is capot, you must answer with COINCHE or PASS")
					.build();
		} else if (currentWinner != null && currentWinner.getContractOptional().isPresent() &&
				currentWinner.getContractOptional().get().getScore() + 10 > message.getContract().getScore()) {
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(474)
					.setMessage("Invalid contract, score must be higher than the previous valid contract + 9 (" +
							oppositeTeam.getContractOptional().get().getScore() + ")")
					.build();
		} else {
			ownTeam.setContractOptional(Optional.of(message.getContract()));
			oppositeTeam.setContractOptional(Optional.empty());
			this.currentWinner = ownTeam;
			if (message.getContract().getScore() == 250) {
				this.toPrompt.get(ownTeam).add("Your team is now CAPOT");
				this.toPrompt.get(oppositeTeam).add("The opposite team is CAPOT, do you want to COINCHE ? (COINCHE/PASS)");
			} else {
				this.toPrompt.get(ownTeam).add("Your team has made a new contract. score: " + message.getContract().getScore() + ", type: " + message.getContract().getTypeValue());
				this.toPrompt.get(oppositeTeam).add("The opposite team has made a new contract. score: " + message.getContract().getScore() + ", type: " + message.getContract().getTypeValue());
			}
			this.reply = Coinche.Reply.newBuilder()
					.setNumber(200)
					.setMessage("SUCCESS")
					.build();
			this.biddingPassTurn = 0;
			this.operationSuccess = true;
		}
	}

	private void handleBiddingPass(Coinche.Event message, Player player, Team ownTeam, Team oppositeTeam) {
		this.toPrompt.get(ownTeam).add("Your team has passed");
		this.toPrompt.get(oppositeTeam).add("The opposite team has passed");
		this.biddingPassTurn++;
		this.reply = Coinche.Reply.newBuilder()
				.setNumber(200)
				.setMessage("SUCCESS")
				.build();
		this.operationSuccess = true;
	}

	public Map<Team, ArrayList<String>> getToPrompt() {
		return toPrompt;
	}

	public Coinche.Reply getReply() {
		return reply;
	}

	public boolean isOperationSuccess() {
		return operationSuccess;
	}

	public boolean getHasEnded() {
		return hasEnded;
	}
}
