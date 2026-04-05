package dev.rbq.library_management_system.controller;

import dev.rbq.library_management_system.dto.ApiResponse;
import dev.rbq.library_management_system.dto.book.PageResponse;
import dev.rbq.library_management_system.dto.borrow.BorrowRecordResponse;
import dev.rbq.library_management_system.dto.borrow.BorrowRequest;
import dev.rbq.library_management_system.dto.borrow.UpdateBorrowRecordRequest;
import dev.rbq.library_management_system.entity.BorrowRecord.BorrowStatus;
import dev.rbq.library_management_system.security.UserDetailsImpl;
import dev.rbq.library_management_system.service.BorrowRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 借阅记录控制器
 */
@RestController
@RequestMapping("/api/borrow-records")
public class BorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    /**
     * 借阅图书
     * 所有已登录用户都可以访问
     * @param request 借阅请求
     * @return 借阅记录详情
     */
    @PostMapping("/borrow")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BorrowRecordResponse>> borrowBook(@Valid @RequestBody BorrowRequest request) {
        try {
            // 获取当前登录用户的UUID
            String userUuid = getCurrentUserUuid();

            BorrowRecordResponse response = borrowRecordService.borrowBook(userUuid, request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("\u501f\u9605\u6210\u529f", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("\u501f\u9605\u5931\u8d25:" + e.getMessage()));
        }
    }

    /**
     * 归还图书
     * 所有已登录用户都可以访问（只能归还自己借的书）
     * @param recordId 借阅记录ID
     * @return 更新后的借阅记录详情
     */
    @PostMapping("/{recordId}/return")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BorrowRecordResponse>> returnBook(@PathVariable String recordId) {
        try {
            String userUuid = getCurrentUserUuid();
            BorrowRecordResponse response = borrowRecordService.returnBook(recordId, userUuid);
            return ResponseEntity.ok(ApiResponse.success("\u5f52\u8fd8\u6210\u529f", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("\u5f52\u8fd8\u5931\u8d25:" + e.getMessage()));
        }
    }

    /**
     * 查询自己的借阅记录
     * 所有已登录用户都可以访问
     * @param page 页码（从0开始，默认为0）
     * @param size 每页数量（默认为10）
     * @param status 借阅状态（可选）
     * @return 借阅记录分页列表
     */
    @GetMapping("/my-records")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<BorrowRecordResponse>>> getMyBorrowRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) BorrowStatus status) {
        try {
            String userUuid = getCurrentUserUuid();
            PageResponse<BorrowRecordResponse> response = borrowRecordService.getMyBorrowRecords(userUuid, page, size, status);
            return ResponseEntity.ok(ApiResponse.success("Borrowing history retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve borrowing records:" + e.getMessage()));
        }
    }

    /**
     * 获取借阅记录详情
     * 所有已登录用户都可以访问（普通用户只能查看自己的记录，管理员可以查看所有记录）
     * @param recordId 借阅记录ID
     * @return 借阅记录详情
     */
    @GetMapping("/{recordId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BorrowRecordResponse>> getBorrowRecordDetail(@PathVariable String recordId) {
        try {
            String userUuid = getCurrentUserUuid();
            boolean isAdmin = isCurrentUserAdmin();
            BorrowRecordResponse response = borrowRecordService.getBorrowRecordDetail(recordId, userUuid, isAdmin);
            return ResponseEntity.ok(ApiResponse.success("\u6210\u529f\u83b7\u53d6\u501f\u9605\u8bb0\u5f55\u8be6\u60c5", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("\u83b7\u53d6\u501f\u9605\u8bb0\u5f55\u8be6\u60c5\u5931\u8d25:" + e.getMessage()));
        }
    }

    /**
     * 查询所有借阅记录（仅管理员）
     * @param page 页码（从0开始，默认为0）
     * @param size 每页数量（默认为10）
     * @param userUuid 用户UUID（可选，用于筛选）
     * @param status 借阅状态（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 借阅记录分页列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<BorrowRecordResponse>>> getAllBorrowRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String userUuid,
            @RequestParam(required = false) BorrowStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            PageResponse<BorrowRecordResponse> response = borrowRecordService.getAllBorrowRecords(
                    page, size, userUuid, status, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("Borrowing history retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve borrowing records:" + e.getMessage()));
        }
    }

    /**
     * 更新借阅记录（仅管理员）
     * @param recordId 借阅记录ID
     * @param request 更新请求
     * @return 更新后的借阅记录详情
     */
    @PostMapping("/{recordId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BorrowRecordResponse>> updateBorrowRecord(
            @PathVariable String recordId,
            @Valid @RequestBody UpdateBorrowRecordRequest request) {
        try {
            BorrowRecordResponse response = borrowRecordService.updateBorrowRecord(recordId, request);
            return ResponseEntity.ok(ApiResponse.success("Update borrowing records successful", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update borrowing records:" + e.getMessage()));
        }
    }

    /**
     * 查询逾期未还的借阅记录（仅管理员）
     * @param page 页码（从0开始，默认为0）
     * @param size 每页数量（默认为10）
     * @return 逾期借阅记录分页列表
     */
    @GetMapping("/overdue")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<BorrowRecordResponse>>> getOverdueRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            PageResponse<BorrowRecordResponse> response = borrowRecordService.getOverdueRecords(page, size);
            return ResponseEntity.ok(ApiResponse.success("Successfully retrieved overdue records", response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve overdue records:" + e.getMessage()));
        }
    }

    /**
     * 获取当前登录用户的UUID
     * @return 用户UUID
     */
    private String getCurrentUserUuid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getUser().getUuid();
    }

    /**
     * 判断当前用户是否为管理员
     * @return true 如果是管理员，否则 false
     */
    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}
