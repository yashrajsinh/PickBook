package com.yashrajsinh.pickbook.Model;

public class BookDetail {

    private String bookName, authorName, bestOfBook, category, imagePath;
    private String userEmail, userId;
    private String requesterEmail;
    private boolean status;
    private boolean requested;

    public BookDetail() {
    }

    public BookDetail(String bookName, String authorName, String bestOfBook, String category, String imagePath, String userEmail,
                      String userId, boolean status, boolean requested) {
        this.bookName = bookName;
        this.authorName = authorName;
        this.bestOfBook = bestOfBook;
        this.category = category;
        this.imagePath = imagePath;
        this.userEmail = userEmail;
        this.userId = userId;
        this.status = status;
        this.requested = requested;
    }

    public String getBookName() {
        return bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getBestOfBook() {
        return bestOfBook;
    }

    public String getCategory() {
        return category;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public boolean isStatus() {
        return status;
    }

    public boolean isRequested() {
        return requested;
    }

}
