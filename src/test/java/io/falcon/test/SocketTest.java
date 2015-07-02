package io.falcon.test;

import org.junit.Test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * SocketTest
 * Created by cozybz@gmail.com on 2015/7/2.
 */
public class SocketTest {
    @Test
    public void startServer() throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);

        Socket socket = serverSocket.accept();

        ObjectOutputStream oo = new ObjectOutputStream(socket.getOutputStream());
        oo.writeLong(0);
        oo.flush();

        ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());

        while (true) {
            Long l = oi.readLong();
            oo.writeLong(++l);
            oo.flush();
            if (l % 50000 == 0) {
                System.out.println(System.currentTimeMillis() + " " + l);
            }

        }
    }

    @Test
    public void startClient() throws Exception {
        Socket socket = new Socket("localhost", 8080);
        ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream oo = new ObjectOutputStream(socket.getOutputStream());
        while (true) {
            Long l = oi.readLong();
            oo.writeLong(l);
            oo.flush();
        }
    }
}
