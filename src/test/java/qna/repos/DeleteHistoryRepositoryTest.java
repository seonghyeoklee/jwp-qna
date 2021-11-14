package qna.repos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import qna.domain.ContentType;
import qna.domain.DeleteHistory;
import qna.domain.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DeleteHistoryRepositoryTest {
    @Autowired
    private DeleteHistoryRepository deleteHistories;
    @Autowired
    private UserRepository users;

    @DisplayName("DeleteHistory 저장 테스트")
    @Test
    void save() {
        User user = users.save(UserRepositoryTest.JAVAJIGI);
        users.save(user);
        DeleteHistory deleteHistory = new DeleteHistory(ContentType.ANSWER, 0L, user, LocalDateTime.now());
        DeleteHistory actual = deleteHistories.save(deleteHistory);
        assertThat(actual).isEqualTo(deleteHistory);
    }
}