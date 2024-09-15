package com.mawus.core.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ClientAction implements Serializable {

    @Serial
    private static final long serialVersionUID = -9088496692601343536L;

    private final Command command;

    private final String action;

    private final LocalDateTime createTs = LocalDateTime.now();

    public ClientAction(Command command, String action) {
        this.command = command;
        this.action = action;
    }

    public Command getCommand() {
        return command;
    }

    public String getAction() {
        return action;
    }

    public LocalDateTime getCreateTs() {
        return createTs;
    }
}
