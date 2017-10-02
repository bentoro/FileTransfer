import java.io.*;
import java.net.*;

public class svr {
  private static ServerSocket listeningSocket;
private static Socket clientSocket;
private static Socket client;
private static DataInputStream cmdIn;
private static DataOutputStream dataOut;
private static DataInputStream dataIn;
private static FileInputStream fileIn;
private static FileOutputStream fileOut;
private static String msg;
private static String[] cmd;
private static String hostName;

public static void main(String args[]) throws IOException, ClassNotFoundException {
  if (args.length != 3) {
    System.out.println("Error : java server <port> <host of client> <port for data>");
    System.exit(0);
  }
  int cltport = Integer.parseInt(args[0]);
  int dataport = Integer.parseInt(args[2]);
  hostName = args[1];

  cmdSocketStart(cltport);
  // get the host name for the data socket
  cmdIn = new DataInputStream(clientSocket.getInputStream());

  //hostName = cmdIn.readUTF();
  //System.out.println("server name: " + hostName);

  // store message from client
  msg = cmdIn.readUTF();
  System.out.println("message from client:" + msg);
  cmd = msg.trim().split(" ");
  if (!msg.toLowerCase().contains("exit")){
    dataSocketStart(hostName, dataport);
    while (msg.toLowerCase().contains("exit") != true) {
      if (msg.toLowerCase().contains("get")) {
        System.out.println("GET FUNCTION CALLED");
        GET();
        // msg = null;
      } else if (msg.toLowerCase().contains("send")) {
        System.out.println("SEND FUNCTION CALLED");
        SEND();
        // msg = null;
      }
      msg = cmdIn.readUTF();
      parseFileName();
    }
    disconnect(0);
  } else {
    disconnect(1);
  }
}

public static void cmdSocketStart(int port) throws UnknownHostException, IOException {
  System.out.println("------------------------------------------------------");
  System.out.println("Connecting to port " + port);
  listeningSocket = new ServerSocket(port);
  System.out.println("Waiting for client to connect!");
  clientSocket = listeningSocket.accept();
  System.out.println("Client has connected to the server");
}

public static void dataSocketStart(String host, int port) throws IOException {
  System.out.println("------------------------------------------------------");
  client = new Socket(host, port);
  System.out.println("Connected to " + host + " and port " + port);
}

public static void GET() throws IOException {
  System.out.println("------------------------------------------------------");
  System.out.println("-                    GET FUNCTION                    -");
  System.out.println("------------------------------------------------------");
  dataOut = new DataOutputStream(client.getOutputStream());
  File myFile = new File(cmd[1]);
  boolean exists = myFile.exists();
  if ((myFile.exists()) == true) {
    dataOut.writeUTF("1");
    System.out.println("File: "+cmd[1]+" exists and is sending to client");
    int size = (int) myFile.length();
    System.out.println("File name:" + cmd[1]+" is of size " + size+ "bytes");
    fileIn = new FileInputStream(cmd[1]);
    byte[] buffer = new byte[size];
    // send size of file to server
    dataOut.writeUTF(Integer.toString(size));
    // While stream has data write to buffer
    fileIn.read(buffer);
    dataOut.write(buffer);
    System.out.println("File sucessfully sent to the client!");
  } else {
    dataOut.writeUTF("0");
    System.out.println("File Doesn't Exist");
  }
}

public static void SEND() throws IOException {
  System.out.println("------------------------------------------------------");
  System.out.println("-                   SEND FUNCTION                    -");
  System.out.println("------------------------------------------------------");
  dataIn = new DataInputStream(client.getInputStream());
  msg = cmdIn.readUTF();
  if (msg.matches("1")) {
    try {
      fileOut = new FileOutputStream(cmd[1]);
      msg = cmdIn.readUTF();
      byte[] buffer = new byte[Integer.parseInt(msg)];
      System.out.println("Receiving file from client of size: " + Integer.parseInt(msg)+"bytes");
      dataIn.read(buffer);
      fileOut.write(buffer);
      System.out.println("File sucessfully received from client!");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  } else {
  }
}
public static void parseFileName() {
  cmd = msg.trim().split(" ");
}

public static void disconnect(int t) throws IOException {
  System.out.println("Closing connection");
  if (t == 1){
    listeningSocket.close();
    clientSocket.close();
    cmdIn.close();
  } else {
    client.close();
  }
}
}
