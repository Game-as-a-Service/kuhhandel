package org.gaas.kuhhandel.auction;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class AuctionSellerControllerTest {
	private AuctionSellerController auctionSellerController;
	private AuctionSellerService auctionSellerService;
	private PlayHistoryRepositor playHistoryRepositor ;
	private PlayRespository respository;
	private PlayHistory playHistory;
	@Before
	public void init() {
		auctionSellerService = Mockito.mock(AuctionSellerService.class);
		auctionSellerController = new AuctionSellerController(auctionSellerService);
		playHistoryRepositor =Mockito.mock(PlayHistoryRepositor.class);
		respository=Mockito.mock(PlayRespository.class);
		playHistory=Mockito.mock(PlayHistory.class);
		
		auctionSellerService = new AuctionSellerService(playHistoryRepositor,respository,playHistory);
	}
	//檢查有沒有呼叫抽牌方法
	@Test
	@Ignore
	public void SellerShuffle() {
		given(auctionSellerService.shuffleAnimal()).willReturn(any(Animal.class));
		auctionSellerController.shuffle();
		verify(auctionSellerService, times(1)).shuffleAnimal();
	}
	
	//檢查拍賣完，是否無人喊價，賣家是否免費獲得卡片
	@Test
	public void auctionBegin() {	
		given(playHistoryRepositor.findNewBarkPart(any(String.class))).willReturn(any(PlayHistory.class));
		//given(respository.update(any(Animal.class))).willReturn(any(Integer.class));
		auctionSellerService.auctionBegin("a001", Animal.GOAT);
		verify(auctionSellerService, times(0)).getFreeCard(Animal.GOAT);
	}

}
