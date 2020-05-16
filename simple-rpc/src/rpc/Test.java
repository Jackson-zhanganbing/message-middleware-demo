package rpc;

import rpc.server.RpcServer;
import rpc.provider.ProviderService;
import rpc.provider.ProviderServiceImpl;

import java.net.InetSocketAddress;

public class Test {
    public static void main(String[] args) {

        Test test = new Test();
        test.startServerAndProvider(test);

        RpcConsumerProxy<ProviderService> importer = new RpcConsumerProxy<>();
        ProviderService echo = importer.remoteCall(ProviderServiceImpl.class,new InetSocketAddress("localhost",8083));
        System.out.println(echo.method("hello?"));
    }

    private void startServerAndProvider(Test test){
        new Thread(test::runServer).start();
    }

    private void runServer(){
        try{
            //服务发布到远程，就像一个socket服务端，等待连接
            RpcServer.handleCall("localhost",8083);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
