package rpc.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * RPC服务端，将provider的服务发布成远程服务，供消费者调用
 *
 * @author zab
 * @date 2020-05-16 17:55
 */
public class RpcServer {

    /**
     * 自定义线程池，自定义线程工厂和拒绝策略
     */
    static Executor executor = new ThreadPoolExecutor(10, 10, 0,
            TimeUnit.SECONDS, new LinkedBlockingDeque<>(),
            r -> {
                Thread t = new Thread(r);
                t.setName("myThread");
                return t;
            },
            (r, executor) -> System.out.println(r.toString() + " is discard")
    );

    /**
     * 处理RPC请求调用
     */
    public static void startServer(String hostName, int port) throws Exception {
        ServerSocket server = new ServerSocket();

        server.bind(new InetSocketAddress(hostName, port));
        try {
            while (true) {
                executor.execute(new RpcHandleTask(server.accept()));
            }
        } finally {
            server.close();
        }
    }

    /**
     * 接收TCP数据，反射调用provider的方法
    */
    private static class RpcHandleTask implements Runnable {
        Socket socket = null;

        public RpcHandleTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            ObjectInputStream input = null;
            ObjectOutputStream output = null;
            try {

                input = new ObjectInputStream(socket.getInputStream());

                String interfaceName = input.readUTF();

                Class<?> clazz = Class.forName(interfaceName);

                String methodName = input.readUTF();

                Class<?>[] parameterTypes = (Class<?>[]) input.readObject();

                Method method = clazz.getMethod(methodName, parameterTypes);

                Object[] arguments = (Object[]) input.readObject();
                Object result = method.invoke(clazz.newInstance(), arguments);

                output = new ObjectOutputStream(socket.getOutputStream());
                output.writeObject(result);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (input != null) {
                    try {
                        input.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}
