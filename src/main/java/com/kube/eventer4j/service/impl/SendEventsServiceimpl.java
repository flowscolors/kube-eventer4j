package com.kube.eventer4j.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.kube.eventer4j.bean.EventSink;
import com.kube.eventer4j.bean.EventSource;
import com.kube.eventer4j.service.KubernetesClientService;
import com.kube.eventer4j.service.SendEventsService;
import com.kube.eventer4j.service.SinkDriver;
import com.kube.eventer4j.utils.ConstantUtil;
import io.fabric8.kubernetes.api.model.events.v1.Event;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SendEventsServiceimpl implements SendEventsService {

    @Autowired
    private KubernetesClientService kubernetesClientService;

    @Autowired
    @Qualifier(ConstantUtil.SINK_KIND_REDIS)
    private SinkDriver<Object> redisSinkDriver;

    @Autowired
    @Qualifier(ConstantUtil.SINK_KIND_MYSQL)
    private SinkDriver<Object> mysqlSinkDriver;

    @Autowired
    @Qualifier(ConstantUtil.SINK_KIND_MINIO)
    private SinkDriver<Object> minioSinkDriver;

    @Autowired
    @Qualifier(ConstantUtil.SINK_KIND_KAFKA)
    private SinkDriver<Object> kafkaSinkDriver;

    @Override
    public List<EventSource> checkSourceClient(List<EventSource> eventSourceList) throws Exception {
        List<EventSource> realSourceList = new ArrayList<EventSource>();
        if(CollectionUtils.isNotEmpty(eventSourceList)){
             for(EventSource eventSource:eventSourceList){
                 KubernetesClient kubernetesClient = kubernetesClientService.connKubernetes(eventSource.getSourceIP(),eventSource.getSourceToken());
                 if(kubernetesClient!=null){
                     eventSource.setSourceClinet(kubernetesClient);
                     realSourceList.add(eventSource);
                 }
             }
            return  realSourceList;
        }else{
            return  realSourceList;
        }
    }

    @Override
    public List<EventSink> checkSinkClient(List<EventSink> eventSinkList) throws Exception {
        List<EventSink> realSinkList = new ArrayList<EventSink>();
        if(CollectionUtils.isNotEmpty(eventSinkList)){
            for(EventSink eventSink:eventSinkList){
                Object object = null;
                //?????????Sink???????????????????????????????????????????????????
                switch (eventSink.getSinkKind()) {
                    case ConstantUtil.SINK_KIND_REDIS:
                        object = redisSinkDriver.initConnection(eventSink);
                        break;
                    case ConstantUtil.SINK_KIND_MYSQL:
                        object = mysqlSinkDriver.initConnection(eventSink);
                        break;
                    case ConstantUtil.SINK_KIND_MINIO:
                        object = minioSinkDriver.initConnection(eventSink);
                        break;
                    case ConstantUtil.SINK_KIND_KAFKA:
                        object = kafkaSinkDriver.initConnection(eventSink);
                        break;
                    default:
                        throw new Exception("????????????SINK??????????????????");
                }
                if(object!=null){
                    eventSink.setSinkClient(object);
                    realSinkList.add(eventSink);
                }
            }
            return  realSinkList;
        }else{
            return  realSinkList;
        }
    }

    @Override
    public void fromEventToSink(Event event, EventSource eventSource,List<EventSink> eventSinkList) throws Exception {
        try{
            //??????????????????sink?????????sink?????????????????????sink?????????????????????
            for(EventSink eventSink:eventSinkList){
                try{
                    switch (eventSink.getSinkKind()) {
                        case ConstantUtil.SINK_KIND_REDIS:
                            redisSinkDriver.sendEvent(event,eventSource,eventSink);
                            break;
                        case ConstantUtil.SINK_KIND_MYSQL:
                            mysqlSinkDriver.sendEvent(event,eventSource,eventSink);
                            break;
                        case ConstantUtil.SINK_KIND_MINIO:
                            minioSinkDriver.sendEvent(event,eventSource,eventSink);
                            break;
                        case ConstantUtil.SINK_KIND_KAFKA:
                            kafkaSinkDriver.sendEvent(event,eventSource,eventSink);
                            break;
                        default:
                            throw new Exception("????????????SINK??????????????????");
                    }
                }catch (Exception e){
                    log.warn("{}  === >> {} ??????Event {}??????. {}",eventSource.getSourceName(),eventSink.getSinkName(),event.getMetadata().getName(),e.getMessage());
                }
            }
        } catch (Exception e){
            log.error("??????sink?????????????????????",e);
        }
    }
}
