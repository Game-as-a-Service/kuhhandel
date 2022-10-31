package org.gaas.kuhhandel.auction.Respositor;

import org.gaas.kuhhandel.auction.Bean.PlayHistory;

public interface PlayHistoryRepositor {
	//查詢目前是否有新的標價
	PlayHistory findNewBarkPart(String partNumber);
	//新增一筆
	int insert();
	//更新記錄檔
	int update(PlayHistory playHistory);

}
