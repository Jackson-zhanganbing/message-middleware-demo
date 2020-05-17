package rpc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 服务消费者远程调用服务提供方，处理连接、反序列化之类工作
 *
 * @author zab
 * @date 2020-05-16 16:50
 */
public class RpcConsumer {

    public Object remoteCall(final Class clazz, final InetSocketAddress addr,Object[] args) throws Throwable{
        Socket socket = null;
        ObjectOutputStream output = null;
        ObjectInputStream input = null;
        try {
            socket = new Socket();
            socket.connect(addr);
            output = new ObjectOutputStream(socket.getOutputStream());

            output.writeUTF(clazz.getName());

            Method[] methods = clazz.getMethods();
            for (Method method : methods) {

                output.writeUTF(method.getName());

                output.writeObject(method.getParameterTypes());

                output.writeObject(args);
            }

            input = new ObjectInputStream(socket.getInputStream());
            return input.readObject();
        } finally {
            if (socket != null) socket.close();
            if (output != null) output.close();
            if (input != null) input.close();
        }
    }
}
