package org.gaas.kuhhandel.auction.Respositor;

import org.gaas.kuhhandel.auction.Bean.PlayUser;
import org.gaas.kuhhandel.auction.eum.Animal;

public interface PlayRespository {

	int update(Animal animal);

	PlayUser find(String playUser);

	int update(PlayUser playUserData);

}
