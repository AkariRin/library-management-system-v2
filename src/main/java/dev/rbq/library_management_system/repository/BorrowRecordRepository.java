package dev.rbq.library_management_system.repository;

import dev.rbq.library_management_system.entity.BorrowRecord;
import dev.rbq.library_management_system.entity.BorrowRecord.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 借阅记录数据访问接口
 */
@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, String> {

    /**
     * 根据用户UUID查询借阅记录（分页）
     * @param userUuid 用户UUID
     * @param pageable 分页参数
     * @return 借阅记录分页列表
     */
    @Query("SELECT br FROM BorrowRecord br WHERE br.user.uuid = :userUuid ORDER BY br.borrowDate DESC")
    Page<BorrowRecord> findByUserUuid(@Param("userUuid") String userUuid, Pageable pageable);

    /**
     * 根据用户UUID和状态查询借阅记录（分页）
     * @param userUuid 用户UUID
     * @param status 借阅状态
     * @param pageable 分页参数
     * @return 借阅记录分页列表
     */
    @Query("SELECT br FROM BorrowRecord br WHERE br.user.uuid = :userUuid AND br.status = :status ORDER BY br.borrowDate DESC")
    Page<BorrowRecord> findByUserUuidAndStatus(@Param("userUuid") String userUuid, @Param("status") BorrowStatus status, Pageable pageable);

    /**
     * 根据状态查询借阅记录（分页）
     * @param status 借阅状态
     * @param pageable 分页参数
     * @return 借阅记录分页列表
     */
    Page<BorrowRecord> findByStatus(BorrowStatus status, Pageable pageable);

    /**
     * 检查图书副本是否有未归还的借阅记录
     * @param itemId 图书副本ID
     * @return true 如果有未归还的记录，否则 false
     */
    @Query("SELECT COUNT(br) > 0 FROM BorrowRecord br WHERE br.bookItem.itemId = :itemId AND br.status = 'Checked_Out'")
    boolean existsByBookItemIdAndStatusCheckedOut(@Param("itemId") Integer itemId);

    /**
     * 检查图书副本是否有任何借阅记录（包括历史记录）
     * @param itemId 图书副本ID
     * @return true 如果有任何借阅记录，否则 false
     */
    @Query("SELECT COUNT(br) > 0 FROM BorrowRecord br WHERE br.bookItem.itemId = :itemId")
    boolean existsByBookItemId(@Param("itemId") Integer itemId);

    /**
     * 查询用户当前借阅中的图书副本记录
     * @param userUuid 用户UUID
     * @param itemId 图书副本ID
     * @return 借阅记录列表
     */
    @Query("SELECT br FROM BorrowRecord br WHERE br.user.uuid = :userUuid AND br.bookItem.itemId = :itemId AND br.status = 'Checked_Out'")
    List<BorrowRecord> findActiveRecordByUserAndItem(@Param("userUuid") String userUuid, @Param("itemId") Integer itemId);

    /**
     * 查询逾期未还的借阅记录
     * @param currentTime 当前时间
     * @param pageable 分页参数
     * @return 逾期借阅记录分页列表
     */
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 'Checked_Out' AND br.dueDate < :currentTime ORDER BY br.dueDate ASC")
    Page<BorrowRecord> findOverdueRecords(@Param("currentTime") LocalDateTime currentTime, Pageable pageable);

    /**
     * 多条件查询借阅记录（管理员使用）
     * @param userUuid 用户UUID（可选）
     * @param status 借阅状态（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @param pageable 分页参数
     * @return 借阅记录分页列表
     */
    @Query("SELECT br FROM BorrowRecord br WHERE " +
            "(:userUuid IS NULL OR br.user.uuid = :userUuid) AND " +
            "(:status IS NULL OR br.status = :status) AND " +
            "(:startDate IS NULL OR br.borrowDate >= :startDate) AND " +
            "(:endDate IS NULL OR br.borrowDate <= :endDate) " +
            "ORDER BY br.borrowDate DESC")
    Page<BorrowRecord> findByMultipleConditions(
            @Param("userUuid") String userUuid,
            @Param("status") BorrowStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}
