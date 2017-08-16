package com.vending.back.machine.error;

/**
 * vyemialyanchyk on 2/8/2017.
 */
public class IntegrationException extends RuntimeException {
    public enum ExtSystem {INTEGRATION}

    private ExtSystem system = ExtSystem.INTEGRATION;

    public ExtSystem getSystem() {
        return system;
    }

    public IntegrationException(String message) {
        super(message);
    }

    public IntegrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public IntegrationException(ExtSystem system, String message) {
        this(system, message, null);
    }

    public IntegrationException(ExtSystem system, String message, Throwable cause) {
        super(system.name() + " failure: " + message, cause);
        this.system = system;
    }

}
