package com.kube.eventer4j.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.kube.eventer4j.bean.EventSink;
import com.kube.eventer4j.bean.EventSource;
import com.kube.eventer4j.service.SinkDriver;
import com.kube.eventer4j.utils.ConstantUtil;
import io.fabric8.kubernetes.api.model.events.v1.Event;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;

@Slf4j
@Service(ConstantUtil.SINK_KIND_MINIO)
public class MinioSinkDriver implements SinkDriver<Object> {
    @Override
    public MinioClient initConnection(EventSink eventSink) throws Exception {
        try{
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint(eventSink.getSinkIP())
                            .credentials(eventSink.getSinkParams().get("accessKey"), eventSink.getSinkParams().get("secretKey"))
                            .build();
            if(minioClient.listBuckets()!=null) {
                log.info("{}  {}  连接成功 ",eventSink.getSinkKind(),eventSink.getSinkIP());
                return minioClient;
            }
        }catch (Exception e){
            log.warn("{}  {}  连接失败  {}",eventSink.getSinkKind(),eventSink.getSinkIP(),e.getMessage());
        }
        return null;
    }

    @Override
    public void sendEvent(Event event, EventSource eventSource, EventSink eventSink) throws Exception {
         MinioClient minioClient = (MinioClient) eventSink.getSinkClient();
         minioClient.putObject(
                PutObjectArgs.builder().bucket(eventSink.getSinkParams().get("bucket")).object(eventSource.getSourceName()+"/"+
                        "event/"+event.getMetadata().getNamespace()+"/"+event.getMetadata().getName()+".yaml").
                        stream(new ByteArrayInputStream(JSONArray.toJSON(event).toString().getBytes()),
                                JSONArray.toJSON(event).toString().getBytes().length,-1)
                        .build());
    }
}
