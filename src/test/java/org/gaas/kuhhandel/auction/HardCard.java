package org.gaas.kuhhandel.auction;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HardCard {
	private Integer hardCardId;
	private String userId;
	private List<Animal> animalCard;
	private List<Integer> amountCard;
	
}
