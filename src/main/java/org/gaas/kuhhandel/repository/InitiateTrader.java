package org.gaas.kuhhandel.repository;

import java.util.List;

import org.gaas.kuhhandel.bean.Card;
import org.gaas.kuhhandel.bean.HandCard;
import org.gaas.kuhhandel.bean.MoneyCard;

/** 發起交易者
 * 
 * @author User
 *
 */
public interface InitiateTrader {
	
	public List<MoneyCard> bid(List<MoneyCard> moneyCards);
	
	public Card selectCard(Card card);
	
	public HandCard changeHandCard(HandCard handCard);
}
