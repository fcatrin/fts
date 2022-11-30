package fts.core;

import java.io.IOException;

public class NotEnoughStorageException extends IOException {
    private final long required;
    private final long available;

    public NotEnoughStorageException(long required, long available) {
        this.required = required;
        this.available = available;
    }

    public long getRequired() {
        return required;
    }

    public long getAvailable() {
        return available;
    }
}
