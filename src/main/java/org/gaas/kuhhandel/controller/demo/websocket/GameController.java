package org.gaas.kuhhandel.controller.demo.websocket;

import java.util.concurrent.ConcurrentHashMap;

import org.gaas.kuhhandel.bean.demo.websocket.Player;
import org.gaas.kuhhandel.bean.demo.websocket.ResponseData;
import org.gaas.kuhhandel.bean.demo.websocket.Room;
import org.gaas.kuhhandel.service.demo.websocket.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {
	
	@Autowired
    private RoomService roomService;

    @MessageMapping("/rooms/{roomId}/move")
    @SendTo("/topic/rooms/{roomId}/positions")
    public ResponseData move(@DestinationVariable String roomId, Player player) throws Exception {
    	Room room = roomService.getRooms().get(roomId);
		if (room == null) {
			return ResponseData.error("Room not found");
		}
		
		ConcurrentHashMap<String, Player> players = room.getPlayers();
		if (!players.containsKey(player.getId())) {
			return ResponseData.error("Player not found in room");
		}
		
		Player originalPlayer = players.get(player.getId());
    	originalPlayer.setX(player.getX());
    	originalPlayer.setY(player.getY());
    	return ResponseData.ok(players);
    }
}