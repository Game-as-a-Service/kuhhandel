package org.gaas.kuhhandel.repository;

import java.util.List;

import org.gaas.kuhhandel.bean.MoneyCard;

/** 發起交易者
 * 
 * @author User
 *
 */
public interface InitiateTrader {
	
	public List<MoneyCard> bid(Integer money,int number);
}
