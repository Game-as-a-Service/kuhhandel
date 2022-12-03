package org.gaas.kuhhandel.repository;

import java.util.List;

import org.gaas.kuhhandel.bean.HandCard;
import org.gaas.kuhhandel.bean.MoneyCard;

/** 回答者
 * 
 * @author User
 *
 */
public interface Respondent {
	public List<MoneyCard> bid(Integer money,int number);
	
	public void acceptBid(List<MoneyCard> moneyCards);
	
	public HandCard changeHandCard(HandCard handCard);
	
}
