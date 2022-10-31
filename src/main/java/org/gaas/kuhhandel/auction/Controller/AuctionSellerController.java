package org.gaas.kuhhandel.auction.Controller;

import java.util.Map;

import org.gaas.kuhhandel.auction.Service.AuctionSellerService;
import org.gaas.kuhhandel.auction.eum.Animal;

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
