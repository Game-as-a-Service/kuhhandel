package org.gaas.kuhhandel.auction;

public interface PlayRespository {

	int update(Animal animal);

	PlayUser find(String playUser);

	int update(PlayUser playUserData);

}
