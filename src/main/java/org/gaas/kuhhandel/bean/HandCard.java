package org.gaas.kuhhandel.bean;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandCard {
	private Integer hardCardId;
	private String userId;
	private List<Card> animalCard;
	private List<MoneyCard> moneyCards;
	
}
