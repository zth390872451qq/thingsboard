package org.thingsboard.server.dao.util;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.entity.EntityService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractTenantBufferedRateExecutor<T extends AsyncTask, F extends ListenableFuture<V>, V> extends AbstractBufferedRateExecutor<T, F, V> {

    @Autowired
    private EntityService entityService;
    private Map<TenantId, String> tenantNamesCache = new HashMap<>();

    private boolean printTenantNames;

    public AbstractTenantBufferedRateExecutor(int queueLimit, int concurrencyLimit, long maxWaitTime, int dispatcherThreads, int callbackThreads, long pollMs,
                                        boolean perTenantLimitsEnabled, String perTenantLimitsConfiguration, boolean printTenantNames) {
        super(queueLimit, concurrencyLimit, maxWaitTime, dispatcherThreads, callbackThreads, pollMs, perTenantLimitsEnabled, perTenantLimitsConfiguration);
        this.printTenantNames = printTenantNames;
    }

    public void printStats() {
        log.info("Permits queueSize [{}] totalAdded [{}] totalLaunched [{}] totalReleased [{}] totalFailed [{}] totalExpired [{}] totalRejected [{}] " +
                        "totalRateLimited [{}] totalRateLimitedTenants [{}] currBuffer [{}] ",
                getQueueSize(),
                totalAdded.getAndSet(0), totalLaunched.getAndSet(0), totalReleased.getAndSet(0),
                totalFailed.getAndSet(0), totalExpired.getAndSet(0), totalRejected.getAndSet(0),
                totalRateLimited.getAndSet(0), rateLimitedTenants.size(), concurrencyLevel.get());

        rateLimitedTenants.forEach(((tenantId, counter) -> {
            if (printTenantNames) {
                String name = tenantNamesCache.computeIfAbsent(tenantId, tId -> {
                    try {
                        return entityService.fetchEntityNameAsync(TenantId.SYS_TENANT_ID, tenantId).get();
                    } catch (Exception e) {
                        log.error("[{}] Failed to get tenant name", tenantId, e);
                        return "N/A";
                    }
                });
                log.info("[{}][{}] Rate limited requests: {}", tenantId, name, counter);
            } else {
                log.info("[{}] Rate limited requests: {}", tenantId, counter);
            }
        }));
        rateLimitedTenants.clear();
    }


}
