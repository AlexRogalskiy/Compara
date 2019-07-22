package com.wildbeeslabs.sensiblemetrics.diffy.common.event.monitor;

import com.wildbeeslabs.sensiblemetrics.diffy.common.event.iface.Event;
import com.wildbeeslabs.sensiblemetrics.diffy.common.event.iface.MessageMonitor;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Counts the number of ingested, successful, failed and processed messages
 *
 * @author Marijn van Zelst
 * @since 4.1
 */
public class MessageCountingMonitor<T> implements MessageMonitor<T, Event<T>> {

    private final Counter ingestedCounter;
    private final Counter successCounter;
    private final Counter failureCounter;
    private final Counter processedCounter;
    private final Counter ignoredCounter;

    private MessageCountingMonitor(Counter ingestedCounter, Counter successCounter, Counter failureCounter,
                                   Counter processedCounter, Counter ignoredCounter) {
        this.ingestedCounter = ingestedCounter;
        this.successCounter = successCounter;
        this.failureCounter = failureCounter;
        this.processedCounter = processedCounter;
        this.ignoredCounter = ignoredCounter;
    }

    /**
     * Creates a message counting monitor
     *
     * @param meterNamePrefix The prefix for the meter name that will be created in the given meterRegistry
     * @param meterRegistry   The meter registry used to create and register the meters
     * @return the message counting monitor
     */
    public static MessageCountingMonitor buildMonitor(String meterNamePrefix, MeterRegistry meterRegistry) {
        Counter ingestedCounter = meterRegistry.counter(meterNamePrefix + ".ingestedCounter");
        Counter successCounter = meterRegistry.counter(meterNamePrefix + ".successCounter");
        Counter failureCounter = meterRegistry.counter(meterNamePrefix + ".failureCounter");
        Counter processedCounter = meterRegistry.counter(meterNamePrefix + ".processedCounter");
        Counter ignoredCounter = meterRegistry.counter(meterNamePrefix + ".ignoredCounter");

        return new MessageCountingMonitor(ingestedCounter,
            successCounter,
            failureCounter,
            processedCounter,
            ignoredCounter);
    }

    @Override
    public MonitorCallback onEventIngested(final Event<T> message) {
        ingestedCounter.increment();
        return new MonitorCallback() {
            @Override
            public void reportSuccess() {
                processedCounter.increment();
                successCounter.increment();
            }

            @Override
            public void reportFailure(Throwable cause) {
                processedCounter.increment();
                failureCounter.increment();
            }

            @Override
            public void reportIgnored() {
                ignoredCounter.increment();
            }
        };
    }
}
