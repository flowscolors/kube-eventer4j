package com.kube.eventer4j.utils;

import com.kube.eventer4j.bean.EventSink;
import com.kube.eventer4j.bean.EventSource;
import com.kube.eventer4j.service.SendEventsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ParseUtil <sourceList, sinkList>{

    @Autowired
    private SendEventsService sendEventsService;

    public static List<EventSource> sourceList = new ArrayList<EventSource>();

    public static List<EventSink> sinkList = new ArrayList<EventSink>();;

    public ParseUtil() {
        log.info("============  Step 1. 进入application.yaml 参数Parse流程   ============");
        initSourceList();
        initSinkList();
    }

    public void initSourceList( ) {
        String initSource = ConstantUtil.getInitSource();
        try {
            if(initSource!=null){
                if(CollectionUtils.isNotEmpty(Arrays.asList(initSource.split(";")))){
                    System.out.println(initSource);
                    int sourceIndex = 0 ;
                    for(String source: initSource.split(";")){
                        Map<String,String> sourceMap = new ConcurrentHashMap<>();
                        sourceMap.put("sourceKind",source.split(":",2)[0].trim());
                        sourceMap.put("sourceName",sourceMap.get("sourceKind")+"-"+sourceIndex++);
                        for(String sourceItem : source.split(":",2)[1].split("&")){
                            sourceMap.put(sourceItem.split("=")[0],sourceItem.split("=")[1]);
                        }

                        //initSource组装成EventSource对象数组
                        EventSource eventSource = new EventSource(sourceMap.get("sourceKind"),sourceMap.get("sourceName"),
                                sourceMap.get("url"),sourceMap.get("token"));
                        sourceList.add(eventSource);
                        System.out.println(eventSource.toString());
                    }
                }else{
                    log.info("initSource解析为空，请检查输入参数");
                }
            }else {
                log.info("initSource输入值为空，请检查输入参数");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initSinkList( ) {
        String initSink = ConstantUtil.getInitSink();
        try {
            if(initSink!=null){
                if(CollectionUtils.isNotEmpty(Arrays.asList(initSink.split(";")))){
                    System.out.println(initSink);
                    int sinkIndex = 0 ;
                    for(String sink: initSink.split(";")){
                        Map<String,String> sinkMap = new ConcurrentHashMap<>();
                        Map<String,String> sinkParamsMap = new ConcurrentHashMap<>();
                        sinkMap.put("sinkKind",sink.split(":",2)[0].trim());
                        sinkMap.put("sinkName",sinkMap.get("sinkKind")+"-"+sinkIndex++);
                        sinkMap.put("url",sink.split(":",2)[1].trim().split("&",2)[0].split("=")[1]);
                        //sinkMap.put("sinkParams",sink.split(":",2)[1].split("&",2)[1].trim());
                        //sinkMap.put("sinkParams",sink.split(":",2)[1].trim());
                        for(String sinkItem : sink.split(":",2)[1].trim().split("&")){
                            sinkParamsMap.put(sinkItem.split("=")[0],sinkItem.split("=")[1]);
                        }

                        //initSInk组装成EventSink对象数组
                        EventSink eventSink = new EventSink(sinkMap.get("sinkKind"),sinkMap.get("sinkName"),
                                sinkMap.get("url"),sinkParamsMap);
                        sinkList.add(eventSink);
                        System.out.println(eventSink);
                    }
                }else {
                    log.info("initSink解析为空，请检查输入参数");
                }
            }else {
                log.info("initSink输入值为空，请检查输入参数");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
