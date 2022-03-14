package com.github.filechka.rpttracker.domain;

public class ActionBuilder {

    private static ActionBuilder instance = new ActionBuilder();
    private String id = "";
    private String name = "";
    private String description = "";

    private ActionBuilder(){}

    public static ActionBuilder create() {
        return instance;
    }

    public ActionBuilder withName(String name) {
        this.name = name;
        return instance;
    }

    public ActionBuilder withDescription(String description) {
        this.description = description;
        return instance;
    }

    public ActionBuilder withId(String id) {
        this.id = id;
        return instance;
    }

    public Action build() {
        Action result;
        if (!description.isEmpty()) result = new Action(name, description);
        else result = new Action(name);

        //TODO check is it necessary
        id = "";
        name = "";
        description = "";

        return result;
    }
}
