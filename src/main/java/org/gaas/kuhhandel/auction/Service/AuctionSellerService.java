package org.gaas.kuhhandel.auction.Service;

import java.util.List;
import java.util.Map;

import org.gaas.kuhhandel.auction.eum.Animal;


public interface AuctionSellerService {

	

	public Animal shuffleAnimal();

	/**
	 * 抽牌
	 * 
	 * @param data
	 */
	public String shuffle(List<String> data);

	/**
	 * 拍賣開始
	 * 
	 * @param partNumber 拍賣桌號
	 * @param animal     拍賣動物卡
	 * @return
	 */
	public Map<String, Object> auctionBegin(String barkUser,String partNumber, Animal animal);

	/**
	 * 免費獲得卡片
	 * 
	 * @param animal
	 */
	public void getFreeCard(Animal animal);

		/**
	 * @param playUser 	 購買人員
	 * @param partNumber 拍賣桌號
	 * @param buyAmount	 結標金額
	 * @param animal	 拍賣動物卡
	 * @param isBuy		 是否購買得標商品
	 */
	public Map<String, Object> buyAuction(String playUser,String partNumber, int buyAmount, Animal animal,boolean isBuy);

}
