package com.sra.biotech.submittool.schedulers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.sra.biotech.submittool.entity.Submission;
import com.sra.biotech.submittool.cdm.Cdm;
import com.sra.biotech.submittool.cdm.ContactInfo;
import com.sra.biotech.submittool.cdm.builder.CdmBuilder;
import com.sra.biotech.submittool.cdm.builder.SubmissionBuilder;
import com.sra.biotech.submittool.cdm.common.SubmitDestinationEnum;
import com.sra.biotech.submittool.entity.ProcessingStatusEnum;
import com.sra.biotech.submittool.persistence.client.RestClientConfiguration;
import com.sra.biotech.submittool.persistence.service.SubmissionServiceImpl;
import com.sra.biotech.submittool.persistence.client.DatabaseToCdmClient;
import com.sra.biotech.submittool.persistence.service.SubmissionService;
import com.sra.biotech.submittool.rest.client.HttpRestClientPost;
import com.sra.biotech.submittool.service.PublishService;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.apache.commons.beanutils.BeanUtils;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
 
@EnableScheduling
@Component
public class SubmitScheduledTask {
    @Autowired 
    private PublishService  publishService;
     @Autowired
    private SubmissionService submissionService;
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 50000)
    public void pullSubmissionsFromDB() {
        System.out.println("The time is now " + dateFormat.format(new Date()));
        DatabaseToCdmClient  dbToCdm  =  new DatabaseToCdmClient ();
        final String submissionUrl = RestClientConfiguration.BASE_URL + "/"  + "submissions";
        final String studiesUrl = RestClientConfiguration.BASE_URL + "/" + "studies";
        Mapper mapper=null;
        List<Submission> submits =null;
        submits = dbToCdm.findByStatus(ProcessingStatusEnum.SUBMISSION_CREATED.getStatusCode());
        List<com.sra.biotech.submittool.cdm.Submission> submissions = new ArrayList<com.sra.biotech.submittool.cdm.Submission>();
        com.sra.biotech.submittool.cdm.Submission cdmSubmission=null;
        for (Submission submission : submits)   {
          Submission sub = submission.build();
            sub.setStatus(ProcessingStatusEnum.READY_FOR_SUBMISSION.getStatusCode());
            //if(submission.getId() != null){
             //   System.out.println("Submission original ID is:" + submission.getId());
            //}
          //  sub.setId(submission.getId());
            
           submissionService.update(sub, submission.getSelfUrl());
            mapper = new DozerBeanMapper();
            cdmSubmission  = mapper.map(submission, com.sra.biotech.submittool.cdm.Submission.class);
            submissions.add(cdmSubmission);
        } 
        if (submits !=null && submits.size() > 0)
        {    
            Cdm cdm = new Cdm();
            cdm.setSubmissions(submissions);
            String json = new CdmBuilder().prettyPrint(submits);
           // System.out.println("submissions. " + new CdmBuilder().prettyPrint(submits));
            /* publish to SRA for now */
            publishService.send(cdm);
        }     
         
	  
    }
}