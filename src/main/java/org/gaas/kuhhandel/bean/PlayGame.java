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
public class PlayGame {
	
	private int gameId;
	private List<PlayUser> gamePlayer;
	private boolean startGame;

}
