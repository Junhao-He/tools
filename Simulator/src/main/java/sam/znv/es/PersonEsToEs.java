package sam.znv.es;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

import static sam.znv.utils.EsUtils.getEsTransportClient;

/**
 * auther: LiuJun
 * date: 2020/2/11 13:47.
 */
public class PersonEsToEs {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonEsToEs.class);

    private static final String FROM_CLUSTER_NAME = "face.dct-znv.com-es";
    private static final String FROM_TRANSPORTHOSTS = "10.45.157.112:9300";
    private static final String FROM_INDEX = "person_list_data_n_project_v1_2";
    private static final String FROM_TYPE = "person_list";

    private static final String TO_CLUSTER_NAME = "face.dct-znv.com-es";
    private static final String TO_TRANSPORTHOSTS = "10.45.157.112:9300";
    private static final String TO_INDEX = "person_list_data_n_project_v1_2_30w";
    private static final String TO_TYPE = "person_list";
    private static String scrollid;

    public static void main(String[] args) {

        TransportClient fromClient = getEsTransportClient(FROM_CLUSTER_NAME, FROM_TRANSPORTHOSTS);
        TransportClient toClient = getEsTransportClient(TO_CLUSTER_NAME, TO_TRANSPORTHOSTS);
        while (true){
            SearchHits result = search(fromClient, FROM_INDEX, FROM_TYPE);
            System.out.println("拉取条数："+result.getHits().length);
            write(result, toClient);
            if(result.getHits().length == 0){
                break;
            }
        }
    }

    /**
     * from中滚动取出数据，一次1万条
     * @param client
     * @param index
     * @param type
     * @return
     */
    public static SearchHits search(TransportClient client, String index, String type){
        SearchResponse response = null;
        if(scrollid == null){
            response = client.prepareSearch(index)
                    .setTypes(type)
                    .setSize(10000)
                    .setScroll(TimeValue.timeValueMinutes(1))
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .execute().actionGet();
        }else{
            response = client.prepareSearchScroll(scrollid).setScroll(TimeValue.timeValueMinutes(8)).execute().actionGet();
        }
        scrollid = response.getScrollId();
        return response.getHits();
    }

    /**
     * 向es写入数据
     * @param result 数据
     * @param esTClient to client
     */
    private static void write(SearchHits result, TransportClient esTClient) {
        BulkRequestBuilder bulkRequest = esTClient.prepareBulk();
        for (SearchHit data : result) {
            bulkRequest.add(esTClient.prepareIndex(TO_INDEX, TO_TYPE).setSource(data.getSource()));
        }
        bulkRequest.execute().actionGet();
    }
}

