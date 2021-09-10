package com.example.newrelictesta01;

import com.newrelic.telemetry.Attributes;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.util.NamedThreadFactory;
import com.newrelic.telemetry.micrometer.NewRelicRegistry;
import com.newrelic.telemetry.micrometer.NewRelicRegistryConfig;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @see <a href="https://newrelic.com/blog/how-to-relic/how-to-monitor-spring-boot-applications-using-micrometer-metrics">Monitoring Spring Boot Applications using Micrometer Metrics in New Relic</a>
 * @see <a href="https://github.com/newrelic-experimental/spring-petclinic-microservices/blob/becd1b54b04118f52e5ad7349f10a72cbb9a6b10/spring-petclinic-api-gateway/src/main/java/org/springframework/samples/petclinic/newrelic/MicrometerConfig.java">MicrometerConfig.java</a>
 */
@Configuration
@AutoConfigureBefore({
    CompositeMeterRegistryAutoConfiguration.class,
    SimpleMetricsExportAutoConfiguration.class
})
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnClass(NewRelicRegistry.class)
@PropertySource("classpath:new-relic.yml")
public class NewRelicMetricsExportAutoConfiguration {

  @Value("${account-id}")
  private String accountId;
  
  @Value("${api-key}")
  private String apiKey;

  @Value("${service-name}")
  private String serviceName;

  @Bean
  public NewRelicRegistryConfig newRelicConfig() {
    return new NewRelicRegistryConfig() {
      @Override
      public String get(String key) {
        return null;
      }

      @Override
      public String apiKey() {
        return apiKey;
      }

      @Override
      public Duration step() {
        return Duration.ofSeconds(5);
      }

      @Override
      public String serviceName() {
        return serviceName;
      }

    };
  }

  @Bean
  public NewRelicRegistry newRelicMeterRegistry(NewRelicRegistryConfig config)
      throws UnknownHostException {
    NewRelicRegistry newRelicRegistry =
        NewRelicRegistry.builder(config)
            .commonAttributes(
                new Attributes()
                    .put("host", InetAddress.getLocalHost().getHostName()))
            .build();
    newRelicRegistry.config().meterFilter(MeterFilter.ignoreTags("plz_ignore_me"));
    newRelicRegistry.config().meterFilter(MeterFilter.denyNameStartsWith("jvm.threads"));
    newRelicRegistry.start(new NamedThreadFactory("newrelic.micrometer.registry"));
    return newRelicRegistry;
  }
}