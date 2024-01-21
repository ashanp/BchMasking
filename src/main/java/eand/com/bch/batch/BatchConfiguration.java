package eand.com.bch.batch;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class BatchConfiguration {
	
	@Value("${bgh_xml_reference_file}")
	private String bgh_xml_reference_file;

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	public BatchConfiguration(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
	}

	@Bean
	ItemReader<String> xmlItemReader() {
		FlatFileItemReader<String> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(bgh_xml_reference_file));
        reader.setLineMapper(lineMapper());
        return reader;
	}
	
    @Bean
    LineMapper<String> lineMapper() {
        return new PassThroughLineMapper();
    }
    
	@Bean
	XmlItemProcessor processor() {
		return new XmlItemProcessor();
	}
	
	@Bean
	ItemWriter<String> xmlItemWriter() {
		Logger LOGs = LoggerFactory.getLogger(BatchConfiguration.class);
		return items -> {   
            for (String item : items) {
            	LOGs.info("Invoice Number:"+item+" extraction completed");
            }
        };
	}
	
	@Bean
	Step xmlTransformStep(XmlItemProcessor xmlItemProcessor) {
		return new StepBuilder("xmlTransformStep", jobRepository).<String, String>chunk(10, transactionManager)
				.reader(xmlItemReader())
				.processor(xmlItemProcessor)
				.writer(xmlItemWriter())
				.transactionManager(transactionManager) 
				.build();
	}
	
	@Bean
	Job job(JobRepository jobRepository, XmlItemProcessor xmlItemProcessor) {
	    return new JobBuilder("job", jobRepository)
	      .incrementer(new RunIdIncrementer())
	      .start(xmlTransformStep(xmlItemProcessor))
	      .build();
	  }	

}
