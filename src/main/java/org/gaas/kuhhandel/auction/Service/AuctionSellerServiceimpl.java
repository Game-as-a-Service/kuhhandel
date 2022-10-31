package org.gaas.kuhhandel.auction.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.gaas.kuhhandel.auction.Bean.HardCard;
import org.gaas.kuhhandel.auction.Bean.PlayHistory;
import org.gaas.kuhhandel.auction.Bean.PlayUser;
import org.gaas.kuhhandel.auction.Respositor.PlayHistoryRepositor;
import org.gaas.kuhhandel.auction.Respositor.PlayRespository;
import org.gaas.kuhhandel.auction.eum.Animal;

public class AuctionSellerServiceimpl implements AuctionSellerService{
	private PlayHistoryRepositor playHistoryRepositor;
	PlayRespository playRespository;
	PlayHistory playHistory;
	
	public AuctionSellerServiceimpl(PlayHistoryRepositor playHistoryRepositor,PlayRespository playRespository,PlayHistory playHistory) {
		this.playHistoryRepositor = playHistoryRepositor;
		this.playRespository=playRespository;
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
	public Map<String, Object> auctionBegin(String barkUser,String partNumber, Animal animal) {
		Map<String, Object> map = new HashMap<>();
		int amount = 0;
		try {
			amount = bark(barkUser,partNumber, animal);
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
		playRespository.update(animal);
	}

	/**
	 * 開始喊價
	 * 
	 * @param partNumber
	 * @param animal
	 * @return
	 * @throws InterruptedException
	 */
	private int bark(String barkUser,String partNumber, Animal animal) throws InterruptedException {
		int bidAmount = 0;
		int barkNumber = 1;
		String barkChangerUser="";
		for (int i = 1; barkNumber < 4; i++) {
			TimeUnit.SECONDS.sleep(1);
			playHistory = playHistoryRepositor.findNewBarkPart(partNumber);
			System.out.println(playHistory);
			if (playHistory != null) {
				barkChangerUser=playHistory.getPlayId()==null?"":playHistory.getPlayId();
			}
			if(!"".equals(barkChangerUser)){
				if(!barkChangerUser.equals(barkUser)) {
					int amount=playHistory.getMoney();
					if(amount>=bidAmount+10) {
						barkUser=barkChangerUser;
						bidAmount = playHistory.getMoney();
						i=1;
						barkNumber=1;
						playHistoryRepositor.update(playHistory);
					}
				}
			}
			System.out.println("動物" + animal.getDesc() + "，價錢:" + bidAmount + "，第" + barkNumber + "幾次喊價");
			barkNumber++;
		}
		System.out.println("成交");
		return bidAmount;
	}
	/**
	 * @param playUser 	 購買人員
	 * @param partNumber 拍賣桌號
	 * @param buyAmount	 結標金額
	 * @param animal	 拍賣動物卡
	 * @param isBuy		 是否購買得標商品
	 */
	public Map<String, Object> buyAuction(String playUser,String partNumber, int buyAmount, Animal animal,boolean isBuy) {
		Map<String,Object> map=new HashMap<>();
		if(isBuy) {
			PlayUser playUserData= playRespository.find(playUser);
			
			if(playUserData!=null) {
				HardCard hardCard = playUserData.getHardCard() == null ? new HardCard() : playUserData.getHardCard();
				Integer amount=playUserData.getAmount()==null?0:playUserData.getAmount();
				if(amount>=buyAmount) {
					amount=amount-buyAmount;
					List<Animal> hardAnimalCard = hardCard.getAnimalCard();					
					System.out.println(hardAnimalCard);
					hardAnimalCard.add(animal);
					hardCard.setAnimalCard(hardAnimalCard);
					playUserData.setHardCard(hardCard);
					playUserData.setAmount(amount);
					System.out.println("購買後:"+hardAnimalCard);
					System.out.println("購買後:"+playUserData);
					playRespository.update(playUserData);
					System.out.println("購買成功，獲得卡片");
					map.put("status", "success");
					map.put("msg", "賣方獲得卡");
				}else {
					System.out.println("角色金額不足");
					//需顯示底牌
					System.out.println(hardCard.getAmountCard());
					map.put("status", "error");
					map.put("msg", "角色金額不足,顯示卡片");
				}
			}else {
				System.out.println("查無此人員，交易失敗");	
				map.put("status", "error");
				map.put("msg", "交易異常，查無此人員");
				
			}
		}else {
			System.out.println("賣方不購買");	
			map.put("status", "error");
			map.put("msg", "賣方不交易");
		}
		return map;
	}

}
