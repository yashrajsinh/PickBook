package com.yashrajsinh.pickbook.Model;

public class PlacedBookRequest {

    private String bookName, authorName, category, bestOfBook, requesterEmail;
    private String ownerEmail;
    private String bookImg;
    private boolean status;
    private boolean requested;

    public PlacedBookRequest() {}

    public PlacedBookRequest(String bookName, String authorName, String category, String bestOfBook, String requesterEmail, String bookImg, String ownerEmail, boolean status, boolean requested) {
        this.bookName = bookName;
        this.authorName = authorName;
        this.category = category;
        this.bestOfBook = bestOfBook;
        this.requesterEmail = requesterEmail;
        this.bookImg = bookImg;
        this.status = status;
        this.ownerEmail = ownerEmail;
        this.requested = requested;
    }

    public String getBookName() {
        return bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getCategory() {
        return category;
    }

    public String getBestOfBook() {
        return bestOfBook;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public String getBookImg() {
        return bookImg;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public boolean isStatus() {
        return status;
    }

    public boolean isRequested() {
        return requested;
    }
}
