package io.falcon.bootstrap;

import io.falcon.service.FalconService;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * Created by cozybz@gmail.com on 2015/4/15.
 */
public class FalconBootStrap {
    public static void startService(FalconService service, int port) {
        //TODO 使用netty
        Socket socket = null;
        try {
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            String methodName = input.readUTF();
            Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
            Object[] arguments = (Object[])input.readObject();
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            Method method = service.getClass().getMethod(methodName, parameterTypes);
            Object result = method.invoke(service, arguments);
            output.writeObject(result);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
