package org.gaas.kuhhandel.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.gaas.kuhhandel.eum.AnimalCardEnum;
import org.gaas.kuhhandel.eum.ControllerTypeEnum;
import org.gaas.kuhhandel.eum.GameStatusEnum;
import org.gaas.kuhhandel.interfaces.Auctioneer;
import org.gaas.kuhhandel.interfaces.Bidder;
import org.gaas.kuhhandel.interfaces.InitiateTrader;
import org.gaas.kuhhandel.interfaces.Respondent;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
//import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayUser implements Auctioneer, Bidder, InitiateTrader, Respondent {
	@JsonIgnore
	private Room room;
	@JsonIgnore
	private Game game;
	private String id;
	private int status; // 0: Not prepared, 1: Ready to go, 2: In the game
	private Integer weight;
	private Integer score;
	private HandCard handCard;
	
	@Override
	public void acceptBid(Bid bid) {
		if(!validBid(bid))	return;
		
		bid.setResultStatus(GameStatusEnum.TRADING_PLAYER_ACCEPT_BID);
		game.setBid(bid);
		game.setGameStatus(GameStatusEnum.TRADING_JUDGMENT);
		game.setController(ControllerTypeEnum.SYSTEM);
	}
	@Override
	public Bid bid(Bid bid) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Card selectCard(AnimalCardEnum donkey, int quantity) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public HandCard changeHandCard(HandCard handCard) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<MoneyCard> bid(Integer money, int number) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Card drawCard() {
		// TODO Auto-generated method stub
		return null;
	}
	public Boolean validBid(Bid bid) {
		if(!GameStatusEnum.TRADING.equals(game.getGameStatus())) {
            return false;
        }
		
		if (!id.equals(game.getCurrentPlayerId())) {
			return false;
		}
		
		return null;
	}
	
	@JsonIgnore
	public Boolean isCurrentPlayer() {
		if (!id.equals(game.getCurrentPlayerId())) {
			System.out.println("Not current player: " + id + " != " + game.getCurrentPlayerId());
			return false;
		}
		
		return true;
	}
	public List<BidOption> selectBid() {
		if (!isCurrentPlayer()) {
			return null;
		}
		List<BidOption> result = new ArrayList<>();
		HashMap<AnimalCardEnum, Integer> currentPlayerAnimalCardMap = handCard.getAnimalCardMap();
		
		ConcurrentHashMap<String, PlayUser> players = room.getPlayers();
		for(PlayUser player : players.values()) {
			if (id.equals(player.getId())) continue;
			
			List<AnimalCardEnum> playerAnimalCardMap = new ArrayList<>();
			player.getHandCard().getAnimalCardMap().forEach((animalCard, quantity) -> {
				if (currentPlayerAnimalCardMap.containsKey(animalCard)) {
					playerAnimalCardMap.add(animalCard);
				}
				
				result.add(new BidOption(player.getId(), playerAnimalCardMap));
			});
		}
		
		game.setGameStatus(GameStatusEnum.TRADING_PLAYER);
		
		return result;
	}

}
