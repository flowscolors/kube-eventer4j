package com.kube.eventer4j.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class ConstantUtil {

    //support source kinds
    public final static String SOURCE_KIND_K8S = "kubernetes";

    //support sink kinds
   public final static String SINK_KIND_MYSQL = "mysql";

   public final static String SINK_KIND_REDIS = "redis";

   public final static String SINK_KIND_KAFKA = "kafka";

   public final static String SINK_KIND_MINIO = "minio";

   //source sink get value
   public static String initSource ;

   public static String initSink;

   public static String getInitSource() {
       return initSource;
   }

    @Value("${source}")
    public void setInitSource(String source) {
        ConstantUtil.initSource = source;
    }

    public static String getInitSink() {
        return initSink;
    }

    @Value("${sink}")
    public void setInitSink(String sink) {
        ConstantUtil.initSink = sink;
    }
}
