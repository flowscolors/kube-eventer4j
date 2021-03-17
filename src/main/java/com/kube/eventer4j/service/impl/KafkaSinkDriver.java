package com.kube.eventer4j.service.impl;

import com.alibaba.fastjson.JSON;
import com.kube.eventer4j.bean.EventSink;
import com.kube.eventer4j.bean.EventSource;
import com.kube.eventer4j.service.SinkDriver;
import com.kube.eventer4j.utils.ConstantUtil;
import io.fabric8.kubernetes.api.model.events.v1.Event;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service(ConstantUtil.SINK_KIND_KAFKA)
public class KafkaSinkDriver implements SinkDriver<Object> {
    @Override
    public KafkaProducer initConnection(EventSink eventSink) throws Exception {
        try{
            Properties properties = new Properties();
            properties.put("bootstrap.servers",eventSink.getSinkParams().get("brokers"));
            properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            KafkaProducer<String,String> producer  = new KafkaProducer<String, String>(properties);

            //此处依靠是否为null判断是否连接成功可能会有问题，但不影响最后结果。
            if(producer != null){
                log.info("{}  {}  连接成功 ",eventSink.getSinkKind(),eventSink.getSinkIP());
                return producer;
            }
        }catch (Exception e){
            log.warn("{}  {}  连接失败  {}",eventSink.getSinkKind(),eventSink.getSinkIP(),e.getMessage());
        }
        return null;
    }

    @Override
    public void sendEvent(Event event, EventSource eventSource,EventSink eventSink) throws Exception {
        KafkaProducer<String,String> producer = (KafkaProducer<String, String>) eventSink.getSinkClient();
        Map<String,Object> dataMap = new HashMap<>(2);
        dataMap.put("cluster",eventSource.getSourceName());
        dataMap.put("event",event);
        producer.send(new ProducerRecord<>(eventSink.getSinkParams().get("eventstopic"), JSON.toJSONString(dataMap)), (recordMetadata, e) -> {
            if (Objects.nonNull(e)) {
                log.info("send error: {}", e);
            }
        });
    }
}
