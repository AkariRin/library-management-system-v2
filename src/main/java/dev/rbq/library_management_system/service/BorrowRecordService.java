package dev.rbq.library_management_system.service;

import dev.rbq.library_management_system.dto.book.PageResponse;
import dev.rbq.library_management_system.dto.borrow.BorrowRecordResponse;
import dev.rbq.library_management_system.dto.borrow.BorrowRequest;
import dev.rbq.library_management_system.dto.borrow.UpdateBorrowRecordRequest;
import dev.rbq.library_management_system.entity.BookItem;
import dev.rbq.library_management_system.entity.BookItem.BookItemStatus;
import dev.rbq.library_management_system.entity.BorrowRecord;
import dev.rbq.library_management_system.entity.BorrowRecord.BorrowStatus;
import dev.rbq.library_management_system.entity.User;
import dev.rbq.library_management_system.repository.BookItemRepository;
import dev.rbq.library_management_system.repository.BorrowRecordRepository;
import dev.rbq.library_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 借阅记录服务
 */
@Service
public class BorrowRecordService {

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    @Autowired
    private BookItemRepository bookItemRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 借阅图书（已登录用户）
     * @param userUuid 当前登录用户的UUID
     * @param request 借阅请求
     * @return 借阅记录响应
     */
    @Transactional
    public BorrowRecordResponse borrowBook(String userUuid, BorrowRequest request) {
        // 验证用户是否存在
        User user = userRepository.findById(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist"));

        // 验证图书副本是否存在
        BookItem bookItem = bookItemRepository.findById(request.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("The book copy does not exist."));

        // 检查图书副本状态是否可借
        if (bookItem.getStatus() != BookItemStatus.Available) {
            throw new IllegalStateException("This copy of the book is currently unavailable for borrowing. Status:" + bookItem.getStatus().getDescription());
        }

        // 检查是否已存在未归还的借阅记录
        if (borrowRecordRepository.existsByBookItemIdAndStatusCheckedOut(bookItem.getItemId())) {
            throw new IllegalStateException("This copy of the book is currently checked out");
        }

        // 创建借阅记录
        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setRecordId(UUID.randomUUID().toString());
        borrowRecord.setUser(user);
        borrowRecord.setBookItem(bookItem);
        borrowRecord.setBorrowDate(LocalDateTime.now());
        borrowRecord.setDueDate(LocalDateTime.now().plusDays(30)); // 默认借阅期限30天
        borrowRecord.setStatus(BorrowStatus.Checked_Out);

        // 更新图书副本状态为已借出
        bookItem.setStatus(BookItemStatus.Checked_Out);
        bookItemRepository.save(bookItem);

        // 保存借阅记录
        BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);

        return convertToResponse(savedRecord);
    }

    /**
     * 查询自己的借阅记录
     * @param userUuid 当前登录用户的UUID
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @param status 借阅状态（可选）
     * @return 借阅记录分页列表
     */
    public PageResponse<BorrowRecordResponse> getMyBorrowRecords(String userUuid, int page, int size, BorrowStatus status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("borrowDate").descending());

        Page<BorrowRecord> recordPage;
        if (status != null) {
            recordPage = borrowRecordRepository.findByUserUuidAndStatus(userUuid, status, pageable);
        } else {
            recordPage = borrowRecordRepository.findByUserUuid(userUuid, pageable);
        }

        List<BorrowRecordResponse> content = recordPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                recordPage.getNumber(),
                recordPage.getSize(),
                recordPage.getTotalElements(),
                recordPage.getTotalPages(),
                recordPage.isFirst(),
                recordPage.isLast(),
                recordPage.isEmpty()
        );
    }

    /**
     * 查询所有借阅记录（仅管理员）
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @param userUuid 用户UUID（可选，用于筛选）
     * @param status 借阅状态（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 借阅记录分页列表
     */
    public PageResponse<BorrowRecordResponse> getAllBorrowRecords(
            int page, int size, String userUuid, BorrowStatus status,
            LocalDateTime startDate, LocalDateTime endDate) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("borrowDate").descending());

        Page<BorrowRecord> recordPage;
        if (userUuid != null || status != null || startDate != null || endDate != null) {
            recordPage = borrowRecordRepository.findByMultipleConditions(userUuid, status, startDate, endDate, pageable);
        } else {
            recordPage = borrowRecordRepository.findAll(pageable);
        }

        List<BorrowRecordResponse> content = recordPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                recordPage.getNumber(),
                recordPage.getSize(),
                recordPage.getTotalElements(),
                recordPage.getTotalPages(),
                recordPage.isFirst(),
                recordPage.isLast(),
                recordPage.isEmpty()
        );
    }

    /**
     * 获取借阅记录详情
     * @param recordId 借阅记录ID
     * @param currentUserUuid 当前用户UUID
     * @param isAdmin 是否为管理员
     * @return 借阅记录响应
     */
    public BorrowRecordResponse getBorrowRecordDetail(String recordId, String currentUserUuid, boolean isAdmin) {
        BorrowRecord borrowRecord = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("No borrowing records found"));

        // 如果不是管理员，只能查看自己的借阅记录
        if (!isAdmin && !borrowRecord.getUser().getUuid().equals(currentUserUuid)) {
            throw new IllegalArgumentException("You do not have permission to view this borrowing record");
        }

        return convertToResponse(borrowRecord);
    }

    /**
     * 更新借阅记录（仅管理员）
     * @param recordId 借阅记录ID
     * @param request 更新请求
     * @return 更新后的借阅记录响应
     */
    @Transactional
    public BorrowRecordResponse updateBorrowRecord(String recordId, UpdateBorrowRecordRequest request) {
        BorrowRecord borrowRecord = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("No borrowing records found"));

        // 更新应还日期
        if (request.getDueDate() != null) {
            borrowRecord.setDueDate(request.getDueDate());
        }

        // 更新实际归还日期
        if (request.getReturnDate() != null) {
            borrowRecord.setReturnDate(request.getReturnDate());
        }

        // 更新状态
        if (request.getStatus() != null) {
            BorrowStatus oldStatus = borrowRecord.getStatus();
            BorrowStatus newStatus = request.getStatus();

            borrowRecord.setStatus(newStatus);

            // 如果状态从已借出变为已归还，更新图书副本状态
            if (oldStatus == BorrowStatus.Checked_Out && newStatus == BorrowStatus.Returned) {
                BookItem bookItem = borrowRecord.getBookItem();
                bookItem.setStatus(BookItemStatus.Available);
                bookItemRepository.save(bookItem);

                // 自动设置归还日期（如果没有设置）
                if (borrowRecord.getReturnDate() == null) {
                    borrowRecord.setReturnDate(LocalDateTime.now());
                }
            }
        }

        BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);
        return convertToResponse(savedRecord);
    }

    /**
     * 归还图书（用户操作）
     * @param recordId 借阅记录ID
     * @param currentUserUuid 当前用户UUID
     * @return 更新后的借阅记录响应
     */
    @Transactional
    public BorrowRecordResponse returnBook(String recordId, String currentUserUuid) {
        BorrowRecord borrowRecord = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("No borrowing records found"));

        // 验证是否为本人的借阅记录
        if (!borrowRecord.getUser().getUuid().equals(currentUserUuid)) {
            throw new IllegalArgumentException("You do not have permission to perform this operation on this borrowing record");
        }

        // 验证状态是否为已借出
        if (borrowRecord.getStatus() != BorrowStatus.Checked_Out) {
            throw new IllegalStateException("The status of this borrowing record is:" + borrowRecord.getStatus().getDescription() + "，无法归还");
        }

        // 更新借阅记录状态
        borrowRecord.setStatus(BorrowStatus.Returned);
        borrowRecord.setReturnDate(LocalDateTime.now());

        // 更新图书副本状态
        BookItem bookItem = borrowRecord.getBookItem();
        bookItem.setStatus(BookItemStatus.Available);
        bookItemRepository.save(bookItem);

        BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);
        return convertToResponse(savedRecord);
    }

    /**
     * 查询逾期未还的借阅记录（仅管理员）
     * @param page 页码
     * @param size 每页数量
     * @return 逾期借阅记录分页列表
     */
    public PageResponse<BorrowRecordResponse> getOverdueRecords(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BorrowRecord> recordPage = borrowRecordRepository.findOverdueRecords(LocalDateTime.now(), pageable);

        List<BorrowRecordResponse> content = recordPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                recordPage.getNumber(),
                recordPage.getSize(),
                recordPage.getTotalElements(),
                recordPage.getTotalPages(),
                recordPage.isFirst(),
                recordPage.isLast(),
                recordPage.isEmpty()
        );
    }

    /**
     * 将借阅记录实体转换为响应DTO
     * @param borrowRecord 借阅记录实体
     * @return 借阅记录响应DTO
     */
    private BorrowRecordResponse convertToResponse(BorrowRecord borrowRecord) {
        BorrowRecordResponse response = new BorrowRecordResponse();
        response.setRecordId(borrowRecord.getRecordId());
        response.setUserUuid(borrowRecord.getUser().getUuid());
        response.setUsername(borrowRecord.getUser().getUsername());
        response.setUserName(borrowRecord.getUser().getName());
        response.setItemId(borrowRecord.getBookItem().getItemId());
        response.setBarcode(borrowRecord.getBookItem().getBarcode());
        response.setBookId(borrowRecord.getBookItem().getBook().getBookId());
        response.setBookTitle(borrowRecord.getBookItem().getBook().getTitle());
        response.setBookAuthor(borrowRecord.getBookItem().getBook().getAuthor());
        response.setBorrowDate(borrowRecord.getBorrowDate());
        response.setDueDate(borrowRecord.getDueDate());
        response.setReturnDate(borrowRecord.getReturnDate());
        response.setStatus(borrowRecord.getStatus());
        response.setStatusDescription(borrowRecord.getStatus().getDescription());

        // 判断是否逾期
        boolean isOverdue = borrowRecord.getStatus() == BorrowStatus.Checked_Out
                && borrowRecord.getDueDate().isBefore(LocalDateTime.now());
        response.setIsOverdue(isOverdue);

        return response;
    }
}

