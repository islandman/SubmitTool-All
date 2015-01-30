/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sra.biotech.submittool.persistence.client;
import com.sra.biotech.submittool.submitcli.GroovyExcelParser;
//import com.sra.biotech.submittool.cdm.Submission;
//
 


/**
 *
 * @author hix3
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.net.URI;

import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.core.AnnotationRelProvider;
import org.springframework.hateoas.core.DefaultRelProvider;
import org.springframework.hateoas.core.DelegatingRelProvider;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.plugin.core.OrderAwarePluginRegistry;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
 
 
 

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sra.biotech.submittool.entity.FileDeliveryInfo;
import com.sra.biotech.submittool.persistence.service.FileDeliveryInfoServiceImpl;
import com.sra.biotech.submittool.cdm.builder.CdmBuilder;
import com.sra.biotech.submittool.entity.Study;
import com.sra.biotech.submittool.persistence.service.SubmissionService;
import com.sra.biotech.submittool.persistence.service.SubmissionServiceImpl;

public class RestClientConfiguration {
	static public final String BASE_URL = "http://localhost:8090/submit/api";

	static public final String FILE_DELIVERY_STATATUS_INFO_REL = "fileDeliveryStatusInfo";
	static public final String SAMPLE_REL= "samples";
	static public final String STUDY_REL= "studies";
	static public final String EXPERIMENT_REL= "experiments";
	static public final String SUBMISSION_REL= "submissions";
	 
	//@Bean
	public RestClientConfiguration(){}
	public RestTemplate restTemplate() {
	   List<HttpMessageConverter<?>> converters = new ArrayList<>();
	   converters.add(halConverter());

		RestTemplate restTemplate1 = new RestTemplate();

	 	restTemplate1.setMessageConverters(converters);

		return restTemplate1;
	}

	//@Bean
	public MappingJackson2HttpMessageConverter halConverter() {
		RelProvider defaultRelProvider = defaultRelProvider();
		RelProvider annotationRelProvider = annotationRelProvider();
 
		OrderAwarePluginRegistry<RelProvider, Class<?>> relProviderPluginRegistry = OrderAwarePluginRegistry
		        .create(Arrays.asList(defaultRelProvider, annotationRelProvider));
 
		DelegatingRelProvider delegatingRelProvider = new DelegatingRelProvider(relProviderPluginRegistry);
 
		ObjectMapper halObjectMapper = new ObjectMapper();
		halObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		halObjectMapper.registerModule(new Jackson2HalModule());
		halObjectMapper
		        .setHandlerInstantiator(new Jackson2HalModule.HalHandlerInstantiator(delegatingRelProvider, null));
 
		MappingJackson2HttpMessageConverter halConverter = new MappingJackson2HttpMessageConverter();
		halConverter.setSupportedMediaTypes(Arrays.asList(MediaTypes.HAL_JSON));
		halConverter.setObjectMapper(halObjectMapper);
		return halConverter;
	}

	 
	public DefaultRelProvider defaultRelProvider() {
		return new DefaultRelProvider();
	}
        
        public void submissionToDatabase(com.sra.biotech.submittool.entity.Submission submission)
        {
            for (com.sra.biotech.submittool.entity.Study study : submission.getStudies() )   {
                
            }
        }        
	 
	public AnnotationRelProvider annotationRelProvider() {
		return new AnnotationRelProvider();
	}
	public static void main(String args[]) {
	       
            RestTemplate restTemplate=null;
		//restTemplate.setMessageConverters(getMessageConverters());
            RestClientConfiguration restClient = new RestClientConfiguration();
            final String submissionUrl = RestClientConfiguration.BASE_URL + "/"  + "submissions";

            final String studiesUrl = RestClientConfiguration.BASE_URL + "/" + "studies";


            String path = "/projects/oidproject/hix3/data/Pertussis_SRA_submission_template_CDC_V09_dummy.xlsx";
            GroovyExcelParser parser = new GroovyExcelParser();
            String json =  (String)parser.parse(path);


            com.sra.biotech.submittool.cdm.Cdm cdm = new CdmBuilder().buildFromJson(json);
            String printStr = new CdmBuilder().prettyPrint(cdm);
            System.out.println("pretty Json = " + printStr);
            com.sra.biotech.submittool.cdm.Submission submission = cdm.getSubmissions().get(0);


            restTemplate = restClient.restTemplate();
            /*
            FileDeliveryInfoServiceImpl serviceImpl = new FileDeliveryInfoServiceImpl (restTemplate);

            List<FileDeliveryInfo> fileDeliveryInfo = serviceImpl.findAllFileDeliveryInfo ();
            System.out.println(fileDeliveryInfo.toString()); */
            String sampleSubmission = submission.jsonSampleSubmission();
            SubmissionService  submitImpl = new SubmissionServiceImpl();
            System.out.println("Sample submission= " + submission.jsonSampleSubmission());
            System.out.println("Sample Studies = " + submission.getStudies().get(0).jsonSampleStudy());

           // final URI submissionUri = restTemplate.postForLocation(submissionUrl, submission.jsonSampleSubmission());
            //submitImpl.save(null)
                
		 
    }
}     