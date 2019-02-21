package server;

import client.main.Client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerReceiver implements Runnable {

  private static final Logger LOGGER = LogManager.getLogger(Client.class.getName());
  private InetAddress player;
  private Socket socket;
  private Server server;
  private ServerSocket serverSocket;
  private List connected;


  public ServerReceiver(Server server, ServerSocket serverSocket, List connected) {
    this.server = server;
    this.serverSocket = serverSocket;
    this.connected = connected;
  }


  @Override
  public void run() {
    String message = null;
    BufferedReader input = null;
    try {
      socket = this.serverSocket.accept();
      input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      /** Client Join */
      message = input.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(message);
    int packetID = Integer.parseInt(message.split(",")[0]);
    if (packetID == 0 && server.playerCount.get() < 4) {
      server.playerCount.getAndIncrement();
      connected.add(socket.getInetAddress());
      player = socket.getInetAddress();

      /** Main Loop */
      while (true) {
        try {
          message = input.readLine();
          server.sendToClients(message.getBytes(), player);
        } catch (SocketTimeoutException e) {
          server.playerCount.decrementAndGet();
          connected.remove(socket.getInetAddress());
          break;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
