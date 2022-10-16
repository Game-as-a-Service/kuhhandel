package org.gaas.kuhhandel.auction;

public interface PlayHistoryRepositor {

	PlayHistory findNewBarkPart(String partNumber);

	int insert();

}
