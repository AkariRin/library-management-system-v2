package dev.rbq.library_management_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 借阅记录实体类
 */
@Entity
@Table(name = "borrow_records")
public class BorrowRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "record_id", length = 36, nullable = false)
    private String recordId;

    @NotNull(message = "\u6210\u6237UUID\u4e0d\u80fd\u4e3a\u7a7a")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid", nullable = false, foreignKey = @ForeignKey(name = "fk_borrow_records_user_uuid"))
    private User user;

    @NotNull(message = "\u4e66\u7c4d\u526f\u672cID\u4e0d\u80fd\u4e3a\u7a7a")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_borrow_records_item_id"))
    private BookItem bookItem;

    @NotNull(message = "\u501f\u9605\u65e5\u671f\u4e0d\u80fd\u4e3a\u7a7a")
    @Column(name = "borrow_date", nullable = false, columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private LocalDateTime borrowDate;

    @NotNull(message = "\u5230\u61c2\u65e5\u671f\u4e0d\u80fd\u4e3a\u7a7a")
    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @NotNull(message = "\u501f\u9605\u72b6\u6001\u4e0d\u80fd\u4e3a\u7a7a")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BorrowStatus status = BorrowStatus.Checked_Out;

    // Constructors
    public BorrowRecord() {
    }

    public BorrowRecord(String recordId, User user, BookItem bookItem, LocalDateTime borrowDate,
                        LocalDateTime dueDate, LocalDateTime returnDate, BorrowStatus status) {
        this.recordId = recordId;
        this.user = user;
        this.bookItem = bookItem;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    // Getters and Setters
    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BookItem getBookItem() {
        return bookItem;
    }

    public void setBookItem(BookItem bookItem) {
        this.bookItem = bookItem;
    }

    public LocalDateTime getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDateTime borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public BorrowStatus getStatus() {
        return status;
    }

    public void setStatus(BorrowStatus status) {
        this.status = status;
    }

    /**
     * 借阅状态枚举
     */
    public enum BorrowStatus {
        Checked_Out("Checked Out", "Checked Out"),
        Returned("Returned", "Returned");

        private final String value;
        private final String description;

        BorrowStatus(String value, String description) {
            this.value = value;
            this.description = description;
        }

        public String getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }
    }
}
