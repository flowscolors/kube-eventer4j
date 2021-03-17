package com.kube.eventer4j.service.impl;

import com.kube.eventer4j.bean.EventSink;
import com.kube.eventer4j.bean.EventSource;
import com.kube.eventer4j.service.SinkDriver;
import com.kube.eventer4j.utils.ConstantUtil;
import io.fabric8.kubernetes.api.model.events.v1.Event;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service(ConstantUtil.SINK_KIND_MYSQL)
public class MysqlSinkDriver implements SinkDriver<Object> {
    @Override
    public MySQLPool initConnection(EventSink eventSink) throws Exception {
        try{
            int mysqlPort = Integer.valueOf(eventSink.getSinkIP().split(":",3)[2].split("/",2)[0]);
            String mysqlIP = eventSink.getSinkIP().split("//",2)[1].split(":",2)[0];
            String mysqlDatabase = eventSink.getSinkIP().split(":",3)[2].split("/",2)[1];
            //log.info("{}   {}   {}",mysqlIP,String.valueOf(mysqlPort),mysqlDatabase);

            MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                    .setPort(mysqlPort)
                    .setHost(mysqlIP)
                    .setDatabase(mysqlDatabase)
                    .setUser(eventSink.getSinkParams().get("username"))
                    .setPassword(eventSink.getSinkParams().get("password"));

            PoolOptions poolOptions = new PoolOptions()
                    .setMaxSize(5);
            MySQLPool client = MySQLPool.pool(connectOptions, poolOptions);

            //此处依靠是否为null判断是否连接成功可能会有问题，但不影响最后结果。
            if(client!=null){
                log.info("{}  {}  连接成功 ",eventSink.getSinkKind(),eventSink.getSinkIP());
                return client;
            }
        }catch (Exception e){
            log.warn("{}  {}  连接失败  {}",eventSink.getSinkKind(),eventSink.getSinkIP(),e.getMessage());
        }
        return null;
    }

    @Override
    public void sendEvent(Event event, EventSource eventSource,EventSink eventSink) throws Exception {
        MySQLPool client = (MySQLPool) eventSink.getSinkClient();

        client.preparedQuery("INSERT INTO kube_event4j (cluster_name, event_name,event_namespace," +
                        "event_id,type,reason,message,kind,first_occurrence_time,last_occurrence_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
                .execute(Tuple.of(eventSource.getSourceName(), event.getMetadata().getName(),event.getMetadata().getNamespace(),event.getMetadata().getUid(),
                        event.getType(),event.getReason(),event.getNote(),event.getKind(),event.getDeprecatedFirstTimestamp(),event.getDeprecatedLastTimestamp()), ar -> {
                    if (ar.succeeded()) {
                        //log.info("{} === >> {} 发送Event {} 失败",eventSource.getSourceName(),eventSink.getSinkName(),event.getMetadata().getName());
                    } else {
                        log.warn("Failure: " + ar.cause().getMessage());
                    }
                });
    }
}
