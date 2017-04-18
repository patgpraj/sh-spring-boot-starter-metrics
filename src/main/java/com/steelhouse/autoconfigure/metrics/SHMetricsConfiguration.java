package com.steelhouse.autoconfigure.metrics;

import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import io.prometheus.client.spring.boot.SpringBootMetricsCollector;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.Collection;

@Configuration
@ConditionalOnProperty(prefix = "shmetrics", value = "enable")
@EnableConfigurationProperties(SHMetricsProperties.class)
public class SHMetricsConfiguration {

  private final GaugeService gaugeService;
  private final SHMetricsProperties shMetricsProperties;

  @Inject
  public SHMetricsConfiguration(GaugeService gaugeService, SHMetricsProperties shMetricsProperties) {
    this.gaugeService = gaugeService;
    this.shMetricsProperties = shMetricsProperties;
  }

  @Bean
  public ServletRegistrationBean servletRegistrationBean() {
    DefaultExports.initialize();
    return new ServletRegistrationBean(new MetricsServlet(), "/prometheus");
  }

  @Bean
  public SpringBootMetricsCollector springBootMetricsCollector(Collection<PublicMetrics> publicMetrics) {
    SpringBootMetricsCollector springBootMetricsCollector = new SpringBootMetricsCollector(publicMetrics);
    springBootMetricsCollector.register();
    return springBootMetricsCollector;
  }

  @Bean
  public SHMetricsAspect sHMetricsAspect() {
    return new SHMetricsAspect(gaugeService, shMetricsProperties);
  }

}