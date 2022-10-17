package org.gaas.kuhhandel.auction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayUser {
	private String id;
	private Integer amount;
	private Integer hardCardId;
	private HardCard hardCard;
}
