package org.gaas.kuhhandel.bean;

import org.gaas.kuhhandel.eum.Animal;

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
