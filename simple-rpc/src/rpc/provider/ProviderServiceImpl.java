package rpc.provider;

/**
 * 服务提供方 实现
 *
 * @author zab
 * @date 2020-05-16 17:52
 */
public class ProviderServiceImpl implements ProviderService {
    @Override
    public String hello(String str) {
        return str == null ? "hello consumer." : str + "---> hello consumer. I can do something else for you！";
    }
}
