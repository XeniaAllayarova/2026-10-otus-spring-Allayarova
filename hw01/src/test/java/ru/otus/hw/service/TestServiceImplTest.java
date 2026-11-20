package ru.otus.hw.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TestServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    private AutoCloseable mocks;
    private TestService testService;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        testService = new TestServiceImpl(ioService, questionDao);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void shouldPrintAllQuestionsWithNumberedAnswers() {
        Question q1 = new Question("Is there life on Mars?", List.of(
                new Answer("Science doesn't know this yet", true),
                new Answer("Certainly. The red UFO is from Mars. And green is from Venus", false),
                new Answer("Absolutely not", false)));
        when(questionDao.findAll()).thenReturn(List.of(q1));

        testService.executeTest();

        verify(ioService).printFormattedLine("Please answer the questions below%n");

        verify(ioService).printLine("Is there life on Mars?");

        verify(ioService).printLine("1. Science doesn't know this yet");
        verify(ioService).printLine("2. Certainly. The red UFO is from Mars. And green is from Venus");
        verify(ioService).printLine("3. Absolutely not");

        verify(questionDao).findAll();
    }

    @Test
    void shouldNotPrintQuestionsWhenDaoReturnsEmptyList() {
        when(questionDao.findAll()).thenReturn(List.of());

        testService.executeTest();

        verify(ioService).printLine("");
        verify(ioService).printFormattedLine("Please answer the questions below%n");
        verify(ioService, times(1)).printLine(anyString());
    }
}