package io.falcon.test;

import org.junit.Test;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SimpleFalcon
 * Created by cozybz@gmail.com on 2015/6/27.
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
        Runnable r = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    IFooService fooService = bindServer(IFooService.class, "localhost", 8080);
                    fooService.say("dawn");
                }
            }
        };

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 2000; i++)
            threads.add(new Thread(r));

        for (Thread thread : threads)
            thread.start();

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void startServer(final Object service, final int port) throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);

        final AtomicInteger sum = new AtomicInteger();

        while (true) {
            final Socket socket = serverSocket.accept();

            Runnable handler = new Runnable() {
                @Override
                public void run() {

                    ObjectInputStream input = null;
                    ObjectOutputStream output = null;

                    try {
                        while (true) {
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
                            output.flush();

                            if (sum.getAndDecrement() % 1000 == 0) {
                                System.out.println(System.currentTimeMillis() + " " + sum.get());
                            }
                        }
                    } catch (Exception e) {
                        if (e instanceof EOFException) {
                            try {
                                output.close();
                                input.close();
                                socket.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }

                        } else {
                            e.printStackTrace();
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
        //Socket socket = new Socket(host, port);
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

        output.writeUTF(method.getName());
        output.writeObject(method.getParameterTypes());
        output.writeObject(args);
        output.flush();

        Object result = input.readObject();
        if (result instanceof Throwable) {
            throw (Throwable) result;
        }
        SocketPool.freeSocket(host, port, socket);

//        output.close();
//        input.close();
//        socket.close();

        return result;
    }
}


interface IFooService {
    String say(String name);
}

class FooService implements IFooService {

    @Override
    public String say(String name) {

        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "Hello, World. I'm " + name + "!";
    }
}