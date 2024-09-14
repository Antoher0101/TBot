package com.mawus.core.repository.nonpersistent;

import com.mawus.core.domain.Command;

public interface ClientCommandStateRepository {

    void pushByChatId(Long chatId, Command command);

    Command popByChatId(Long chatId);

    void deleteAllByChatId(Long chatId);
}
