package ch.qos.logback.core;

import ch.qos.logback.core.rolling.helper.ArchiveRemoverReason;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import com.google.common.collect.ImmutableMap;
import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.core.MetricName;

public class LogbackMetrics {
    public static Counter getDeletedLogFilesCounter(ArchiveRemoverReason reason, FileNamePattern fileNamePattern) {
        return Metrics.newCounter(
                new MetricName(
                        LogbackMetrics.class,
                        "log-files-deleted",
                        ImmutableMap.of("reason", reason.name(), "fileNamePattern", fileNamePattern.getPattern())
                )
        );
    }
    public static Histogram getDeletedLogFileSizeHistogram(ArchiveRemoverReason reason, FileNamePattern fileNamePattern) {
        return Metrics.newHistogram(
                new MetricName(
                        LogbackMetrics.class,
                        "deleted-log-file-size",
                        ImmutableMap.of("reason", reason.name(), "fileNamePattern", fileNamePattern.getPattern())
                ));
    }
}
