package com.steelhouse.autoconfigure.metrics;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("shmetrics")
public class SHMetricsProperties {

  private String name = "defaultapp";

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
