import java.io.*;
import java.net.*;

public class clt {
public static void main(String args[]) throws UnknownHostException, IOException {

  //creates a socket connecting to the server
  Socket socket = new Socket("LOCALHOST",4444);
  //Socket socket = new Socket("192.168.0.17",7005);
  System.out.println("======================================");
  System.out.println("Connected to 127.0.0.1 and port 7005!");
  System.out.println("======================================");

  BufferedReader input = new BufferedReader (new InputStreamReader(System.in));
  String msg = input.readLine();
  OutputStream ots = socket.getOutputStream();
  DataOutputStream out = new DataOutputStream(ots);
  out.writeUTF(msg);
  //if GET commmand is used
  if (msg.matches("GET")){
    //creates a server socket
    ServerSocket socket1 = new ServerSocket(7005);
    System.out.println("waiting for connection on port 7005");
    Socket clt = socket1.accept();
    System.out.println("CONNECTED");

    DataInputStream in = new DataInputStream(clt.getInputStream());
    FileOutputStream file = new FileOutputStream("totoro1.jpg");
    byte [] buffer = new byte[4096];
    int filesize = 15123;
    int read = 0;
    int totalRead = 0;
    int remaining = filesize;

    while((read = in.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
      totalRead += read;
      remaining -= read;
      System.out.println("read " + totalRead + " bytes.");
      file.write(buffer, 0, read);
    }

  }

  /*
  // Write data to socket
  OutputStream outsvr = socket.getOutputStream();
  DataOutputStream out = new DataOutputStream(outsvr);
  String file = "totoro.jpg";
  System.out.println("File name:" + file);
  FileInputStream fis = new FileInputStream(file);
  byte[] buffer = new byte[4096];
  //copy file to output
  while (fis.read(buffer)>0) {
    out.write(buffer);
  }
  System.out.println("======================================");
  System.out.println("Disconnecting from data transfer socket!");
  out.close();
  socket.close();*/

}
}
