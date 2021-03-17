package com.kube.eventer4j.service;

import com.kube.eventer4j.bean.EventSink;
import com.kube.eventer4j.bean.EventSource;
import io.fabric8.kubernetes.api.model.events.v1.Event;

import java.awt.*;
import java.util.List;

public interface SinkDriver<T> {

    public Object initConnection(EventSink eventSink) throws Exception ;

    public void sendEvent(Event event,EventSource eventSource,EventSink eventSink) throws Exception;

}
