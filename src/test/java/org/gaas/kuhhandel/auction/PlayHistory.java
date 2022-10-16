package org.gaas.kuhhandel.auction;



import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlayHistory {
	private int id;
	private String playId;
	private int money;
	private boolean isGetData;
	private Date creatTime;
	private String partNumber;
}
