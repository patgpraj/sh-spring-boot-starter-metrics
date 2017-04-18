package com.steelhouse.autoconfigure.metrics;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.scheduling.annotation.Async;

import java.time.Duration;
import java.time.Instant;

@Aspect
public class SHMetricsAspect {

  private final GaugeService gaugeService;
  private final SHMetricsProperties shMetricsProperties;

  public SHMetricsAspect(GaugeService gaugeService, SHMetricsProperties shMetricsProperties) {
    this.gaugeService = gaugeService;
    this.shMetricsProperties = shMetricsProperties;
  }

  @Pointcut("execution(public * *(..))")
  private void anyPublicMethod() {
  }

  @Around("anyPublicMethod() && @annotation(latencyMetric)")
  public Object calculateLatencyMetric(ProceedingJoinPoint pjp, LatencyMetric latencyMetric) {
    return _calculateLatencyMetric(pjp);
  }

  @Around("anyPublicMethod() && @annotation(async)")
  public Object calculateAsyncLatencyMetric(ProceedingJoinPoint pjp, Async async) {
    return _calculateLatencyMetric(pjp);
  }

  private Object _calculateLatencyMetric(ProceedingJoinPoint pjp) {
    Object result = null;
    String LATENCY_METRIC = "";
    try {
      Instant stime = Instant.now();
      result = pjp.proceed();
      Instant etime = Instant.now();
      Duration timeElapsed = Duration.between(stime, etime);

      final String methodName = pjp.getSignature().getName();
      LATENCY_METRIC = shMetricsProperties.getName() + ".latency." + methodName;

      gaugeService.submit(LATENCY_METRIC, timeElapsed.toMillis());
    } catch (Throwable ex) {
      result = null;
    }
    return result;
  }

}
