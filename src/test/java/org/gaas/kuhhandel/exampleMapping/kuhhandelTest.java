package org.gaas.kuhhandel.exampleMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.gaas.kuhhandel.bean.Bid;
import org.gaas.kuhhandel.bean.BidOption;
import org.gaas.kuhhandel.bean.Game;
import org.gaas.kuhhandel.bean.HandCard;
import org.gaas.kuhhandel.bean.MoneyCard;
import org.gaas.kuhhandel.bean.PlayUser;
import org.gaas.kuhhandel.bean.demo.websocket.Player;
import org.gaas.kuhhandel.bean.demo.websocket.Room;
import org.gaas.kuhhandel.bean.demo.websocket.RoomB;
import org.gaas.kuhhandel.eum.AnimalCardEnum;
import org.gaas.kuhhandel.eum.ControllerTypeEnum;
import org.gaas.kuhhandel.eum.GameStatusEnum;
import org.gaas.kuhhandel.utils.RandomIdUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 幕後交易測試
 */
@ExtendWith(MockitoExtension.class)
public class kuhhandelTest {

	@BeforeEach
	public void init() {

	}

	private PlayUser initRespondent(Game game, String id, HashMap<AnimalCardEnum, Integer> animalCardMap,
			List<MoneyCard> respondentMoneyCards) {
		HandCard respondentHandCard = new HandCard(id, animalCardMap, respondentMoneyCards);
		PlayUser playUser = new PlayUser(game, id, 2, 50, 0, respondentHandCard);
		return Mockito.spy(playUser);
	}

	private PlayUser initTrader(Game game, String id) {
		HashMap<AnimalCardEnum, Integer> animalCardMap = new HashMap<>();
		animalCardMap.put(AnimalCardEnum.HORSE, 1);
		animalCardMap.put(AnimalCardEnum.DOG, 1);
		List<MoneyCard> initiateTraderMoneyCards = new ArrayList<MoneyCard>();
		MoneyCard iTmoneyCard1 = new MoneyCard(100, 1);
		MoneyCard iTmoneyCard2 = new MoneyCard(200, 1);
		initiateTraderMoneyCards.add(iTmoneyCard1);
		initiateTraderMoneyCards.add(iTmoneyCard2);
		HandCard initiateTraderHandCard = new HandCard(id, animalCardMap, initiateTraderMoneyCards);
		PlayUser playUser = new PlayUser(game, id, 2, 60, 0, initiateTraderHandCard);
		return Mockito.spy(playUser);
	}

	/**
	 * 選擇幕後交易
	 */
	@Test
	public void givenNexRroundStarts_whenTrading_thenUpdateGameData() {
		RoomB room = new RoomB();
		room.setId(RandomIdUtils.generateRandomId());
		room.setName("Room");
		ConcurrentHashMap<String, PlayUser> players = room.getPlayers();
		Game game = new Game();
		game.setGameId(RandomIdUtils.generateRandomId());
		game.setRoom(room);
		game.setCurrentRound(7);
		game.setGameStatus(GameStatusEnum.ROUND_STARTS);
		game.setController(ControllerTypeEnum.PLAYER);

		// 初始化發起交易者手牌
		PlayUser playUserA = initTrader(game, "1");
		players.put(playUserA.getId(), playUserA);

		// 初始化回答者手牌
		HashMap<AnimalCardEnum, Integer> animalCardMapB = new HashMap<>();
		animalCardMapB.put(AnimalCardEnum.HORSE, 1);
		List<MoneyCard> respondentMoneyCardsB = new ArrayList<MoneyCard>();
		respondentMoneyCardsB.add(new MoneyCard(50, 2));
		respondentMoneyCardsB.add(new MoneyCard(200, 1));
		PlayUser playUserB = initRespondent(game, "2", animalCardMapB, respondentMoneyCardsB);
		players.put(playUserB.getId(), playUserB);

		HashMap<AnimalCardEnum, Integer> animalCardMapC = new HashMap<>();
		animalCardMapC.put(AnimalCardEnum.DOG, 1);
		List<MoneyCard> respondentMoneyCardsC = new ArrayList<MoneyCard>();
		respondentMoneyCardsC.add(new MoneyCard(50, 2));
		respondentMoneyCardsC.add(new MoneyCard(200, 1));
		PlayUser playUserC = initRespondent(game, "3", animalCardMapC, respondentMoneyCardsC);
		players.put(playUserC.getId(), playUserC);

		game.setCurrentPlayerId(playUserA.getId());

		// given
		// given(playUserA.isCurrentPlayer()).willReturn(true);

		// when
		List<BidOption> BidOptions = playUserA.selectBid();

		// then
		then(playUserA).should().isCurrentPlayer();

		assertEquals(BidOptions.size(), 2);
		List<BidOption> validBidOptions = List.of(new BidOption("2", List.of(AnimalCardEnum.HORSE)),
				new BidOption("3", List.of(AnimalCardEnum.DOG)));

		// 遊戲狀態變更為"幕後交易 被指定玩家 選擇回合權"
		assertEquals(GameStatusEnum.TRADING_PLAYER, game.getGameStatus());
		// 驗證選擇幕後交易的回傳值
		assertEquals(validBidOptions, BidOptions);
		// 回合控制權玩家A交易者
		assertEquals(ControllerTypeEnum.PLAYER, game.getController());
		assertEquals(playUserA.getId(), game.getCurrentPlayerId());
	}

	/**
	 * 接受出價
	 */
	@Test
	public void givenValidBid_whenAcceptBid_thenUpdateGameData() {
		RoomB room = new RoomB();
		room.setId(RandomIdUtils.generateRandomId());
		room.setName("Room");
		ConcurrentHashMap<String, PlayUser> players = room.getPlayers();
		Game game = new Game();
		game.setGameId(RandomIdUtils.generateRandomId());
		game.setRoom(room);
		game.setCurrentRound(9);
		game.setGameStatus(GameStatusEnum.TRADING_PLAYER);
		game.setController(ControllerTypeEnum.PLAYER);

		// 初始化發起交易者手牌
		PlayUser playUserA = initTrader(game, "1");
		players.put(playUserA.getId(), playUserA);

		// 初始化回答者手牌
		HashMap<AnimalCardEnum, Integer> animalCardMapB = new HashMap<>();
		animalCardMapB.put(AnimalCardEnum.HORSE, 1);
		List<MoneyCard> respondentMoneyCardsB = new ArrayList<MoneyCard>();
		respondentMoneyCardsB.add(new MoneyCard(50, 2));
		respondentMoneyCardsB.add(new MoneyCard(200, 1));
		PlayUser playUserB = initRespondent(game, "2", animalCardMapB, respondentMoneyCardsB);
		players.put(playUserB.getId(), playUserB);

		HashMap<AnimalCardEnum, Integer> animalCardMapC = new HashMap<>();
		animalCardMapC.put(AnimalCardEnum.DOG, 1);
		List<MoneyCard> respondentMoneyCardsC = new ArrayList<MoneyCard>();
		respondentMoneyCardsC.add(new MoneyCard(50, 2));
		respondentMoneyCardsC.add(new MoneyCard(200, 1));
		PlayUser playUserC = initRespondent(game, "3", animalCardMapC, respondentMoneyCardsC);
		players.put(playUserC.getId(), playUserC);

		game.setCurrentPlayerId(playUserB.getId());

		// 交易的手牌-動物卡
		HashMap<AnimalCardEnum, Integer> animalCardMap = new HashMap<>();
		animalCardMap.put(AnimalCardEnum.HORSE, 1);

		// 交易的手牌-金錢卡
		List<MoneyCard> moneyCards = new ArrayList<MoneyCard>();
		MoneyCard moneyCard = new MoneyCard(100, 1);
		moneyCards.add(moneyCard);

		// 發起幕後交易的資料
		Bid bid = new Bid(null, playUserA.getId(), playUserB.getId(), animalCardMap, moneyCards,null,null, 0);
		given(playUserB.validBid(bid)).willReturn(true);

		// when
		playUserB.acceptBid(bid);

		// then
		// 後端保留幕後交易資料
		assertEquals(game.getBid(), bid);
		assertEquals(game.getBid().getResultStatus(), GameStatusEnum.TRADING_PLAYER_ACCEPT_BID);
		assertEquals(game.getBid().getInitiateTraderId(), bid.getInitiateTraderId());
		assertEquals(game.getBid().getRespondentId(), bid.getRespondentId());
		assertEquals(game.getBid().getAnimalCardMap(), bid.getAnimalCardMap());
		assertEquals(game.getBid().getMoneyCards(), bid.getMoneyCards());
		// 遊戲狀態變更為"幕後交易 系統 判斷議價價高者或平手，與分配金錢卡與動物卡"
		assertEquals(game.getGameStatus(), GameStatusEnum.TRADING_JUDGMENT);
		// 回合控制權移交給系統
		assertEquals(game.getController(), ControllerTypeEnum.SYSTEM);
	}

	/**
	 * 選擇還價
	 */
	@Test
	public void givenValidBid_whenDicker_thenUpdateGameData() {
		// 初始化遊戲資料
		RoomB room = new RoomB();
		room.setId(RandomIdUtils.generateRandomId());
		room.setName("Room");
		ConcurrentHashMap<String, PlayUser> players = room.getPlayers();
		Game game = new Game();
		game.setGameId(RandomIdUtils.generateRandomId());
		game.setRoom(room);
		game.setCurrentRound(9);
		game.setGameStatus(GameStatusEnum.TRADING_PLAYER);
		game.setController(ControllerTypeEnum.PLAYER);

		// 初始化發起幕後交易者手牌-玩家A
		PlayUser playUserA = initTrader(game, "1");
		players.put(playUserA.getId(), playUserA);

		// 初始化被指定交易者手牌-玩家B
		HashMap<AnimalCardEnum, Integer> animalCardMapB = new HashMap<>();
		animalCardMapB.put(AnimalCardEnum.HORSE, 1);
		List<MoneyCard> respondentMoneyCardsB = new ArrayList<MoneyCard>();
		respondentMoneyCardsB.add(new MoneyCard(0, 1));
		respondentMoneyCardsB.add(new MoneyCard(50, 2));
		respondentMoneyCardsB.add(new MoneyCard(200, 1));
		PlayUser playUserB = initRespondent(game, "2", animalCardMapB, respondentMoneyCardsB);
		players.put(playUserB.getId(), playUserB);

		// 初始化其他玩家手牌-玩家C
		HashMap<AnimalCardEnum, Integer> animalCardMapC = new HashMap<>();
		animalCardMapC.put(AnimalCardEnum.DOG, 1);
		List<MoneyCard> respondentMoneyCardsC = new ArrayList<MoneyCard>();
		respondentMoneyCardsC.add(new MoneyCard(50, 2));
		respondentMoneyCardsC.add(new MoneyCard(200, 1));
		PlayUser playUserC = initRespondent(game, "3", animalCardMapC, respondentMoneyCardsC);
		players.put(playUserC.getId(), playUserC);

		game.setCurrentPlayerId(playUserB.getId());

		// 交易的手牌-動物卡
		HashMap<AnimalCardEnum, Integer> animalCardMapA = new HashMap<>();
		animalCardMapA.put(AnimalCardEnum.HORSE, 1);

		// 交易的手牌-金錢卡
		List<MoneyCard> moneyCardsA = new ArrayList<MoneyCard>();
		MoneyCard moneyCardA = new MoneyCard(100, 1);
		moneyCardsA.add(moneyCardA);

		// 發起幕後交易的資料
		Bid bid = new Bid(null, playUserA.getId(), playUserB.getId(), animalCardMapA, moneyCardsA,null,null, 0);
		given(playUserB.validBid(bid)).willReturn(true);

		// 被指定玩家選擇的手牌-金錢卡
		List<MoneyCard> moneyCardsB = new ArrayList<MoneyCard>();
		MoneyCard moneyCardB = new MoneyCard(0, 1);
		moneyCardsB.add(moneyCardB);

		// when
		// 被指定玩家B 選擇金錢卡(0元*1)
		bid.setRespondentMoneyCards(moneyCardsB);

		// then
		// 後端保留幕後交易資料
		assertEquals(game.getBid(), bid);
		assertEquals(game.getBid().getResultStatus(), GameStatusEnum.TRADING_PLAYER_DICKER);
		assertEquals(game.getBid().getInitiateTraderId(), bid.getInitiateTraderId());
		assertEquals(game.getBid().getRespondentId(), bid.getRespondentId());
		assertEquals(game.getBid().getAnimalCardMap(), bid.getAnimalCardMap());
		assertEquals(game.getBid().getMoneyCards(), bid.getMoneyCards());
		assertEquals(game.getBid().getRespondentAnimalCardMap(), bid.getRespondentAnimalCardMap());
		assertEquals(game.getBid().getRespondentMoneyCards(), bid.getRespondentMoneyCards());
		// 遊戲狀態變更為"幕後交易 系統 判斷議價價高者或平手，與分配金錢卡與動物卡"
		assertEquals(game.getGameStatus(), GameStatusEnum.TRADING_JUDGMENT);
		// 回合控制權移交給系統
		assertEquals(game.getController(), ControllerTypeEnum.SYSTEM);
	}
}
