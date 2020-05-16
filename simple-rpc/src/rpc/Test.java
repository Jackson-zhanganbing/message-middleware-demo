package rpc;

import rpc.server.RpcServer;
import rpc.provider.ProviderService;
import rpc.provider.ProviderServiceImpl;

import java.net.InetSocketAddress;

public class Test {

    static {
        startServer();
    }

    public static void main(String[] args) {

        RpcConsumerProxy<ProviderService> consumer = new RpcConsumerProxy<>();

        ProviderService providerService = consumer.remoteCall(ProviderServiceImpl.class,new InetSocketAddress("localhost",8083));

        System.out.println(providerService.testMethod("调用远程方法--"));

    }

    private static void startServer(){
        new Thread(new Test()::runServer).start();
    }

    private void runServer(){
        try{
            //远程TCP服务打开，provide发布到远程
            RpcServer.startServer("localhost",8083);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
