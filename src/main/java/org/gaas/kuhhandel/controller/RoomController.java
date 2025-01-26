package org.gaas.kuhhandel.controller;

import java.io.IOException;
import java.util.Map;

import org.gaas.kuhhandel.bean.PlayUser;
import org.gaas.kuhhandel.bean.Room;
import org.gaas.kuhhandel.bean.demo.websocket.ResponseData;
import org.gaas.kuhhandel.service.RoomService;
import org.gaas.kuhhandel.utils.RandomIdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class RoomController {

	@Autowired
    private RoomService roomService;

    @GetMapping("/rooms")
    @ResponseBody
    public ResponseData listRooms() {
        return ResponseData.ok(roomService.getRooms());
    }

    @PostMapping("/rooms")
    @ResponseBody
    public ResponseData createRoom(@RequestBody Room room) {
        String roomId = RandomIdUtils.generateRandomId();
        room.setId(roomId);
        roomService.getRooms().put(roomId, room);
        return ResponseData.ok(room);
    }

    @PostMapping("/rooms/{roomId}/join")
    @ResponseBody
    public ResponseData joinRoom(@PathVariable String roomId, @RequestBody PlayUser player) {
        Room room = roomService.getRooms().get(roomId);
        
		if (room == null) {
			return ResponseData.error("Room not found");
		}
		
		String playerId = player.getId();
		if (room.getPlayers().containsKey(playerId)) {
			return ResponseData.error("Player already in room");
		}
        
		if (room.getPlayers().size() >= 5) {
			return ResponseData.error("Room is full");
		}
		
		player.setStatus(0); // Not prepared
		player.setRoom(room);
		room.getPlayers().put(playerId, player);
		
        return ResponseData.ok(room);
    }

    @PostMapping("/rooms/{roomId}/exit")
    @ResponseBody
    public ResponseData exitRoom(@PathVariable String roomId, @RequestParam String playerId) {
        Room room = roomService.getRooms().get(roomId);
        if (room != null) {
            room.getPlayers().remove(playerId);
        }
        return ResponseData.ok(room);
    }

    @MessageMapping("/rooms/{roomId}/status")
    @SendTo("/topic/rooms/{roomId}")
    public ResponseData updateStatus(@DestinationVariable String roomId, PlayUser player) {
        Room room = roomService.getRooms().get(roomId);
        
		if (room == null) {
			return ResponseData.error("Room not found");
		}
        
        if (room != null && room.getPlayers().containsKey(player.getId())) {
            if (player.getStatus() == 0 || player.getStatus() == 1) {
                room.getPlayers().get(player.getId()).setStatus(player.getStatus());
            } else {
                throw new RuntimeException("Invalid status");
            }
        }
        return ResponseData.ok(room);
    }

    @MessageMapping("/rooms/{roomId}/start")
    @SendTo("/topic/rooms/{roomId}")
    public ResponseData startGame(@DestinationVariable String roomId) {
        Room room = roomService.getRooms().get(roomId);
        if (room != null) {
            long readyPlayers = room.getPlayers().values().stream()
                    .filter(p -> p.getStatus() == 1).count();
            if (readyPlayers >= 3) {
                room.getPlayers().values().forEach(p -> p.setStatus(2));
            } else {
            	return ResponseData.error("Not enough players ready");
            }
        }
        return ResponseData.ok(room);
    }

    @MessageMapping("/rooms/{roomId}/talk")
    @SendTo("/topic/rooms/{roomId}")
    public ResponseData talk(@DestinationVariable String roomId, @Payload String payload) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> payloadMap;
        try {
            payloadMap = mapper.readValue(payload, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Invalid payload", e);
        }
        String playerId = payloadMap.get("playerId");
        String message = payloadMap.get("message");

        Room room = roomService.getRooms().get(roomId);
        
        if (room == null) {
        	return ResponseData.error("Room not found");
        }
        
        if (!room.getPlayers().containsKey(playerId)) {
        	return ResponseData.error("Player not in room");
        }
        
        return ResponseData.ok(playerId + ": " + message);
    }
}
