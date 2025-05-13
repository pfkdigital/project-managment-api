package org.example.projectmanagementapi.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.projectmanagementapi.exception.RateLimitationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class RateLimitAspect {
  public static final String ERROR_MESSAGE =
      "To many requests at endpoint %s from IP address %s, please try again after %d milliseconds. ";
  private final ConcurrentHashMap<String, List<Long>> requests = new ConcurrentHashMap<>();

  @Value("${app.rate.limit}")
  private int rateLimit;

  @Value("${app.rate.duration}")
  private int duration;

  @Before("@annotation(org.example.projectmanagementapi.annotation.WithRateLimitProtection)")
  public void rateLimit() {
    final ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    assert attributes != null;
    final String ipAddress = attributes.getRequest().getRemoteAddr();
    final long currentTime = System.currentTimeMillis();

    requests.putIfAbsent(ipAddress, new ArrayList<>());
    requests.get(ipAddress).add(currentTime);
    cleanUpRequests(currentTime);

    if (requests.get(ipAddress).size() > rateLimit) {
      throw new RateLimitationException(
          String.format(
              attributes.getRequest().getRequestURI(), ERROR_MESSAGE, ipAddress, duration));
    }
  }

  private void cleanUpRequests(final long currentTime) {
    requests
        .values()
        .forEach(
            list -> {
              list.removeIf(t -> timeToOld(currentTime, t));
            });
  }

  private boolean timeToOld(final long currentTime, final long timeToCheck) {
    return (currentTime - timeToCheck) > duration;
  }
}
