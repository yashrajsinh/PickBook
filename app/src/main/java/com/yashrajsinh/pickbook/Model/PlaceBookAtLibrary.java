package com.yashrajsinh.pickbook.Model;

public class PlaceBookAtLibrary {

    private String libaryName, bookName, currentUserEmail, requesterUserEmail;
    private  String bookDetailDocId;
    private int otp;
    private boolean approved;

    public PlaceBookAtLibrary(String libaryName, String bookName, String currentUserEmail, String requesterUserEmail, String bookDetailDocId, int otp, boolean approved) {
        this.libaryName = libaryName;
        this.bookName = bookName;
        this.currentUserEmail = currentUserEmail;
        this.requesterUserEmail = requesterUserEmail;
        this.bookDetailDocId = bookDetailDocId;
        this.otp = otp;
        this.approved = approved;
    }

    public String getLibaryName() {
        return libaryName;
    }

    public String getBookName() {
        return bookName;
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public String getRequesterUserEmail() {
        return requesterUserEmail;
    }

    public String getBookDetailDocId() {
        return bookDetailDocId;
    }

    public int getOtp() {
        return otp;
    }

    public boolean isApproved() {
        return approved;
    }
}
