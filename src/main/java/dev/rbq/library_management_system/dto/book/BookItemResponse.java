package dev.rbq.library_management_system.dto.book;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 图书副本响应 DTO
 */
public class BookItemResponse {

    private Integer itemId;
    private Integer bookId;
    private String bookTitle; // 图书标题
    private String bookAuthor; // 图书作者
    private String barcode;
    private String location;
    private String status;
    private String statusDescription;
    private LocalDate acquisitionDate;
    private BigDecimal acquisitionPrice;
    private String notes;

    // Constructors
    public BookItemResponse() {
    }

    public BookItemResponse(Integer itemId, Integer bookId, String bookTitle, String bookAuthor,
                            String barcode, String location, String status, String statusDescription,
                            LocalDate acquisitionDate, BigDecimal acquisitionPrice, String notes) {
        this.itemId = itemId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.barcode = barcode;
        this.location = location;
        this.status = status;
        this.statusDescription = statusDescription;
        this.acquisitionDate = acquisitionDate;
        this.acquisitionPrice = acquisitionPrice;
        this.notes = notes;
    }

    // Getters and Setters
    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public LocalDate getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(LocalDate acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public BigDecimal getAcquisitionPrice() {
        return acquisitionPrice;
    }

    public void setAcquisitionPrice(BigDecimal acquisitionPrice) {
        this.acquisitionPrice = acquisitionPrice;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "BookItemResponse{" +
                "itemId=" + itemId +
                ", bookId=" + bookId +
                ", bookTitle='" + bookTitle + '\'' +
                ", barcode='" + barcode + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", acquisitionDate=" + acquisitionDate +
                ", acquisitionPrice=" + acquisitionPrice +
                '}';
    }
}
