package io.falcon.test;

import org.junit.Test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * SimpleFalcon
 * Created by quyao on 2015/6/27.
 */
public class SimpleFalcon {
    @Test
    public void runServer() {
        try {
            startServer(new FooService(), 8080);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSimpleFalcon() {


        long t1 = System.currentTimeMillis();
        int i = 0;

        while (true) {
            IFooService fooService = bindServer(IFooService.class, "localhost", 8080);
            fooService.say("dawn");
            i++;
            if (i % 10000 == 0) {
                System.out.println(i);
                System.out.println(System.currentTimeMillis() - t1);
                t1 = System.currentTimeMillis();
            }
        }
    }

    public static void startServer(final Object service, final int port) throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);

        while (true) {
            final Socket socket = serverSocket.accept();

            Runnable handler = new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        ObjectInputStream input = null;
                        ObjectOutputStream output = null;
                        try {
                            input = new ObjectInputStream(socket.getInputStream());
                            output = new ObjectOutputStream(socket.getOutputStream());
                            String methodName = input.readUTF();
                            Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
                            Object[] arguments = (Object[]) input.readObject();
                            Method method = service.getClass().getMethod(methodName, parameterTypes);
                            try {
                                Object result = method.invoke(service, arguments);
                                output.writeObject(result);
                            } catch (Exception e) {
                                output.writeObject(e);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                //if (input != null)
//                                input.close();
//                            if (output != null)
//                                output.flush();
                                //output.close();
                                //socket.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            };

            new Thread(handler).start();
        }

    }

    @SuppressWarnings("unchecked")
    public static <T> T bindServer(Class<T> interfaceClazz, String host, int port) {
        return (T) Proxy.newProxyInstance(interfaceClazz.getClassLoader(), new Class[]{interfaceClazz}, new SimpleFalconProxy(host, port));
    }

}

class SimpleFalconProxy implements InvocationHandler {
    private String host;
    private int port;

    public SimpleFalconProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Socket socket = SocketPool.getSocket(host, port);
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        output.writeUTF(method.getName());
        output.writeObject(method.getParameterTypes());
        output.writeObject(args);
        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        Object result = input.readObject();
        if (result instanceof Throwable) {
            throw (Throwable) result;
        }
        SocketPool.freeSocket(host, port, socket);
        return result;
    }
}


interface IFooService {
    String say(String name);
}

class FooService implements IFooService {

    @Override
    public String say(String name) {
        return "Hello, World. I'm " + name + "!";
    }
}