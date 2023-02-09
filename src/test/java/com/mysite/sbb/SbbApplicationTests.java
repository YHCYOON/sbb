package com.mysite.sbb;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import com.mysite.sbb.question.QuestionService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class SbbApplicationTests {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionService questionService;

    @Test
    void 질문저장() {
        Question q1 = new Question();
        q1.setSubject("sbb가 무엇인가요?");
        q1.setContent("sbb에 대해 알고 싶어요");
        q1.setCreateDate(LocalDateTime.now());
        this.questionRepository.save(q1);

        Question q2 = new Question();
        q2.setSubject("스프링부트 모델 질문이요");
        q2.setContent("id는 자동 생성인가요");
        q2.setCreateDate(LocalDateTime.now());
        this.questionRepository.save(q2);
    }

    @Test
    void 질문조회() {
        List<Question> all = this.questionRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        Question q = all.get(0);
        assertThat(q.getSubject()).isEqualTo("sbb가 무엇인가요?");
    }

    @Test
    void ID로조회() {
        Optional<Question> oq = this.questionRepository.findById(1);
        if (oq.isPresent()) {
            Question q = oq.get();
            assertThat(q.getSubject()).isEqualTo("sbb가 무엇인가요?");
        }
    }

    @Test
    void 제목으로조회() {
        Question q = this.questionRepository.findBySubject("sbb가 무엇인가요?");
        assertThat(q.getId()).isEqualTo(1);
    }

    @Test
    void AND조회() {
        Question q = this.questionRepository.findBySubjectAndContent(
                "sbb가 무엇인가요?", "sbb에 대해 알고 싶어요");
        assertThat(q.getId()).isEqualTo(1);
    }

    @Test
    void Like조회() {
        List<Question> lq = this.questionRepository.findBySubjectLike("sbb가%");
        assertThat(lq.size()).isEqualTo(1);
    }

    @Test
    void 수정() {
        Optional<Question> oq = this.questionRepository.findById(1);
        assertThat(oq.isPresent()).isTrue();
        Question q = oq.get();
        q.setSubject("수정된 제목");
        this.questionRepository.save(q);
    }

    @Test
    void 삭제() {
        Optional<Question> oq = this.questionRepository.findById(1);
        assertThat(oq.isPresent()).isTrue();
        Question q = oq.get();
        this.questionRepository.delete(q);
        assertThat(this.questionRepository.count()).isEqualTo(1);
    }

    @Test
    void 답변생성() {
        Optional<Question> oq = this.questionRepository.findById(2);
        assertThat(oq.isPresent()).isTrue();
        Question q = oq.get();

        Answer a1 = new Answer();
        a1.setContent("답변입니다");
        a1.setCreateDate(LocalDateTime.now());
        a1.setQuestion(q);

        this.answerRepository.save(a1);
    }

    @Test
    void 답변조회() {
        Optional<Answer> oa = this.answerRepository.findById(1);
        assertThat(oa.isPresent()).isTrue();

        Answer a = oa.get();
        assertThat(a.getQuestion().getId()).isEqualTo(2);
    }

    @Transactional
    @Test
    void 질문으로답변찾기() {
        Optional<Question> oq = this.questionRepository.findById(2);
        assertThat(oq.isPresent()).isTrue();
        Question q = oq.get();

        List<Answer> la = q.getAnswerList();
        assertThat(la.get(0).getId()).isEqualTo(1);
    }

    @Test
    void 질문여러개만들기() {
        for (int i = 0; i < 300; i++) {
            String subject = String.format("테스트 데이터 : [%03d]", i);
            String content = "내용없음";
            this.questionService.createQuestion(subject, content, null);
        }
    }
}
