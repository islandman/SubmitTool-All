/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sra.biotech.submittool.persistence.service;

import com.sra.biotech.submittool.entity.Study;
import com.sra.biotech.submittool.entity.Submission;
import java.util.List;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.net.URI;

import org.springframework.web.client.RestTemplate;
import org.springframework.hateoas.Link;
 import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils; 
import  com.sra.biotech.submittool.persistence.resource.*;
import  com.sra.biotech.submittool.persistence.client.RestClientConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sra.biotech.submittool.entity.Sample;
import com.sra.biotech.submittool.entity.experiment.Experiment;
import com.sra.biotech.submittool.entity.experiment.Run;
import com.sra.biotech.submittool.persistence.client.ErrorResource;
import com.sra.biotech.submittool.persistence.client.RestUtil;
import java.io.IOException;
import org.springframework.web.client.RestClientException;
 

/**
 *
 * @author hix3
 */
@Service
public class SubmissionServiceImpl implements  SubmissionService{
	//@Autowired
	private RestTemplate restTemplate;
        //@Autowired
        private ObjectMapper objectMapper;
        private RestTemplateService restTemplateService;
	public SubmissionServiceImpl(){
            this.restTemplateService = new RestTemplateServiceImpl();
            this.restTemplate = restTemplateService.restTemplate();
        }
	 

    public SubmissionServiceImpl(RestTemplate restTemplate) {
        
        this.restTemplate = restTemplate;
    }
    public SubmissionServiceImpl(RestTemplateService restTemplateService) {
        
        this.restTemplateService = restTemplateService;
    }

    @Override
    public Submission findSubmission(Long key) {
 
        DecimalFormat intFormat = new DecimalFormat("#");
		String strKey =  intFormat.format(key);
		
		Submission submission = restTemplate.getForObject(getSubmissionUrlTemplate() +"/{key}", SubmissionResource.class, strKey).getContent();
 
		return submission;
    }

    @Override
    public List<Submission> findAllSubmission() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<String> response
                = restTemplate.exchange(getSubmissionUrlTemplate(), HttpMethod.GET, request, String.class);
       
        String responseBody = response.getBody();
        try {
            if (RestUtil.isError(response.getStatusCode())) {
                ErrorResource error = objectMapper.readValue(responseBody, ErrorResource.class);
                throw new RestClientException("[" + error.getCode() + "] " + error.getMessage());
            } else {
                SubmissionResources resources = objectMapper.readValue(responseBody, SubmissionResources.class);
              //  SubmissionResources resources = restTemplate.getForObject(getSubmissionUrlTemplate(), SubmissionResources.class);
             if (resources == null || CollectionUtils.isEmpty(resources.getContent())) {
			return Collections.emptyList();
		}
            
 
		Link listSelfLink = resources.getLink(Link.REL_SELF);
		Collection<SubmissionResource> content = resources.getContent();
 
		if (!content.isEmpty()) {
			SubmissionResource firstSubmissionResource = content.iterator().next();
			Link linkToFirstResource = firstSubmissionResource.getLink(Link.REL_SELF);
			System.out.println("href = " + linkToFirstResource.getHref());
			System.out.println("rel = " + linkToFirstResource.getRel());
		}
 
		return resources.unwrap();
               // return resources;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
       
		
    }
    public List<Submission> findByStatus( String status) {
        
                SubmissionResources resources = restTemplate.getForObject(getSubmissionUrlTemplate() + "/search/findByStatus/?status={status}", SubmissionResources.class,status);
        
		if (resources == null || CollectionUtils.isEmpty(resources.getContent())) {
			return Collections.emptyList();
		}
 
		Link listSelfLink = resources.getLink(Link.REL_SELF);
		Collection<SubmissionResource> content = resources.getContent();
 
		if (!content.isEmpty()) {
			SubmissionResource firstSubmissionResource = content.iterator().next();
			Link linkToFirstResource = firstSubmissionResource.getLink(Link.REL_SELF);
			System.out.println("href = " + linkToFirstResource.getHref());
			System.out.println("rel = " + linkToFirstResource.getRel());
		}
 
		return resources.unwrap();
    }
     
    
    public URI save(Submission newsubmission)
    {
       
        return  restTemplate.postForLocation(getSubmissionUrlTemplate(), newsubmission);
        
    }
    public Submission getSubmission(URI submissionUri)
    {
       ResponseEntity<Resource<Submission>> submissionResponseEntity
				= restTemplate.exchange(submissionUri, HttpMethod.GET, null, new ParameterizedTypeReference<Resource<Submission>>() {
		});
       Resource<Submission> submisssionResource = submissionResponseEntity.getBody();

		Link submisssionLink = submisssionResource.getLink("self");
		String submisssionUri = submisssionLink.getHref();

		return submisssionResource.getContent();
        
    }
    public Submission addStudy(URI submissionUri, Study study)
    {
       ResponseEntity<Resource<Submission>> submissionResponseEntity
				= restTemplate.exchange(submissionUri, HttpMethod.GET, null, new ParameterizedTypeReference<Resource<Submission>>() {
		});
       Resource<Submission> submisssionResource = submissionResponseEntity.getBody();

		Link submisssionLink = submisssionResource.getLink("self");
		String submisssionUri = submisssionLink.getHref();

		return submisssionResource.getContent();
        
    }
    public URI assignStudy(URI submissionUri, Study study)
    {
        ObjectMapper objectMapper = restTemplateService.getObjectMapperWithHalModule();
		ObjectNode jsonNodeStudy = (ObjectNode) objectMapper.valueToTree(study);
		jsonNodeStudy.put("submission", submissionUri.getPath());
                
		URI studyUri = restTemplate.postForLocation(studiesUri(), jsonNodeStudy);
                ResponseEntity<Resource<Study>> studyResponseEntity
				= restTemplate.exchange(studyUri, HttpMethod.GET, null, new ParameterizedTypeReference<Resource<Study>>() {
		});
		Resource<Study> studyResource = studyResponseEntity.getBody();
		Link submissionLinkThroughStudy = studyResource.getLink("submission");
		System.out.println("Submission Link through Study = " + submissionLinkThroughStudy);
                return studyUri;
    }    
     public URI assignSample(URI submissionUri, Sample sample)
     {
        ObjectMapper objectMapper = restTemplateService.getObjectMapperWithHalModule();
                String samplesUri = RestClientConfiguration.BASE_URL + "/"	 + "samples";
		ObjectNode jsonNodeSample = (ObjectNode) objectMapper.valueToTree(sample);
		jsonNodeSample.put("submission", submissionUri.getPath());
                
		URI sampleUri = restTemplate.postForLocation(samplesUri, jsonNodeSample);
                ResponseEntity<Resource<Sample>> sampleResponseEntity
				= restTemplate.exchange(sampleUri, HttpMethod.GET, null, new ParameterizedTypeReference<Resource<Sample>>() {
		});

		Resource<Sample> studyResource = sampleResponseEntity.getBody();
		Link submissionLinkThroughSample = studyResource.getLink("submission");
		System.out.println("Submission Link through Sample = " + submissionLinkThroughSample);
                return sampleUri;
    }   
    public URI assignExperiment(URI submissionUri, Experiment experiment)
    {
        ObjectMapper objectMapper = restTemplateService.getObjectMapperWithHalModule();
	String experimentsUri = RestClientConfiguration.BASE_URL + "/" + "experiments";	
        ObjectNode jsonNodeExperiment = (ObjectNode) objectMapper.valueToTree(experiment);
		jsonNodeExperiment.put("submission", submissionUri.getPath());
                
		URI experimentUri = restTemplate.postForLocation(experimentsUri, jsonNodeExperiment);
                ResponseEntity<Resource<Experiment>> experimentResponseEntity
				= restTemplate.exchange(experimentUri, HttpMethod.GET, null, new ParameterizedTypeReference<Resource<Experiment>>() {
		});

		Resource<Experiment> experimentResource = experimentResponseEntity.getBody();
		Link submissionLinkThroughExperiment = experimentResource.getLink("submission");
		System.out.println("Submission Link through Experiment = " + submissionLinkThroughExperiment);
                return experimentUri;
    } 
    public Link assignRun(URI submissionUri, Run run)
    {
        ObjectMapper objectMapper = restTemplateService.getObjectMapperWithHalModule();
                String runsUri = RestClientConfiguration.BASE_URL + "/" + "runs";
		ObjectNode jsonNodeRun = (ObjectNode) objectMapper.valueToTree(run);
		jsonNodeRun.put("experiment", submissionUri.getPath());
                
		URI runUri = restTemplate.postForLocation(runsUri, jsonNodeRun);
                ResponseEntity<Resource<Run>> runResponseEntity
				= restTemplate.exchange(runUri, HttpMethod.GET, null, new ParameterizedTypeReference<Resource<Run>>() {
		});

		Resource<Run> runResource = runResponseEntity.getBody();
		Link experimentLinkThroughRun = runResource.getLink("experiment");
		System.out.println("Experiment Link through Run = " + experimentLinkThroughRun);
                return experimentLinkThroughRun;
    }   
    
    public void  update(Submission submission)
    {
    	 HttpEntity<Submission> entity = new HttpEntity<Submission>(submission);
         restTemplate.put(getSubmissionUrlTemplate() +"/{id}",  submission.getId());
        
    }
    public void delete(final long id) {
        this.restTemplate.delete(getSubmissionUrlTemplate() +"/{id}", id);
    }
    public String getSubmissionUrlTemplate()
    {
    	return RestClientConfiguration.BASE_URL + "/"	 + "submissions";
    }
    public String studiesUri()
    {
    	return RestClientConfiguration.BASE_URL + "/"	 + "studies";
    }
}
