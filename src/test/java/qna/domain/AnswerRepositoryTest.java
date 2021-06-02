package qna.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class AnswerRepositoryTest {

    @Autowired
    private AnswerRepository answers;

    @Autowired
    private UserRepository users;

    @Autowired
    private QuestionRepository questions;

    private Answer answer;

    private Answer actual;

    @BeforeEach
    void setUp() {
        User writer = new User("tj", "ps", "김석진", "7271kim@naver.com");
        User questionWriter = new User("tjs", "pss", "김석진2", "7271ki2m@naver.com");
        Question question = new Question("질문", "입니다.").writeBy(questionWriter);

        answer = new Answer(writer, question, "Answers Contents1");
        actual = answers.save(answer);
        users.save(writer);
        users.save(questionWriter);
        questions.save(question);
    }

    @Test
    @DisplayName("정상적으로 전 후 데이터가 들어가 있는지 확인한다.")
    void save() {
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getCreatedAt()).isNotNull(),
            () -> assertThat(actual.getUpdatedAt()).isNull(),
            () -> assertThat(actual.getContents()).isEqualTo(answer.getContents()),
            () -> assertThat(actual.isDeleted()).isEqualTo(answer.isDeleted()),
            () -> assertThat(actual.getQuestion()).isEqualTo(answer.getQuestion()),
            () -> assertThat(actual.getWriter()).isEqualTo(answer.getWriter()));
    }

    @Test
    @DisplayName("update 확인")
    void updata() {
        answer.setContents("change content");
        answers.saveAndFlush(answer);
        Answer finedAnswer = answers.findById(answer.getId()).get();
        assertAll(
            () -> assertThat(finedAnswer.getContents()).isEqualTo("change content"),
            () -> assertThat(finedAnswer.getUpdatedAt()).isNotNull());
    }

    @Test
    @DisplayName("question 연관관계 확인")
    void qustionCheck() {
        assertThat(actual.getQuestion()).isNotNull();
        assertThat(actual.getQuestion().getId()).isNotNull();
    }

    @Test
    @DisplayName("writer 연관관계 확인")
    void writerCheck() {
        assertThat(actual.getWriter()).isNotNull();
        assertThat(actual.getWriter().getId()).isNotNull();
    }
}
