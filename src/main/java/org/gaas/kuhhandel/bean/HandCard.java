package org.gaas.kuhhandel.bean;

import java.util.List;

import org.gaas.kuhhandel.eum.Animal;

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
	private List<Animal> animalCard;
	private List<MoneyCard> moneyCards;
	
}
