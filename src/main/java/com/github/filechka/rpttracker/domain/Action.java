package com.github.filechka.rpttracker.domain;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Data
public class Action {
    @NotNull
    private String id;
    @NotNull
    private String name;
    private String description;

    public Action() {
        this.id = UUID.randomUUID().toString();
    }

    public Action(String name) {
        this();
        this.name = name;
    }

    public Action(String name, String description) {
        this(name);
        this.description = description;
    }
}
