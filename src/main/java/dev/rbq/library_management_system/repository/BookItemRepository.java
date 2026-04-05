package dev.rbq.library_management_system.repository;

import dev.rbq.library_management_system.entity.BookItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 图书副本数据访问接口
 */
@Repository
public interface BookItemRepository extends JpaRepository<BookItem, Integer> {

    /**
     * 根据条码号查找图书副本
     * @param barcode 条码号
     * @return 图书副本（如果存在）
     */
    Optional<BookItem> findByBarcode(String barcode);

    /**
     * 检查条码号是否存在
     * @param barcode 条码号
     * @return true 如果条码号已存在，否则 false
     */
    boolean existsByBarcode(String barcode);

    /**
     * 根据图书ID查询该图书的所有副本（分页）
     * @param bookId 图书ID
     * @param pageable 分页参数
     * @return 图书副本分页列表
     */
    @Query("SELECT bi FROM BookItem bi WHERE bi.book.bookId = :bookId")
    Page<BookItem> findByBookId(@Param("bookId") Integer bookId, Pageable pageable);

    /**
     * 根据图书ID和状态查询副本（分页）
     * @param bookId 图书ID
     * @param status 副本状态
     * @param pageable 分页参数
     * @return 图书副本分页列表
     */
    @Query("SELECT bi FROM BookItem bi WHERE bi.book.bookId = :bookId AND bi.status = :status")
    Page<BookItem> findByBookIdAndStatus(
            @Param("bookId") Integer bookId,
            @Param("status") BookItem.BookItemStatus status,
            Pageable pageable
    );

    /**
     * 根据馆藏位置查询副本（分页）
     * @param location 馆藏位置
     * @param pageable 分页参数
     * @return 图书副本分页列表
     */
    Page<BookItem> findByLocationContaining(String location, Pageable pageable);

    /**
     * 统计某本图书的副本数量
     * @param bookId 图书ID
     * @return 副本数量
     */
    @Query("SELECT COUNT(bi) FROM BookItem bi WHERE bi.book.bookId = :bookId")
    long countByBookId(@Param("bookId") Integer bookId);

    /**
     * 统计某本图书特定状态的副本数量
     * @param bookId 图书ID
     * @param status 副本状态
     * @return 副本数量
     */
    @Query("SELECT COUNT(bi) FROM BookItem bi WHERE bi.book.bookId = :bookId AND bi.status = :status")
    long countByBookIdAndStatus(
            @Param("bookId") Integer bookId,
            @Param("status") BookItem.BookItemStatus status
    );
}
