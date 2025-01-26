package org.gaas.kuhhandel.exampleMapping;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import org.gaas.kuhhandel.bean.PlayUser;
import org.gaas.kuhhandel.bean.Room;
import org.gaas.kuhhandel.bean.demo.websocket.ResponseData;
import org.gaas.kuhhandel.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class HappyPathTest {

	@LocalServerPort
	private Integer port;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private RoomService roomService;

	private WebSocketStompClient webSocketStompClient;

	@BeforeEach
	void setup() {
		this.webSocketStompClient = new WebSocketStompClient(
				new SockJsClient(List.of(new WebSocketTransport(new StandardWebSocketClient()))));
	}

	@Test
	void verifyHappyPath_createRoom_joinRoom_chat_allReady_startGame() throws Exception {
		String roomName = "happyTest1-joinRoom-chat-ready-startGame";
		Room room = new Room();
		room.setName(roomName);
		String requestBody = objectMapper.writeValueAsString(room);

		//創建房間
		this.mockMvc.perform(post("/rooms").contentType(MediaType.APPLICATION_JSON).content(requestBody))
				.andExpect(status().isOk());

		Room verifyRoom = roomService.getRooms().values().stream().filter(r -> r.getName().equals(roomName)).findFirst()
				.get();
		assertEquals(roomName, verifyRoom.getName());

		String roomId = verifyRoom.getId();

		PlayUser playerA = new PlayUser();
		playerA.setId("playerA");

		PlayUser playerB = new PlayUser();
		playerB.setId("playerB");

		PlayUser playerC = new PlayUser();
		playerC.setId("playerC");

		//加入房間
		this.mockMvc.perform(post("/rooms/" + roomId + "/join").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(playerA))).andExpect(status().isOk());

		this.mockMvc.perform(post("/rooms/" + roomId + "/join").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(playerB)));

		this.mockMvc.perform(post("/rooms/" + roomId + "/join").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(playerC)));

		Collection<PlayUser> players = verifyRoom.getPlayers().values();
		assertEquals(3, players.size());
		players.stream().forEach(p -> assertEquals(0, p.getStatus()));

		//init websocket
		CountDownLatch latch = new CountDownLatch(1);
		BlockingQueue<ResponseData> blockingQueue = new LinkedBlockingQueue<>();
		webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
		StompSession session = webSocketStompClient.connectAsync(getWsPath(), new StompSessionHandlerAdapter() {
		}).get(1, SECONDS);

		//訂閱房間的公開頻道
		subscribeByRoomId(roomId, latch, blockingQueue, session);

		//playerA 聊天
		Map<String, String> payloadMap = new HashMap<>();
		payloadMap.put("playerId", playerA.getId());
		payloadMap.put("message", "Hello, Mike!");
		String payload = objectMapper.writeValueAsString(payloadMap);
		session.send("/app/rooms/" + roomId + "/talk", payload);

		await().atMost(5, SECONDS)
				.untilAsserted(() -> assertEquals("playerA: Hello, Mike!", blockingQueue.poll().getData()));
		
		//playerA 準備好
		playerA.setStatus(1);
		session.send("/app/rooms/" + roomId + "/status", playerA);
		await().atMost(5, SECONDS).untilAsserted(() -> assertEquals(0, latch.getCount()));
		assertEquals(1, objectMapper.convertValue(blockingQueue.poll().getData(), Room.class).getPlayers().get(playerA.getId()).getStatus());
		
		//playerB 準備好
		playerB.setStatus(1);
		session.send("/app/rooms/" + roomId + "/status", playerB);
		await().atMost(5, SECONDS).untilAsserted(() -> assertEquals(0, latch.getCount()));
		assertEquals(1, objectMapper.convertValue(blockingQueue.poll().getData(), Room.class).getPlayers().get(playerB.getId()).getStatus());
		
		//playerC 準備好
		playerC.setStatus(1);
		session.send("/app/rooms/" + roomId + "/status", playerC);
		await().atMost(5, SECONDS).untilAsserted(() -> assertEquals(0, latch.getCount()));
		assertEquals(1, objectMapper.convertValue(blockingQueue.poll().getData(), Room.class).getPlayers().get(playerC.getId()).getStatus());
	}

	private void subscribeByRoomId(String roomId, CountDownLatch latch, BlockingQueue<ResponseData> blockingQueue, StompSession session) {
		session.subscribe("/topic/rooms/" + roomId, new StompFrameHandler() {

			@Override
			public Type getPayloadType(StompHeaders headers) {
				return ResponseData.class;
			}

			@Override
			public void handleFrame(StompHeaders headers, Object payload) {
				latch.countDown();
				blockingQueue.add((ResponseData) payload);
			}
		});
	}

	private String getWsPath() {
		return String.format("ws://localhost:%d/game", port);
	}
}