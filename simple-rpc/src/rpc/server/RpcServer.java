package rpc.server;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
     * 接收TCP数据，根据接口，反射调用接口实现类provider的方法
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
                //读出接口名
                String interfaceName = input.readUTF();
                //找到接口所在相对地址
                String path = interfaceName.substring(0, interfaceName.lastIndexOf("."));

                Class<?> clazz = Class.forName(interfaceName);

                //找到接口所在文件下所有文件
                File[] files = new File(clazz.getResource("/").getFile() + path.replaceAll("\\.", "\\/")).listFiles();
                for (File file : files) {
                    //找文件，而不是文件夹
                    if (!file.isDirectory()) {
                        //path不一定是provider实现类的，这里需要实现类的path
                        String implPath = path + "." + file.getName().replaceAll(".class", "");
                        //获取实现类的class
                        Class<?> implClass = Class.forName(implPath);
                        //不是接口，并且实现类的接口名和传过来的接口名一致
                        if (!implClass.isInterface() && interfaceName.equals(implClass.getInterfaces()[0].getName())) {
                            //获取消费者传过来的接口方法
                            String methodName = input.readUTF();
                            //获取消费者传过来的接口参数类型
                            Class<?>[] parameterTypes = (Class<?>[]) input.readObject();

                            Method method = implClass.getMethod(methodName, parameterTypes);

                            Object[] arguments = (Object[]) input.readObject();
                            //移花接木，调用实现类的方法
                            Object result = method.invoke(implClass.newInstance(), arguments);

                            output = new ObjectOutputStream(socket.getOutputStream());
                            output.writeObject(result);
                        }
                    }
                }


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
