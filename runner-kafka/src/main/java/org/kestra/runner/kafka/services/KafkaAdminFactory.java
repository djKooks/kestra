package org.kestra.runner.kafka.services;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import org.apache.kafka.clients.admin.AdminClient;
import org.kestra.core.metrics.MetricRegistry;
import org.kestra.runner.kafka.KafkaQueueEnabled;
import org.kestra.runner.kafka.configs.ClientConfig;
import org.kestra.runner.kafka.metrics.KafkaClientMetrics;

import java.util.Properties;
import javax.inject.Inject;
import javax.inject.Singleton;

@KafkaQueueEnabled
@Factory
public class KafkaAdminFactory {
    @Bean(preDestroy = "close")
    @Inject
    @Singleton
    AdminClient restHighLevelClient(ClientConfig clientConfig, MetricRegistry metricRegistry) {
        Properties properties = new Properties();
        properties.putAll(clientConfig.getProperties());

        AdminClient client = AdminClient.create(properties);

        metricRegistry.bind(new KafkaClientMetrics(client));

        return client;
    }
}
