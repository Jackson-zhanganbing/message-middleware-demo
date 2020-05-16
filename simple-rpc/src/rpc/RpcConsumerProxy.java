package rpc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 服务消费者本地调用动态代理，处理连接、反序列化之类工作
 *
 * @author zab
 * @date 2020-05-16 16:50
 */
public class RpcConsumerProxy<S> {
    public S remoteCall(final Class<?> serviceClass, final InetSocketAddress addr) {

        return (S) Proxy.newProxyInstance(serviceClass.getClassLoader(),
                new Class<?>[]{serviceClass.getInterfaces()[0]},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        Socket socket = null;
                        ObjectOutputStream output = null;
                        ObjectInputStream input = null;
                        try {
                            socket = new Socket();
                            socket.connect(addr);
                            output = new ObjectOutputStream(socket.getOutputStream());

                            output.writeUTF(serviceClass.getName());

                            output.writeUTF(method.getName());

                            output.writeObject(method.getParameterTypes());

                            output.writeObject(args);

                            input = new ObjectInputStream(socket.getInputStream());
                            return input.readObject();
                        } finally {
                            if (socket != null) socket.close();
                            if (output != null) output.close();
                            if (input != null) input.close();
                        }
                    }
                }
        );

    }
}
