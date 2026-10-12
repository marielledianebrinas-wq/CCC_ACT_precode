package model;

public class Department {
    private final String id;
    private String name;
    private boolean required;
    private boolean active = true;

    public Department(String id, String name, boolean required) {
        this.id = id;
        this.name = name;
        this.required = required;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
