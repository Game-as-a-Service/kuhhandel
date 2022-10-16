package org.gaas.kuhhandel.auction;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class AuctionSellerService {

	private PlayHistoryRepositor playHistoryRepositor;
	PlayRespository respository;
	PlayHistory playHistory;

	public AuctionSellerService(PlayHistoryRepositor playHistoryRepositor,PlayRespository respository,PlayHistory playHistory) {
		this.playHistoryRepositor = playHistoryRepositor;
		this.respository=respository;
		this.playHistory=playHistory;
	}

	public Animal shuffleAnimal() {
		List<String> list = Stream.of(Animal.values()).map(x -> x.toString().toUpperCase()).toList();

		String card = shuffle(list);

		switch (card) {
		case "HORSE": {
			return Animal.HORSE;
		}
		case "CATTLE": {
			return Animal.CATTLE;
		}
		case "DONKEY": {
			return Animal.DONKEY;
		}
		case "GOAT": {
			return Animal.GOAT;
		}
		case "SHEEP": {
			return Animal.SHEEP;
		}
		case "GOOSE": {
			return Animal.GOOSE;
		}
		case "ROOSTER": {
			return Animal.ROOSTER;
		}
		default: {
			return null;
		}
		}

	}

	/**
	 * 抽牌
	 * 
	 * @param data
	 */
	public String shuffle(List<String> data) {
		String card = "";
		Random ran = new Random();
		int i = ran.nextInt(data.size());
		card = data.get(i);
		return card;
	}

	/**
	 * 拍賣開始
	 * 
	 * @param partNumber 拍賣桌號
	 * @param animal     拍賣動物卡
	 * @return
	 */
	public Map<String, Object> auctionBegin(String partNumber, Animal animal) {
		Map<String, Object> map = new HashMap<>();
		int amount = 0;
		try {
			amount = bark(partNumber, animal);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		map.put("Amount", amount);
		map.put("Animal", animal);
		if (amount == 0) {
			getFreeCard(animal);
			System.out.println("拍賣家，獲得卡片");
			map.put("Status", "Success");
		} else {
			map.put("Status", "");
		}
		return map;
	}

	/**
	 * 免費獲得卡片
	 * 
	 * @param animal
	 */
	public void getFreeCard(Animal animal) {
		respository.update(animal);
	}

	/**
	 * 開始喊價
	 * 
	 * @param partNumber
	 * @param animal
	 * @return
	 * @throws InterruptedException
	 */
	private int bark(String partNumber, Animal animal) throws InterruptedException {
		int bidAmount = 0;
		int barkNumber = 1;
		boolean testbark = true;
		for (int i = 1; barkNumber < 4; i++) {
			TimeUnit.SECONDS.sleep(1);
			playHistory = playHistoryRepositor.findNewBarkPart(partNumber);
			if (testbark) {
				if (barkNumber == 3) {
					playHistory = PlayHistory.builder().playId("use123").money(30).isGetData(true)
							.creatTime(new Timestamp(new Date().getTime())).partNumber(partNumber).build();

					testbark = false;
					i = 0;
				}
			}
			// 沒人喊價
			if (playHistory != null) {
				boolean newBark = playHistory.isGetData();
				if (newBark) {
					bidAmount = playHistory.getMoney();
					barkNumber = 1;
				}
			}
			System.out.println("動物" + animal.getDesc() + "，價錢:" + bidAmount + "，第" + barkNumber + "幾次喊價");
			playHistoryRepositor.insert();
			barkNumber++;

		}
		return bidAmount;
	}

}
