package io.deviad.ripeti.webapp.infrastructure;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.jmx.JmxMeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MetricsConfig {
  @Bean
  JvmThreadMetrics threadMetrics() {
    return new JvmThreadMetrics();
  }

  @Bean
  @Primary
  CompositeMeterRegistry compositeMeterRegistry() {
    CompositeMeterRegistry registry = new CompositeMeterRegistry();
    registry.add(new JmxMeterRegistry(s -> null, Clock.SYSTEM));
    return registry;
  }

  @Bean
  MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
    return registry -> registry.config().commonTags("application", "Ripeti");
  }

  @Bean
  public TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
  }

  @Bean
  public CountedAspect countedAspect(MeterRegistry registry) {
    return new CountedAspect(registry);
  }
}
