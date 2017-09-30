import java.io.*;
import java.net.*;

public class svr {
  private static ServerSocket ListeningSocket;
  private static Socket ClientSocket;
  private static Socket client;
  private static InputStream ofs;
  private static DataInputStream in;
  private static OutputStream outsvr;
  private static DataOutputStream out;
  private static DataInputStream ins;
  private static String msg;
  private static String[] cmd;

  public static void main(String args[]) throws IOException, ClassNotFoundException {

    if(args.length != 2)
    {
        System.out.println("Error : java server <port> <port for data>");
        System.exit(0);
    }
    int cltport = Integer.parseInt(args[0]);
    int dataport = Integer.parseInt(args[1]);

    cmdSocketStart(cltport);
    //get the host name for the data socket
    ofs = ClientSocket.getInputStream();
    in = new DataInputStream(ofs);

    msg = in.readUTF();
    System.out.println("server name: " + msg);
    dataSocketStart(msg,dataport);



    // store message from client
    msg = in.readUTF();
    System.out.println("message from client:" + msg);
    cmd = msg.trim().split(" ");
    outsvr = client.getOutputStream();
    out = new DataOutputStream(outsvr);
    ins = new DataInputStream(client.getInputStream());

    System.out.println(msg.contains("exit"));
    //while (msg.toLowerCase().contains("exit") != true){
      if (cmd[0].equalsIgnoreCase("GET")) {
        GET();
      } else if (cmd[0].equalsIgnoreCase("SEND")) {
        SEND();

      }
    //}
    disconnect();

  }

  public static void cmdSocketStart(int port) throws UnknownHostException, IOException {
    System.out.println("Connecting to port "+ port);
    ListeningSocket = new ServerSocket(port);
    // ServerSocket svr = new ServerSocket(7005);
    System.out.println("Waiting for client to connect!");
    ClientSocket = ListeningSocket.accept();
    System.out.println("Client has connected to the server");
  }

  public static void dataSocketStart(String host,int port) throws IOException {
    client = new Socket(host, port);
    System.out.println("Connected to "+ host+" and port "+port);
  }

  public static void GET() throws IOException {
    File myFile = new File(cmd[1]);
    boolean exists = myFile.exists();
    System.out.println("FILE EXISTS?" + myFile.exists());
    if ((myFile.exists()) == true) {
      out.writeUTF("1");
      System.out.println("File Exists");
      int size = (int) myFile.length();
      System.out.println("File name:" + cmd[1]);
      FileInputStream fis = new FileInputStream(cmd[1]);
      byte[] buffer = new byte[size];
      // send size of file to server
        out.writeUTF(Integer.toString(size));
      // While stream has data write to buffer
        fis.read(buffer);
        out.write(buffer);
    } else {
        out.writeUTF("0");
      System.out.println("File Doesn't Exist");
    }
  }

  public static void SEND() throws IOException {
    msg = in.readUTF();
    System.out.println("message from client");
    if(msg.matches("1")) {
      try {
        FileOutputStream file1 = new FileOutputStream("totoro1.jpg");
        msg = in.readUTF();
        byte[] buffer = new byte[Integer.parseInt(msg)];
        System.out.println("MESSAGE FROM CLIENT FILE SIZE= " + Integer.parseInt(msg));
        ins.read(buffer);
        file1.write(buffer);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
    }

  }

  public static void disconnect() throws IOException {
    System.out.println("Closing connection");
    ListeningSocket.close();
    ClientSocket.close();
    client.close();
    ofs.close();
    in.close();
    outsvr.close();
    out.close();
    ins.close();
  }
}
