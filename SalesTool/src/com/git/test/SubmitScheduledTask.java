package com.sra.biotech.submittool.schedulers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import com.sra.biotech.submittool.entity.Submission;
import com.sra.biotech.submittool.cdm.Cdm;
 
import com.sra.biotech.submittool.cdm.ContactInfo;
import com.sra.biotech.submittool.cdm.builder.CdmBuilder;
import com.sra.biotech.submittool.cdm.builder.SubmissionBuilder;
import com.sra.biotech.submittool.cdm.common.SubmitDestinationEnum;
import com.sra.biotech.submittool.persistence.client.RestClientConfiguration;
import com.sra.biotech.submittool.persistence.service.SubmissionServiceImpl;
import com.sra.biotech.submittool.persistence.client.DatabaseToCdmClient;
import com.sra.biotech.submittool.rest.client.HttpRestClientPost;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.apache.commons.beanutils.BeanUtils;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
 
@EnableScheduling
public class SubmitScheduledTask {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 500000)
    public void pullSubmissionsFromDB() {
        System.out.println("The time is now " + dateFormat.format(new Date()));
        DatabaseToCdmClient  dbToCdm  =  new DatabaseToCdmClient ();
        final String submissionUrl = RestClientConfiguration.BASE_URL + "/"  + "submissions";
        final String studiesUrl = RestClientConfiguration.BASE_URL + "/" + "studies";
        Mapper mapper=null;
        List<Submission> submits =null;
        submits = dbToCdm.findByStatus("created");
        List<com.sra.biotech.submittool.cdm.Submission> submissions = new ArrayList<com.sra.biotech.submittool.cdm.Submission>();
        com.sra.biotech.submittool.cdm.Submission cdmSubmission=null;
        for (Submission submission : submits)   {
            mapper = new DozerBeanMapper();
            cdmSubmission  = mapper.map(submission, com.sra.biotech.submittool.cdm.Submission.class);
            submissions.add(cdmSubmission);
        } 
        Cdm cdm = new Cdm();
        cdm.setSubmissions(submissions);
        String json = new CdmBuilder().prettyPrint(submits);
        System.out.println("submissions. " + new CdmBuilder().prettyPrint(submits));
	String response = new HttpRestClientPost("http://localhost:8080/submit/cdm",json).post(); 
	  
    }
}