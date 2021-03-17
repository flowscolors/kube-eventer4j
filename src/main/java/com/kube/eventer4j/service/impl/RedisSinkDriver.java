package com.kube.eventer4j.service.impl;

import com.kube.eventer4j.bean.EventSink;
import com.kube.eventer4j.bean.EventSource;
import com.kube.eventer4j.service.SinkDriver;
import com.kube.eventer4j.utils.ConstantUtil;
import io.fabric8.kubernetes.api.model.events.v1.Event;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service(ConstantUtil.SINK_KIND_REDIS)
public class RedisSinkDriver implements SinkDriver<Object> {
    @Override
    public RedisCommands initConnection(EventSink eventSink) throws Exception {
        try{
            RedisClient redisClient = null;
            String redisIP = eventSink.getSinkIP().split("//",2)[1].split(":",2)[0];
            int redisPort = Integer.valueOf(eventSink.getSinkIP().split(":",3)[2]);

            if(eventSink.getSinkParams().get("requirepass")!=null){
                RedisURI redisUri = RedisURI.builder()
                        .withHost(redisIP)
                        .withPort(redisPort)
                        .withPassword(eventSink.getSinkParams().get("requirepass"))
                        .withTimeout(Duration.of(20, ChronoUnit.SECONDS))
                        .build();
                redisClient = RedisClient.create(redisUri);
            }else {
                RedisURI redisUri = RedisURI.builder()
                        .withHost(redisIP)
                        .withPort(redisPort)
                        .withTimeout(Duration.of(20, ChronoUnit.SECONDS))
                        .build();
                redisClient = RedisClient.create(redisUri);
            }
            StatefulRedisConnection<String, String> connection = redisClient.connect();     // <3> 创建线程安全的连接
            RedisCommands<String, String> redisCommands = connection.sync();                // <4> 创建同步命令
            if (redisCommands.ping()!=null){
                log.info("{}  {}  连接成功 ",eventSink.getSinkKind(),eventSink.getSinkIP());
                return redisCommands;
            }
        }catch (Exception e){
            log.warn("{}  {}  连接失败  {}",eventSink.getSinkKind(),eventSink.getSinkIP(),e.getMessage());
        }
        return  null;
    }

    @Override
    public void sendEvent(Event event, EventSource eventSource, EventSink eventSink) throws Exception {
        RedisCommands<String, String> redisCommands = (RedisCommands<String, String>) eventSink.getSinkClient();
        redisCommands.set(eventSink.getSinkName()+"_"+event.getMetadata().getNamespace()+"_"
                +event.getMetadata().getName(), String.valueOf(event));
    }
}
