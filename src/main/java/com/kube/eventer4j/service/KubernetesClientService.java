package com.kube.eventer4j.service;

import io.fabric8.kubernetes.client.KubernetesClient;

public interface KubernetesClientService {
    public KubernetesClient connKubernetes(String apiServerIP, String token) throws Exception;
}
