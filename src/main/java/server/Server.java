package server;

import client.main.Client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server extends Application {

  private static final Logger LOGGER = LogManager.getLogger(Client.class.getName());
  private ArrayList<InetAddress> connectedList = new ArrayList<>();
  private List connected = Collections.synchronizedList(connectedList);
  public final AtomicInteger playerCount = new AtomicInteger(0);
  private final AtomicBoolean running = new AtomicBoolean(false);
  private String threadName;

  private int playerLastCount = 0;
  private ServerSocket serverSocket = null;
  private int serverPort = 4446;
  private ExecutorService executor;
  private Server server;

  private DatagramSocket socket;

  public static void main(String args[]) {
    launch(args);
  }

  public void init() {
    server = this;
    executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    threadName = "Server";
    try {
      this.serverSocket = new ServerSocket(4445);
      this.socket = new DatagramSocket();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    running.set(true);
    LOGGER.debug("Running " + threadName);
    /** Receiver from clients */
    executor.execute(new ServerReceiver(this, serverSocket, connected));
    while (running.get()) {
      if (playerLastCount < playerCount.get()) {
        playerLastCount++;
        executor.execute(new ServerReceiver(server, serverSocket, connected));
      }
      Thread.sleep(1000);
    }

  }

  public void stop() {
    running.set(false);
  }


  public void sendToClients(byte[] buffer, InetAddress player) {
    synchronized (connected) {
      connected.forEach(address -> {
        try {
          if (address != player) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
                (InetAddress) address, serverPort);
            socket.send(packet);
          }
        } catch (UnknownHostException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
    }
  }


}
