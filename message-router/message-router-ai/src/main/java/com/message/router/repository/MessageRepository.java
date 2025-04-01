package com.message.router.repository;

import com.message.router.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 消息仓库接口
 * 
 * @author rcloud
 * @since 2025-03-31
 */
@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
} 