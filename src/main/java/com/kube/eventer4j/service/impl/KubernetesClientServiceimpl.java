package com.kube.eventer4j.service.impl;

import com.kube.eventer4j.service.KubernetesClientService;
import io.fabric8.kubernetes.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;

@Slf4j
@Service
public class KubernetesClientServiceimpl implements KubernetesClientService {
    @Override
    public KubernetesClient connKubernetes(String apiServerIP, String token) throws Exception {
        try{
            Config config = new ConfigBuilder().withRequestTimeout(12000).
                    withTrustCerts(true).withOauthToken(token).
                    withMasterUrl(apiServerIP).build();
            KubernetesClient kubernetesClient = new DefaultKubernetesClient(config);
            if(kubernetesClient.namespaces().list().getKind()!=null) {
                return kubernetesClient;
            }
        }catch (Exception e){
            log.warn(apiServerIP+"  Kubernetes Client 连接失败");
            e.printStackTrace();
        }
        return null;
    }
}
