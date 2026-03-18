package edu.eci.dosw.DOSW_Library.tdd.controller.dto;

public class UserDTO {
    private String id;
    private String name;

    public UserDTO() {}

    public UserDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
}
