package org.gaas.kuhhandel.auction;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class AuctionSellerControllerTest {
	private AuctionSellerController auctionSellerController;
	private AuctionSellerService auctionSellerService;
	private PlayHistoryRepositor playHistoryRepositor;
	private PlayRespository respository;
	private PlayHistory playHistory;

	@Before
	public void init() {
		playHistoryRepositor = Mockito.mock(PlayHistoryRepositor.class);
		respository = Mockito.mock(PlayRespository.class);
		playHistory = Mockito.mock(PlayHistory.class);

//		auctionSellerService = Mockito.mock(AuctionSellerService.class);
		auctionSellerService = new AuctionSellerService(playHistoryRepositor, respository, playHistory);
		auctionSellerController = new AuctionSellerController(auctionSellerService);
	}

	// 檢查有沒有呼叫抽牌方法
	@Test
//	@Ignore
	public void SellerShuffle() {
//		given(auctionSellerService.shuffleAnimal()).willReturn(any(Animal.class));
		Animal animal=auctionSellerController.shuffle();
//		Animal animal= auctionSellerService.shuffleAnimal();
		System.out.println(animal);
//		verify(auctionSellerService, times(1)).shuffleAnimal();
	}

	/**
	 * 拍賣卡片:山羊 情境:拍賣,第一次無人喊價，第2次有人喊價，第三次重複人喊價
	 */
	@Test
	@Ignore
	public void auctionBegin_test_3_bark_1_null_2_success_bark_3_repeat_bark() {
		PlayHistory playHistory = PlayHistory.builder().playId("use001").money(30).isGetData(true)
				.creatTime(new Timestamp(new Date().getTime())).partNumber("a001").build();
		PlayHistory playHistory2 = PlayHistory.builder().playId("use001").money(50).isGetData(true)
				.creatTime(new Timestamp(new Date().getTime())).partNumber("a001").build();
		when(playHistoryRepositor.findNewBarkPart(any(String.class))).thenReturn(new PlayHistory())
				.thenReturn(playHistory).thenReturn(playHistory2);
		given(playHistoryRepositor.update(playHistory)).willReturn(1);
		Map<String, Object> map = auctionSellerService.auctionBegin("use123", "a001", Animal.GOAT);
		assertEquals(map.get("Status"), "");
		assertEquals(map.get("Animal"), Animal.GOAT);
		assertEquals(map.get("Amount"), 30);
		// verify(auctionSellerService, times(0)).getFreeCard(Animal.GOAT);
	}

	/**
	 * 拍賣卡片:山羊 情境:拍賣,第1次賣家喊價，第2次有人喊價未超過10元,第3次有人喊價有超過10元
	 */
	@Test
	@Ignore
	public void auctionBegin_test_3_bark_1_sellerHimself_2_error_bark_3_success_bark() {
		PlayHistory playHistory = PlayHistory.builder().playId("use123").money(30).isGetData(true)
				.creatTime(new Timestamp(new Date().getTime())).partNumber("a001").build();
		PlayHistory playHistory2 = PlayHistory.builder().playId("use001").money(5).isGetData(true)
				.creatTime(new Timestamp(new Date().getTime())).partNumber("a001").build();
		PlayHistory playHistory3 = PlayHistory.builder().playId("use001").money(10).isGetData(true)
				.creatTime(new Timestamp(new Date().getTime())).partNumber("a001").build();
		when(playHistoryRepositor.findNewBarkPart(any(String.class))).thenReturn(playHistory).thenReturn(playHistory2)
				.thenReturn(playHistory3);

		Map<String, Object> map = auctionSellerService.auctionBegin("use123", "a001", Animal.GOAT);
		assertEquals(map.get("Status"), "");
		assertEquals(map.get("Animal"), Animal.GOAT);
		assertEquals(map.get("Amount"), 10);
		// verify(auctionSellerService, times(0)).getFreeCard(Animal.GOAT);
	}

	/**
	 * 拍賣卡片:山羊 情境:拍賣,三次無人喊價
	 */
	@Test
	@Ignore
	public void auctionBegin_test_not_bark() {
		given(playHistoryRepositor.findNewBarkPart(any(String.class))).willReturn(new PlayHistory());
		given(playHistoryRepositor.update(playHistory)).willReturn(1);
		given(respository.update(any(Animal.class))).willReturn(1);

		Map<String, Object> map = auctionSellerService.auctionBegin("use123", "a001", Animal.GOAT);
		System.out.println(map);
		assertEquals(map.get("Status"), "Success");
	}
	//角色1
	public PlayUser PlayUserinit() {
		List<Integer> amountList = new ArrayList<>();
		amountList.add(10);
		amountList.add(30);
		amountList.add(30);
		amountList.add(30);
		List<Animal> animalCard = new ArrayList<>();
		animalCard.add(Animal.CATTLE);
		HardCard hardCard = HardCard.builder().amountCard(amountList).animalCard(animalCard).hardCardId(1).userId("user123").build();
		PlayUser playUser = PlayUser.builder().amount(100).hardCardId(1).id("user123").hardCard(hardCard).build();
		return playUser;
	}
	//角色2
	public PlayUser PlayUser2init() {
		List<Integer> amountList = new ArrayList<>();
		amountList.add(10);
		List<Animal> animalCard = new ArrayList<>();
		animalCard.add(Animal.CATTLE);
		HardCard hardCard = HardCard.builder().amountCard(amountList).animalCard(animalCard).hardCardId(1).userId("user123").build();
		PlayUser playUser = PlayUser.builder().amount(10).hardCardId(1).id("user123").hardCard(hardCard).build();
		return playUser;
	}

	// 拍賣完賣家買下商品 查無此人
	@Test
	@Ignore
	public void sellerHimself_error_findError() {
		PlayUser playUser = PlayUserinit();
		System.out.println(playUser);
		given(respository.find("user123")).willReturn(playUser);
		given(respository.update(any(PlayUser.class))).willReturn(1);
		auctionSellerService.buyAuction("A001", "a001", 30, Animal.GOAT, true);

	}

	// 拍賣完賣家買下商品 購買成功
	@Test
	@Ignore
	public void sellerHimself_success_getCard() {
		PlayUser playUser = PlayUserinit();
		System.out.println(playUser);
		given(respository.find("user123")).willReturn(playUser);
		given(respository.update(any(PlayUser.class))).willReturn(1);
		auctionSellerService.buyAuction("user123", "a001", 30, Animal.GOAT, true);

	}

	// 拍賣完賣家買下商品 購買失敗 金額不足 需要顯示底牌
	@Test
	@Ignore
	public void sellerHimself_error_showCard() {
		PlayUser playUser = PlayUser2init();
		System.out.println(playUser);
		given(respository.find("user123")).willReturn(playUser);
		given(respository.update(any(PlayUser.class))).willReturn(1);
		auctionSellerService.buyAuction("user123", "a001", 30, Animal.GOAT, true);

	}
}
