package br.com.pmg.hc.infrastructure;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class KeepAliveScheduler {

    private static final Logger LOGGER = Logger.getLogger(KeepAliveScheduler.class);

    @ConfigProperty(name = "app.keepalive.enabled", defaultValue = "true")
    boolean enabled;

    @ConfigProperty(name = "app.keepalive.url")
    java.util.Optional<String> keepAliveUrlConfig;

    @ConfigProperty(name = "app.keepalive.timeout-seconds", defaultValue = "5")
    int timeoutSeconds;

    private HttpClient client;
    private URI targetUri;

    @PostConstruct
    void init() {
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(timeoutSeconds))
                .build();

        String defaultPort = System.getenv().getOrDefault("PORT", "8080");
        String fallbackUrl = "http://127.0.0.1:" + defaultPort + "/hello";
        String url = keepAliveUrlConfig.orElse(fallbackUrl);
        targetUri = URI.create(url);

        LOGGER.infof("Keep-alive scheduler configured to ping %s (enabled=%s)", targetUri, enabled);
    }

    @Scheduled(every = "${app.keepalive.every:5m}", delayed = "${app.keepalive.initial-delay:1m}")
    void ping() {
        if (!enabled) {
            return;
        }
        HttpRequest request = HttpRequest.newBuilder(targetUri)
                .GET()
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenAccept(response -> {
                    if (response.statusCode() >= 400) {
                        LOGGER.warnf("Keep-alive ping returned status %d", response.statusCode());
                    }
                })
                .exceptionally(throwable -> {
                    LOGGER.warn("Keep-alive ping failed", throwable);
                    return null;
                });
    }
}
