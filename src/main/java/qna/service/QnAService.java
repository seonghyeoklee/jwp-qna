package qna.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qna.CannotDeleteException;
import qna.NotFoundException;
import qna.domain.Answer;
import qna.domain.AnswerRepository;
import qna.domain.ContentType;
import qna.domain.DeleteHistory;
import qna.domain.Question;
import qna.domain.QuestionRepository;
import qna.domain.User;

@Service
public class QnAService {
	private static final Logger log = LoggerFactory.getLogger(QnAService.class);

	private QuestionRepository questionRepository;
	private AnswerRepository answerRepository;
	private DeleteHistoryService deleteHistoryService;

	public QnAService(QuestionRepository questionRepository, AnswerRepository answerRepository,
		DeleteHistoryService deleteHistoryService) {
		this.questionRepository = questionRepository;
		this.answerRepository = answerRepository;
		this.deleteHistoryService = deleteHistoryService;
	}

	@Transactional(readOnly = true)
	public Question findQuestionById(Long id) {
		return questionRepository.findByIdAndDeletedFalse(id)
			.orElseThrow(NotFoundException::new);
	}

	@Transactional
	public void deleteQuestion(User loginUser, Long questionId) throws CannotDeleteException {
		Question question = findQuestionById(questionId);
		if (!question.isOwner(loginUser)) {
			throw new CannotDeleteException("질문을 삭제할 권한이 없습니다.");
		}

		List<Answer> answers = answerRepository.findByQuestionIdAndDeletedFalse(questionId);
		for (Answer answer : answers) {
			if (!answer.isOwner(loginUser)) {
				throw new CannotDeleteException("다른 사람이 쓴 답변이 있어 삭제할 수 없습니다.");
			}
		}

		List<DeleteHistory> deleteHistories = new ArrayList<>();
		question.setDeleted(true);
		deleteHistories.add(
			new DeleteHistory(ContentType.QUESTION, questionId, question.getWriter(), LocalDateTime.now()));
		for (Answer answer : answers) {
			answer.setDeleted(true);
			deleteHistories.add(
				new DeleteHistory(ContentType.ANSWER, answer.getId(), answer.getWriter(), LocalDateTime.now()));
		}
		deleteHistoryService.saveAll(deleteHistories);
	}
}