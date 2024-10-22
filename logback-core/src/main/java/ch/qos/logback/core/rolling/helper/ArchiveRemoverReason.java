package ch.qos.logback.core.rolling.helper;

public enum ArchiveRemoverReason {
    MAX_HISTORY, // Exceeded max age for an archived file
    TOTAL_SIZE_CAP, // Exceeded total size of all archived files
}
