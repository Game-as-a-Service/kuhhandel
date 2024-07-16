
package org.gaas.kuhhandel.bean;

import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;

@Data
public class Room {
	private String id;
	private String name;
	private ConcurrentHashMap<String, PlayUser> players = new ConcurrentHashMap<>();
	private Game game;
}