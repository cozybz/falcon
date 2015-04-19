package io.falcon.bootstrap;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * Created by cozybz@gmail.com on 2015/4/17.
 */
public class FalconProxy implements InvocationHandler {


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //TODO 使用netty
        Socket socket = null;

        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        String methodName = method.getName();
        Class[] paramType = method.getParameterTypes();

        //传递方法名，参数类型，参数
        out.writeUTF(methodName);
        out.writeObject(paramType);
        out.writeObject(args);

        //获取返回对象
        Object result = in.readObject();

        return result;
    }
}
