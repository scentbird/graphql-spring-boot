package graphql.kickstart.autoconfigure.web.servlet;

import graphql.kickstart.servlet.GraphQLWebsocketServlet;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import org.springframework.context.Lifecycle;
import org.springframework.web.socket.server.standard.ServerEndpointRegistration;

/**
 * @author Andrew Potter
 */
public class GraphQLWsServerEndpointRegistration extends ServerEndpointRegistration
    implements Lifecycle {

  private final GraphQLWebsocketServlet servlet;
  private final WsCsrfFilter csrfFilter;

  public GraphQLWsServerEndpointRegistration(
      String path, GraphQLWebsocketServlet servlet, WsCsrfFilter csrfFilter) {
    super(path, servlet);
    this.servlet = servlet;
    this.csrfFilter = csrfFilter;
  }

  @Override
  public boolean checkOrigin(String originHeaderValue) {
    return servlet.checkOrigin(originHeaderValue);
  }

  @Override
  public void modifyHandshake(
      ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
    super.modifyHandshake(sec, request, response);
    csrfFilter.doFilter(request);
    servlet.modifyHandshake(sec, request, response);
  }

  @Override
  public void start() {
    // do nothing
  }

  @Override
  public void stop() {
    servlet.beginShutDown();
  }

  @Override
  public boolean isRunning() {
    return !servlet.isShutDown();
  }
}
