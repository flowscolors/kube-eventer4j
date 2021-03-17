package com.kube.eventer4j.utils;

import com.alibaba.fastjson.JSONArray;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kube.eventer4j.bean.EventSink;
import com.kube.eventer4j.bean.EventSource;
import com.kube.eventer4j.service.SendEventsService;
import io.fabric8.kubernetes.api.model.events.v1.Event;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.minio.PutObjectArgs;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Component
public class StartSinks implements ApplicationRunner {

    @Autowired
    private SendEventsService sendEventsService;

    ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("sink-pool-%d").build();

    //Common Thread Pool
    ExecutorService pool = new ThreadPoolExecutor(10, 200,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());


    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            //检查List<EventSource> sourceList，获得真正能拿到输出的K8S clients
            List<EventSource>  realSourceList = sendEventsService.checkSourceClient(ParseUtil.sourceList);
            log.info("============  Step 2-1. Kubernetes realSourceList不为空,size为: {} ============" ,realSourceList.size());

            //执行逻辑，开启线程池，每个sink开启一个线程，每个线程开启多个informer。【需要保证source sink正确非空】
            List<EventSink> realSinkList = sendEventsService.checkSinkClient(ParseUtil.sinkList);
            log.info("============  Step 2-2. Kubernetes realSinkList不为空,size为: {} ============" , realSinkList.size());

            if(CollectionUtils.isNotEmpty(realSourceList)){
                if(CollectionUtils.isNotEmpty(realSinkList)){
                    //每一个sink创建对应的sources信息
                    for (EventSource eventSource : realSourceList){
                        log.info(eventSource.toString());
                        pool.execute(new Runnable() {
                            @SneakyThrows
                            @Override
                            public void run() {
                                log.info(Thread.currentThread().getName()+"  "+eventSource.getSourceName()+"  进入");
                                KubernetesClient client = eventSource.getSourceClinet();
                                SharedInformerFactory sharedInformerFactory = client.informers();
                                SharedInformer<Event> eventSharedInformer = sharedInformerFactory.sharedIndexInformerFor(Event.class,12*3600*1000);

                                eventSharedInformer.addEventHandler(
                                        new ResourceEventHandler<Event>() {
                                            @Override
                                            public void onAdd(Event event) {
                                                try {
                                                    sendEventsService.fromEventToSink(event,eventSource,realSinkList);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onUpdate(Event event, Event t1) {
                                                try {
                                                    sendEventsService.fromEventToSink(event,eventSource,realSinkList);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onDelete(Event event, boolean b) {

                                            }
                                        }
                                );
                                sharedInformerFactory.startAllRegisteredInformers();
                                log.info(eventSource.getSourceName()+"-"+eventSource.getSourceIP()+" Event Informer 初始化成功");
                            }
                        });
                    }
                    //gracefully shutdown
                    //pool.shutdown();
                }else{
                    log.info("Step 2. Kubernetes sinkList为空，请检查输入值");
                }
            }else{
                log.info("Step 2. Kubernetes sourceList为空，请检查输入值");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
