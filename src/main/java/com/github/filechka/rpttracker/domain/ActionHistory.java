package com.github.filechka.rpttracker.domain;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ActionHistory {
    @NotNull
    private String id;
    @NotNull
    private String actionId;
    private LocalDateTime created;

    public ActionHistory() {
        this.id = UUID.randomUUID().toString();
        this.created = LocalDateTime.now();
    }

    public ActionHistory(String actionId) {
        this();
        this.actionId = actionId;
    }
}
