package dev.rbq.library_management_system.service;

import dev.rbq.library_management_system.dto.book.BookItemRequest;
import dev.rbq.library_management_system.dto.book.BookItemResponse;
import dev.rbq.library_management_system.dto.book.PageResponse;
import dev.rbq.library_management_system.entity.Book;
import dev.rbq.library_management_system.entity.BookItem;
import dev.rbq.library_management_system.repository.BookItemRepository;
import dev.rbq.library_management_system.repository.BookRepository;
import dev.rbq.library_management_system.repository.BorrowRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 图书副本服务
 */
@Service
public class BookItemService {

    @Autowired
    private BookItemRepository bookItemRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    /**
     * 获取图书副本详情
     * @param itemId 副本ID
     * @return 图书副本详情
     */
    public BookItemResponse getBookItemDetail(Integer itemId) {
        BookItem bookItem = bookItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("The book copy does not exist"));
        return convertToResponse(bookItem);
    }

    /**
     * 根据图书ID分页查询副本列表
     * @param bookId 图书ID
     * @param page 页码（从0开始）
     * @param size 每页数量
     * @param status 副本状态（可选）
     * @param sortBy 排序字段（默认为itemId）
     * @param sortDirection 排序方向（asc/desc，默认为desc）
     * @return 图书副本分页列表
     */
    public PageResponse<BookItemResponse> getBookItemsByBookId(
            Integer bookId, int page, int size, String status,
            String sortBy, String sortDirection) {

        // 验证图书是否存在
        if (!bookRepository.existsById(bookId)) {
            throw new IllegalArgumentException("The book does not exist");
        }

        // 创建排序对象
        Sort sort = sortDirection != null && sortDirection.equalsIgnoreCase("asc")
                ? Sort.by(sortBy != null ? sortBy : "itemId").ascending()
                : Sort.by(sortBy != null ? sortBy : "itemId").descending();

        // 创建分页对象
        Pageable pageable = PageRequest.of(page, size, sort);

        // 执行查询
        Page<BookItem> itemPage;
        if (status != null && !status.isEmpty()) {
            try {
                BookItem.BookItemStatus itemStatus = parseStatus(status);
                itemPage = bookItemRepository.findByBookIdAndStatus(bookId, itemStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid copy status:" + status);
            }
        } else {
            itemPage = bookItemRepository.findByBookId(bookId, pageable);
        }

        // 转换为响应对象
        List<BookItemResponse> content = itemPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                itemPage.getNumber(),
                itemPage.getSize(),
                itemPage.getTotalElements(),
                itemPage.getTotalPages(),
                itemPage.isFirst(),
                itemPage.isLast(),
                itemPage.isEmpty()
        );
    }

    /**
     * 添加图书副本
     * @param request 图书副本请求
     * @return 添加后的图书副本详情
     */
    @Transactional
    public BookItemResponse addBookItem(BookItemRequest request) {
        // 验证图书是否存在
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("The book does not exist"));

        // 检查条码号是否已存在
        if (bookItemRepository.existsByBarcode(request.getBarcode())) {
            throw new IllegalArgumentException("The barcode number already exists");
        }

        // 创建副本实体
        BookItem bookItem = new BookItem();
        bookItem.setBook(book);
        bookItem.setBarcode(request.getBarcode());
        bookItem.setLocation(request.getLocation());

        // 设置状态
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            try {
                bookItem.setStatus(parseStatus(request.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid copy status:" + request.getStatus());
            }
        } else {
            bookItem.setStatus(BookItem.BookItemStatus.Available);
        }

        bookItem.setAcquisitionDate(request.getAcquisitionDate());
        bookItem.setAcquisitionPrice(request.getAcquisitionPrice());
        bookItem.setNotes(request.getNotes());

        // 保存副本
        BookItem savedItem = bookItemRepository.save(bookItem);

        return convertToResponse(savedItem);
    }

    /**
     * 更新图书副本
     * @param itemId 副本ID
     * @param request 图书副本请求
     * @return 更新后的图书副本详情
     */
    @Transactional
    public BookItemResponse updateBookItem(Integer itemId, BookItemRequest request) {
        // 查找副本
        BookItem bookItem = bookItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("The book copy does not exist"));

        // 检查条码号是否已被其他副本使用
        if (!request.getBarcode().equals(bookItem.getBarcode())) {
            if (bookItemRepository.existsByBarcode(request.getBarcode())) {
                throw new IllegalArgumentException("The barcode number is already in use by another copy");
            }
        }

        // 如果更改了图书ID，验证新图书是否存在
        if (!request.getBookId().equals(bookItem.getBook().getBookId())) {
            Book newBook = bookRepository.findById(request.getBookId())
                    .orElseThrow(() -> new IllegalArgumentException("The new book does not exist"));
            bookItem.setBook(newBook);
        }

        // 更新副本信息
        bookItem.setBarcode(request.getBarcode());
        bookItem.setLocation(request.getLocation());

        // 更新状态
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            try {
                bookItem.setStatus(parseStatus(request.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid copy status:" + request.getStatus());
            }
        }

        bookItem.setAcquisitionDate(request.getAcquisitionDate());
        bookItem.setAcquisitionPrice(request.getAcquisitionPrice());
        bookItem.setNotes(request.getNotes());

        // 保存副本
        BookItem savedItem = bookItemRepository.save(bookItem);

        return convertToResponse(savedItem);
    }

    /**
     * 删除图书副本
     * @param itemId 副本ID
     */
    @Transactional
    public void deleteBookItem(Integer itemId) {
        // 检查副本是否存在
        if (!bookItemRepository.existsById(itemId)) {
            throw new IllegalArgumentException("The book copy does not exist");
        }

        // 检查是否有任何关联的借阅记录（包括历史记录）
        if (borrowRecordRepository.existsByBookItemId(itemId)) {
            throw new IllegalStateException("Cannot delete book copy: it has associated borrow records. Please consider withdrawing the book copy instead of deleting it.");
        }

        // 删除副本
        bookItemRepository.deleteById(itemId);
    }

    /**
     * 将图书副本实体转换为响应对象
     * @param bookItem 图书副本实体
     * @return 图书副本响应对象
     */
    private BookItemResponse convertToResponse(BookItem bookItem) {
        Book book = bookItem.getBook();
        return new BookItemResponse(
                bookItem.getItemId(),
                book.getBookId(),
                book.getTitle(),
                book.getAuthor(),
                bookItem.getBarcode(),
                bookItem.getLocation(),
                bookItem.getStatus().name(),
                bookItem.getStatus().getDescription(),
                bookItem.getAcquisitionDate(),
                bookItem.getAcquisitionPrice(),
                bookItem.getNotes()
        );
    }

    /**
     * 解析状态字符串为枚举
     * @param status 状态字符串
     * @return 状态枚举
     */
    private BookItem.BookItemStatus parseStatus(String status) {
        return switch (status) {
            case "Available" -> BookItem.BookItemStatus.Available;
            case "Checked Out" -> BookItem.BookItemStatus.Checked_Out;
            case "Lost" -> BookItem.BookItemStatus.Lost;
            case "Damaged" -> BookItem.BookItemStatus.Damaged;
            case "Withdrawn" -> BookItem.BookItemStatus.Withdrawn;
            default -> throw new IllegalArgumentException("Invalid copy status:" + status);
        };
    }
}

