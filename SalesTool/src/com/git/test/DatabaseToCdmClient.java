/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sra.biotech.submittool.persistence.client;

import com.sra.biotech.submittool.cdm.builder.CdmBuilder;
import com.sra.biotech.submittool.entity.Sample;
import com.sra.biotech.submittool.entity.Study;
import com.sra.biotech.submittool.entity.Submission;
import com.sra.biotech.submittool.entity.experiment.DataBlock;
import com.sra.biotech.submittool.entity.experiment.Experiment;
import com.sra.biotech.submittool.entity.experiment.File;
import com.sra.biotech.submittool.entity.experiment.Library;
import com.sra.biotech.submittool.entity.experiment.Platform;
import com.sra.biotech.submittool.entity.experiment.Run;
import com.sra.biotech.submittool.persistence.resource.DataBlockResource;
import com.sra.biotech.submittool.persistence.resource.DataBlockResources;
import com.sra.biotech.submittool.persistence.resource.ExperimentResource;
import com.sra.biotech.submittool.persistence.resource.ExperimentResources;
import com.sra.biotech.submittool.persistence.resource.FileResource;
import com.sra.biotech.submittool.persistence.resource.FileResources;
import com.sra.biotech.submittool.persistence.resource.RunResource;
import com.sra.biotech.submittool.persistence.resource.RunResources;
import com.sra.biotech.submittool.persistence.resource.SampleResource;
import com.sra.biotech.submittool.persistence.resource.SampleResources;
import com.sra.biotech.submittool.persistence.resource.StudyResources;
import com.sra.biotech.submittool.persistence.resource.SubmissionResource;
import com.sra.biotech.submittool.persistence.resource.SubmissionResources;
import com.sra.biotech.submittool.persistence.service.DataBlockService;
import com.sra.biotech.submittool.persistence.service.DataBlockServiceImpl;
import com.sra.biotech.submittool.persistence.service.ExperimentService;
import com.sra.biotech.submittool.persistence.service.ExperimentServiceImpl;
import com.sra.biotech.submittool.persistence.service.FileService;
import com.sra.biotech.submittool.persistence.service.FileServiceImpl;
import com.sra.biotech.submittool.persistence.service.LibraryService;
import com.sra.biotech.submittool.persistence.service.LibraryServiceImpl;
import com.sra.biotech.submittool.persistence.service.PlatformService;
import com.sra.biotech.submittool.persistence.service.PlatformServiceImpl;
import com.sra.biotech.submittool.persistence.service.RestTemplateService;
import com.sra.biotech.submittool.persistence.service.RestTemplateServiceImpl;
import com.sra.biotech.submittool.persistence.service.RunService;
import com.sra.biotech.submittool.persistence.service.RunServiceImpl;
import com.sra.biotech.submittool.persistence.service.SampleService;
import com.sra.biotech.submittool.persistence.service.SampleServiceImpl;
import com.sra.biotech.submittool.persistence.service.StudyService;
import com.sra.biotech.submittool.persistence.service.StudyServiceImpl;
import com.sra.biotech.submittool.persistence.service.SubmissionService;
import com.sra.biotech.submittool.persistence.service.SubmissionServiceImpl;
import com.sra.biotech.submittool.submitcli.GroovyExcelParser;
import java.net.URI;
import java.util.List;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

 
 /*
 * @author hix3
 */
import com.sra.biotech.submittool.persistence.service.RestTemplateServiceImpl;
import com.sra.biotech.submittool.persistence.service.RestTemplateService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import org.springframework.hateoas.Link;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

 

/**
 *
 * @author Calvin
 */
public class DatabaseToCdmClient  {
     private SubmissionService submissionService;
     private RestTemplateService restTemplateService;
     RestTemplate restTemplate;
     public DatabaseToCdmClient ()
     {
        
         restTemplateService = new RestTemplateServiceImpl();
         submissionService = new SubmissionServiceImpl();
         restTemplate = restTemplateService.restTemplate();
     }        
        
     public List<Submission> findByStatus( String status) {
                RestTemplate restTemplate = restTemplateService.restTemplate();
                SubmissionResources resources = restTemplate.getForObject(RestClientConfiguration.BASE_URL + "/submissions/search/findByStatus/?status={status}", SubmissionResources.class,status);
                  
		if (resources == null || CollectionUtils.isEmpty(resources.getContent())) {
			return Collections.emptyList();
		}
             
                // getting the author's books using the link with rel books
                Submission submission =null;
                List<Submission> submissions = new ArrayList<Submission>();
                Link submissionSamplesLink=null;
                Link submissionStudiesLink=null;
                Link submissionExperimentsLink=null;
               // Collection<SampleResource> submissionSamples=null;
                SampleResources sampleResources=null;
                StudyResources studyResources=null;
                ExperimentResources experimentsResources=null;
                for (SubmissionResource resource : resources) {
                         submission = resource.getContent();
                         /* Get Samples */
                         submissionSamplesLink = resource.getLink("samples");
                         sampleResources = restTemplate.getForObject(submissionSamplesLink.getHref(), SampleResources.class);
                         submission.setSamples(sampleResources.unwrap());
                         /* Get Studies   */
                        submissionStudiesLink = resource.getLink("studies");
                        studyResources = restTemplate.getForObject(submissionStudiesLink.getHref(), StudyResources.class);
                        submission.setStudies(studyResources.unwrap());
                        /* Get Experiments  */
                        submissionExperimentsLink = resource.getLink("experiments");
                        experimentsResources = restTemplate.getForObject(submissionExperimentsLink.getHref(), ExperimentResources.class);
                      //  submission.setExperiments(experimentsResources.unwrap());
                        submission.setExperiments( findAllExperiments(submissionExperimentsLink));
			submissions.add(submission);
		}
       
		return resources.unwrap();
    }
     
    public List<Experiment> findAllExperiments(  Link submissionExperimentsLink ) {
        Experiment experiment =null;
        Run run =null;
        DataBlock dataBlock=null;
        File file =null;
        Link experimentDataBlocksLink=null;
        Link experimentRunsLink=null;
        Link datablockFilesLink=null;
        RunResources runResources=null;
        FileResources fileResources=null;
        DataBlockResources dataBlockResources=null;
        List<Experiment> experiments = new ArrayList<Experiment>();
        List<Run> runs = new ArrayList<Run>();
        List<DataBlock> dataBlocks = new ArrayList<DataBlock>();
        List<File> files = new ArrayList<File>();
         
        ExperimentResources experimentResources = restTemplate.getForObject(submissionExperimentsLink.getHref(), ExperimentResources.class);
        if (experimentResources == null || CollectionUtils.isEmpty(experimentResources.getContent())) {
                return Collections.emptyList();
        }
        for ( ExperimentResource experimentResource : experimentResources) {
             experiment = experimentResource.getContent();
             /* Get Run */
             experimentRunsLink=experimentResource.getLink("runs");
             runResources = restTemplate.getForObject(experimentRunsLink.getHref(), RunResources.class);
             for (RunResource runResource :  runResources) {
                  /* Get Datablocks */
                run = runResource.getContent();
                runs.add(run);
                experimentDataBlocksLink=runResource    .getLink("dataBlocks");
                dataBlockResources = restTemplate.getForObject(experimentDataBlocksLink.getHref(), DataBlockResources.class);
                for (DataBlockResource resource : dataBlockResources) {
                    dataBlock = resource.getContent(); 
                    datablockFilesLink=resource.getLink("files");
                    fileResources = restTemplate.getForObject(datablockFilesLink.getHref(), FileResources.class);
                    for (FileResource fileResource : fileResources) {
                        dataBlock.getFiles().add(fileResource.getContent());
                    }
                    run.getDataBlocks().add(dataBlock);
                 }
                 
                 
             } /* End Of run resources */ 
            experiment.setRuns(runs);
            experiments.add(experiment);
        }
              
       
        return experiments;
       
 	//return experimentResources.unwrap();
    }
     

     public static void main(String args[]) {
	       
            DatabaseToCdmClient  app =  new DatabaseToCdmClient ();
            final String submissionUrl = RestClientConfiguration.BASE_URL + "/"  + "submissions";
            final String studiesUrl = RestClientConfiguration.BASE_URL + "/" + "studies";
           
            List<Submission> submits = app.findByStatus("created");
             
            System.out.println("submissions. " + new CdmBuilder().prettyPrint(submits));
		 
    }
     
    
}
