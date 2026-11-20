package ru.otus.hw.dao;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    /* в CsvQuestionDao.findAll() перехватывается любое исключение, но исходная причина теряется. Лучше передавать её в QuestionReadException, чтобы было проще разобраться с ошибками чтения или разбора CSV. */
    @Override
    public List<Question> findAll() {
        ClassLoader classLoader = getClass().getClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(fileNameProvider.getTestFileName())) {
            InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            ColumnPositionMappingStrategy<QuestionDto> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(QuestionDto.class);
            List<QuestionDto> list = new CsvToBeanBuilder<QuestionDto>(streamReader)
                    .withType(QuestionDto.class)
                    .withSeparator(';')
                    .withSkipLines(1)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();

            return list.stream().map(QuestionDto::toDomainObject).toList();
        } catch (Exception e) {
            throw new QuestionReadException("Resource not found: " + fileNameProvider.getTestFileName());
        }
    }
}