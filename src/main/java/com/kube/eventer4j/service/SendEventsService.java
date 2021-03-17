package com.kube.eventer4j.service;

import com.kube.eventer4j.bean.EventSink;
import com.kube.eventer4j.bean.EventSource;
import io.fabric8.kubernetes.api.model.events.v1.Event;

import java.util.List;

public interface SendEventsService {

    public List<EventSource> checkSourceClient(List<EventSource> eventSourceList) throws Exception;

    public List<EventSink> checkSinkClient(List<EventSink> eventSinkList) throws Exception;

    public void fromEventToSink(Event event,EventSource eventSource,List<EventSink> eventSinkList) throws Exception;
}


