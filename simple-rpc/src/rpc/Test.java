package rpc;

import rpc.provider.ProviderService;
import rpc.server.RpcServer;

import java.net.InetSocketAddress;

public class Test {

    static {
        startServer();
    }

    public static void main(String[] args) throws Throwable {

        RpcConsumer consumer = new RpcConsumer();

        Object[] objArgs = {"调用远程方法--"};

        Object providerService = consumer.remoteCall(ProviderService.class, new InetSocketAddress("localhost", 8083), objArgs);

        System.out.println(providerService.toString());

    }

    private static void startServer() {
        new Thread(new Test()::runServer).start();
    }

    private void runServer() {
        try {
            //远程TCP服务打开，provide发布到远程
            RpcServer.startServer("localhost", 8083);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
