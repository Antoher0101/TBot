package com.mawus.core.repository.nonpersistent;

import com.mawus.core.domain.ClientAction;

public interface ClientActionRepository {

    ClientAction findByChatId(Long chatId);

    void updateByChatId(Long chatId, ClientAction clientAction);

    void deleteByChatId(Long chatId);
}
