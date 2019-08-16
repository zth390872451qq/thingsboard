package org.thingsboard.server.dao.sql;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thingsboard.server.dao.util.AbstractTenantBufferedRateExecutor;
import org.thingsboard.server.dao.util.AsyncTaskContext;
import org.thingsboard.server.dao.util.SqlAnyDao;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

@Component
@Slf4j
@SqlAnyDao
public class SqlBufferedRateExecutor extends AbstractTenantBufferedRateExecutor<SqlStatementTask, ListenableFuture<SqlStatementResult>, SqlStatementResult> {

    protected ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

    public SqlBufferedRateExecutor(
            @Value("${sql.query.buffer_size}") int queueLimit,
            @Value("${sql.query.concurrent_limit}") int concurrencyLimit,
            @Value("${sql.query.permit_max_wait_time}") long maxWaitTime,
            @Value("${sql.query.dispatcher_threads:2}") int dispatcherThreads,
            @Value("${sql.query.callback_threads:4}") int callbackThreads,
            @Value("${sql.query.poll_ms:50}") long pollMs,
            @Value("${sql.query.tenant_rate_limits.enabled}") boolean tenantRateLimitsEnabled,
            @Value("${sql.query.tenant_rate_limits.configuration}") String tenantRateLimitsConfiguration,
            @Value("${sql.query.tenant_rate_limits.print_tenant_names}") boolean printTenantNames) {
        super(queueLimit, concurrencyLimit, maxWaitTime, dispatcherThreads, callbackThreads, pollMs, tenantRateLimitsEnabled, tenantRateLimitsConfiguration, printTenantNames);
    }

    @Override
    protected SettableFuture<SqlStatementResult> create() {
        return SettableFuture.create();
    }

    @Override
    protected ListenableFuture<SqlStatementResult> wrap(SqlStatementTask task, SettableFuture<SqlStatementResult> future) {
        return future;
    }

    @Override
    protected ListenableFuture<SqlStatementResult> execute(AsyncTaskContext<SqlStatementTask, SqlStatementResult> taskCtx) {
        try {
            return taskCtx.getTask().getTask().call();
        } catch (Exception e) {
            throw new ExecutionException(e);
        }
    }
}
