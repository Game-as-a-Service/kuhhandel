package org.gaas.kuhhandel.bean;

import org.gaas.kuhhandel.eum.ControllerTypeEnum;
import org.gaas.kuhhandel.eum.GameStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Game {
	private String gameId;
	private int currentRound;
	private GameStatusEnum gameStatus;
	//控制者是系統還是玩家
	private ControllerTypeEnum controller;
	private String currentPlayerId;
	private Bid bid;
}
