package org.gaas.kuhhandel.auction;

import java.util.Map;

public class AuctionSellerController {

	private AuctionSellerService auctionSellerService;

	public AuctionSellerController(AuctionSellerService auctionSellerService) {
		this.auctionSellerService = auctionSellerService;
	}

	public Animal shuffle() {
		Animal animal = auctionSellerService.shuffleAnimal();
		return animal;
	}

	public Map<String, Object> auctionBegin(String barkUser,String partNumber, Animal animal) {
		Map<String, Object> map = auctionSellerService.auctionBegin(barkUser,partNumber, animal);
		return map;
	}

}
