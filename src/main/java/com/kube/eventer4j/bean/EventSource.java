package com.kube.eventer4j.bean;

import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.Data;

@Data
public class EventSource {
    private String sourceKind;
    private String sourceName;
    private String sourceIP;
    private String sourceToken;
    private KubernetesClient sourceClinet;

    public EventSource () {

    }

    public EventSource (String sourceKind,String sourceName,String sourceIP,String sourceToken) {
        this.sourceKind = sourceKind;
        this.sourceName = sourceName;
        this.sourceIP = sourceIP;
        this.sourceToken = sourceToken;
    }
}
