package server.websocket;

import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

}