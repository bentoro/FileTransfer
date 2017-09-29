import java.io.*;
import java.net.*;

public class svr {
public static void main(String args[]) throws IOException, ClassNotFoundException {
  System.out.println("Connecting to port 7005");
  ServerSocket svr = new ServerSocket(4444);
  //ServerSocket svr = new ServerSocket(7005);
  System.out.println("======================================");
  System.out.println("Waiting for client to connect!");
  Socket socket = svr.accept();
  System.out.println("======================================");
  System.out.println("Client has connected to the server");
  System.out.println("======================================");

  //clients input stream
  InputStream ofs = socket.getInputStream();
  DataInputStream in = new DataInputStream(ofs);
  //store message from client
  String msg = in.readUTF();
  System.out.println("message from client:" + msg);

  if(msg.matches("SEND")){
    //Create new socket
    Socket clt = new Socket("127.0.0.1",7005);
    System.out.println("Connected to 127.0.0.1 and port 4444!");
    OutputStream outsvr = clt.getOutputStream();
    DataOutputStream out = new DataOutputStream(outsvr);
    String file = "totoro.jpg";
    System.out.println("File name:" + file);
    FileInputStream fis = new FileInputStream(file);
    byte[] buffer = new byte[4096];
    //copy file to output
    while (fis.read(buffer)>0) {
      out.write(buffer);
    }
  }

  if(msg.matches("GET")){
    Socket clt = new Socket("127.0.0.1",7005);
    System.out.println("Connected to 127.0.0.1 and port 4444!");
    OutputStream outsvr = clt.getOutputStream();
    DataOutputStream out = new DataOutputStream(outsvr);
  }

  /*
  DataInputStream in = new DataInputStream(socket.getInputStream());
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
  }*/


  System.out.println("Closing connection");
  in.close();
  //out.close();
  svr.close();
  socket.close();
}
}
