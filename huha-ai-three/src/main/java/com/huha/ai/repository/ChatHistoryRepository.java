package com.huha.ai.repository;


import java.util.List;

public interface ChatHistoryRepository {
    /**
     * 保存会话记录
     * @param type 业务类型，如：chat、service、pdf
     * @param chatId 会话ID
     */
    void save(String type,String chatId);

    /**
     * 获取会话ID列表
     * @param type 业务类型，如：chat、service、pdf
     * @return 会话ID列表
     */
    List<String> getChatIds(String type);

    /**
     * 保存会话元信息（如title）
     * @param type 业务类型
     * @param chatId 会话ID
     * @param title 会话标题
     */
    default void saveChatMeta(String type, String chatId, String title) {}
}
