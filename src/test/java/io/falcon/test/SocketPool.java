package io.falcon.test;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

import java.net.Socket;

/**
 * SocketPool
 * Created by quyao on 2015/6/28.
 */
public class SocketPool {
    private static KeyedObjectPool<String, Socket> socketPool;

    static {
        GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
        config.setMaxIdlePerKey(1000);
        config.setMaxTotalPerKey(1000);
        socketPool = new GenericKeyedObjectPool<>(new SocketPollFactory(), config);
    }

    public static Socket getSocket(String host, int port) {
        Socket socket = null;
        try {
            socket = socketPool.borrowObject(host + ":" + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return socket;
    }

    public static void freeSocket(String host, int port, Socket socket) {
        try {
            socketPool.returnObject(host + ":" + port, socket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class SocketPollFactory extends BaseKeyedPooledObjectFactory<String, Socket> {

    @Override
    public Socket create(String key) throws Exception {
        String host = key.split(":")[0];
        Integer port = Integer.valueOf(key.split(":")[1]);
        System.out.println("创建Socket : " + key);
        return new Socket(host, port);
    }

    @Override
    public PooledObject<Socket> wrap(Socket value) {
        return new DefaultPooledObject<Socket>(value);
    }

    @Override
    public void destroyObject(String key, PooledObject<Socket> p) throws Exception {
        p.getObject().close();
    }

    @Override
    public void passivateObject(String key, PooledObject<Socket> p) throws Exception {
        p.getObject().getOutputStream().flush();
    }
}