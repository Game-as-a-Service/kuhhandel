package org.gaas.kuhhandel.auction.Bean;

import org.gaas.kuhhandel.auction.eum.Animal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Card {
	private Animal animal;
	private int fraction;
	
	
}
