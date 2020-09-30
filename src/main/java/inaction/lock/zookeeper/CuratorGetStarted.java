package inaction.lock.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.nio.charset.StandardCharsets;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/29 16:07
 */
public class CuratorGetStarted {
    private static final String ADDRESS = "ubuntu.wsl:2181";
    public static void main(String[] args) throws Exception {
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(ADDRESS, retry);
        // 开启客户端
        client.start();


        // 递归创建永久目录,以及存入数据
        client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                .forPath("/samples/hello", "world".getBytes(StandardCharsets.UTF_8));

        // 获取节点数据
        byte[] data = client.getData().forPath("/samples/hello");
        assert "world".equals(new String(data));

        // 查看状态
        client.setData().forPath("/samples/hello", "zookeeper".getBytes());

        // 删除节点数据
        client.delete().forPath("/samples/hello");

        client.close();
    }
}
