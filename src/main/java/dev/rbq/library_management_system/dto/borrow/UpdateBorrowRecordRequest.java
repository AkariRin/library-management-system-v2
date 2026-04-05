package dev.rbq.library_management_system.dto.borrow;

import dev.rbq.library_management_system.entity.BorrowRecord.BorrowStatus;

import java.time.LocalDateTime;

/**
 * 更新借阅记录请求 DTO（仅管理员使用）
 */
public class UpdateBorrowRecordRequest {

    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private BorrowStatus status;

    // Constructors
    public UpdateBorrowRecordRequest() {
    }

    public UpdateBorrowRecordRequest(LocalDateTime dueDate, LocalDateTime returnDate, BorrowStatus status) {
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    // Getters and Setters
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
}
