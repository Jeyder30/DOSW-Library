package edu.eci.dosw.DOSW_Library.tdd.controller.dto;

public class BookDTO {
    private String id;
    private String title;
    private String author;

    public BookDTO() {}

    public BookDTO(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
}
