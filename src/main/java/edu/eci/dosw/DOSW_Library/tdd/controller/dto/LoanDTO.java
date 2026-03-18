package edu.eci.dosw.DOSW_Library.tdd.controller.dto;

public class LoanDTO {
    private String id;
    private String userId;
    private String bookId;
    private String status;

    public LoanDTO() {}

    public LoanDTO(String id, String userId, String bookId, String status) {
        this.id = id;
        this.userId = userId;
        this.bookId = bookId;
        this.status = status;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getBookId() { return bookId; }
    public String getStatus() { return status; }
}
