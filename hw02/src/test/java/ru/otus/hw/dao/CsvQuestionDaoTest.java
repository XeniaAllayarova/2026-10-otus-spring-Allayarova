package ru.otus.hw.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class CsvQuestionDaoTest {
    @Mock
    private TestFileNameProvider fileNameProvider;

    private AutoCloseable mocks;
    private QuestionDao questionDao;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        questionDao = new CsvQuestionDao(fileNameProvider);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void shouldReadQuestionsFromCsv() {
        when(fileNameProvider.getTestFileName()).thenReturn("questions.csv");

        List<Question> questions = questionDao.findAll();

        assertThat(questions).hasSize(3);
        Question first = questions.get(0);
        assertThat(first.text()).isEqualTo("Is there life on Mars?");
        assertThat(first.answers())
                .extracting(Answer::text)
                .containsExactly("Science doesn't know this yet", "Certainly. The red UFO is from Mars. And green is from Venus", "Absolutely not");
        assertThat(first.answers())
                .extracting(Answer::isCorrect)
                .containsExactly(true, false, false);
    }

    @Test
    void shouldThrowExceptionWhenFileNotFound() {
        when(fileNameProvider.getTestFileName()).thenReturn("fileNotFound.csv");

        assertThatThrownBy(() -> questionDao.findAll())
                .isInstanceOf(QuestionReadException.class);
    }
}
