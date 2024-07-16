package org.gaas.kuhhandel.service.demo.websocket;

import java.util.concurrent.ConcurrentHashMap;

import org.gaas.kuhhandel.bean.demo.websocket.Room;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("demo.websocket.RoomService")
public class RoomService {
	private ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

	public ConcurrentHashMap<String, Room> getRooms() {
		return rooms;
	}
}
