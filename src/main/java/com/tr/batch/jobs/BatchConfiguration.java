package com.tr.batch.jobs;


import javax.sql.DataSource;

import org.springframework.batch.admin.annotation.EnableBatchAdmin;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import com.tr.batch.EmailCompletion;

@Configuration
@EnableBatchAdmin
@EnableAutoConfiguration(exclude=(HypermediaAutoConfiguration.class))
public class BatchConfiguration {

    // tag::readerwriterprocessor[]
    @Bean
    public ItemReader<EmailCompletion> reader() {
        FlatFileItemReader<EmailCompletion> reader = new FlatFileItemReader<EmailCompletion>();
        reader.setResource(new ClassPathResource("sample-data.csv"));
        reader.setLineMapper(new DefaultLineMapper<EmailCompletion>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "firstName", "lastName" });
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<EmailCompletion>() {{
                setTargetType(EmailCompletion.class);
            }});
        }});
        return reader;
    }

    @Bean
    public ItemProcessor<EmailCompletion, EmailCompletion> processor() {
       // return new EmailCompletionItemProcessor();
    	return null;
    }

    @Bean
    public ItemWriter<EmailCompletion> writer(DataSource dataSource) {
        JdbcBatchItemWriter<EmailCompletion> writer = new JdbcBatchItemWriter<EmailCompletion>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<EmailCompletion>());
        writer.setSql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)");
        writer.setDataSource(dataSource);
        return writer;
    }
    // end::readerwriterprocessor[]

    // tag::jobstep[]
    @Bean
    public Job importUserJob(JobBuilderFactory jobs, Step s1, JobExecutionListener listener) {
        return jobs.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(s1)
                .end()
                .build();
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<EmailCompletion> reader,
            ItemWriter<EmailCompletion> writer, ItemProcessor<EmailCompletion, EmailCompletion> processor) {
        return stepBuilderFactory.get("step1")
                .<EmailCompletion, EmailCompletion> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
    // end::jobstep[]

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}