package sam.znv.utils;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * ES连接工具类
 * @author 章云
 * @date 2019/7/16 14:23
 */
public class EsUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(EsUtils.class);

    public static TransportClient getEsTransportClient(String clusterName, String... transportHosts) {
        TransportClient client = null;
        try {
            Settings settings = Settings.builder().put("cluster.name", clusterName).build();
            TransportAddress[] addresses = new TransportAddress[transportHosts.length];
            int i = 0;
            for (String host : transportHosts) {
                host = host.replaceAll("http://", "");
                String[] inet = host.split(":");
                addresses[i++] = new InetSocketTransportAddress(InetAddress.getByName(inet[0]), 9300);
            }
            client = new PreBuiltTransportClient(settings).addTransportAddresses(addresses);
        } catch (Throwable e) {
            LOGGER.error("ES连接异常", e);
            System.exit(1);
        }
        return client;
    }

    public static void main(String[] args) {
        getEsTransportClient("lv216.dct-znv.com-es", "10.45.154.216");
    }

}
