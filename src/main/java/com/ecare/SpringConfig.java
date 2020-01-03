package com.ecare;

import java.util.concurrent.TimeUnit;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.AWSStepFunctionsClientBuilder;

@Configuration("classpath:application-aws.properties")
@EnableAutoConfiguration
@EnableDynamoDBRepositories(basePackageClasses = {com.ecare.model.db.repository.PatientRepository.class})
@ComponentScan("com.ecare")
public class SpringConfig {

	@Value("${amazon_dynamodb_endpoint}")
	private String amazonDynamoDBEndpoint;

	@Value("${amazon_aws_accesskey}")
	private String amazonAWSAccessKey;

	@Value("${amazon_aws_secretkey}")
	private String amazonAWSSecretKey;

	@Bean
	public AWSCredentials amazonAWSCredentials() {
		return new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey);
	}

	@Bean
	public AmazonDynamoDB amazonDynamoDB() {
		AmazonDynamoDB amazonDynamoDB = new AmazonDynamoDBClient(amazonAWSCredentials());
		if (!StringUtils.isEmpty(amazonDynamoDBEndpoint)) {
			amazonDynamoDB.setEndpoint(amazonDynamoDBEndpoint);
		}
		return amazonDynamoDB;
	}

	@Bean
	public DynamoDB dynamoDB() {
		return new DynamoDB(amazonDynamoDB());
	}

	
	@Bean(name="awsStepFunctionClient")
	public AWSStepFunctions getStepFunctionClient() {
		ClientConfiguration clientConfiguration = new ClientConfiguration();
		clientConfiguration.setSocketTimeout((int) TimeUnit.SECONDS.toMillis(70));
		AWSStepFunctions client = AWSStepFunctionsClientBuilder.standard().withRegion(Regions.AP_SOUTH_1)
				.withCredentials(new EnvironmentVariableCredentialsProvider())
				.withClientConfiguration(clientConfiguration).build();
		return client;
	}

}
