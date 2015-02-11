/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sra.biotech.submittool.persistence.client;

/**
 *
 * @author hix3
 */
 
import com.sra.biotech.submittool.persistence.errorhandling.GenericException;
import com.sra.biotech.submittool.persistence.service.RestTemplateServiceImpl;
import com.sra.biotech.submittool.persistence.service.RestTemplateService;

import java.util.List;
import java.net.URI;

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
import com.sra.biotech.submittool.persistence.service.RunService;
import com.sra.biotech.submittool.persistence.service.RunServiceImpl;
import com.sra.biotech.submittool.persistence.service.SampleService;
import com.sra.biotech.submittool.persistence.service.SampleServiceImpl;
import com.sra.biotech.submittool.persistence.service.StudyService;
import com.sra.biotech.submittool.persistence.service.StudyServiceImpl;
import com.sra.biotech.submittool.persistence.service.SubmissionService;
import com.sra.biotech.submittool.persistence.service.SubmissionServiceImpl;
import com.sra.biotech.submittool.submitcli.GroovyExcelParser;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Calvin
 */
@Service
public class SubmitRestPersistenceClient implements SubmitRestPersistenceIF {
     @Autowired
     private SubmissionService submissionService;
     @Autowired
     private RestTemplateService restTemplateService;
     public SubmitRestPersistenceClient()
     {
        
         restTemplateService = new RestTemplateServiceImpl();
         submissionService = new SubmissionServiceImpl();
     }        
     public void submissionToDatabase(Submission submission) throws GenericException
     {
            Submission dbSubmission=submission.build();
            URI submissionUri=null;
            if (dbSubmission !=null) {
                submissionUri = submissionService.save(dbSubmission );
            
            }
            URI studyUri=null;
            Study newStudy =null;
            StudyService studyService = new StudyServiceImpl();
           
            for (Study study : submission.getStudies() )   {
                newStudy = study.build();
               // studyUri = studyService.save(newStudy);
                submissionService.assignStudy(submissionUri,newStudy); 
                
            }
            URI sampleUri=null;
            Sample newSample =null;
            SampleService sampleService = new SampleServiceImpl();
            for (Sample sample : submission.getSamples() )   {
                newSample = sample.build();
              //  sampleUri = sampleService.save(newSample);
                submissionService.assignSample(submissionUri,newSample); 
            }
            URI experimentUri=null;
            Experiment newExperiment =null;
            ExperimentService experimentService = new ExperimentServiceImpl();
            for (Experiment experiment : submission.getExperiments() )   {
                newExperiment = experiment.build();
               // System.out.println("pretty Json experiment = " + new CdmBuilder().prettyPrint(newExperiment));
              //  experimentUri = experimentService.save(newExperiment);
                experimentUri = submissionService.assignExperiment(submissionUri,newExperiment); 
               experimentToDatabase(experiment, experimentUri, experimentService);
            }
     }   
     public void experimentToDatabase(Experiment experiment, URI experimentsUri, ExperimentService experimentService) {
         RunService runService = new RunServiceImpl();
         /* test */
         List<Run> runs = experiment.getRuns();
         Run newrun=null;
         URI run1Uri=null;
         for (Run run : runs) {
             newrun = run.build();
          //   System.out.println("pretty Json Run  = " + new CdmBuilder().prettyPrint(newrun));
           // run1Uri = runService.save(newrun);
             run1Uri  = experimentService.assignRun(experimentsUri, newrun);
             dataBlocksToDatabase(run.getDataBlocks(), run1Uri, runService);
             
         }
         Library library = experiment.getLibrary();
         Library newlib = library.build();
         URI libraryUri = null;
         if (library.build() !=null) {
           // libraryUri = libraryService.save(library.build()) ;
            libraryUri = experimentService.assignLibrary(experimentsUri, newlib);
         }  
        
         Platform platform = experiment.getPlatform();
         //PlatformService platformService = new PlatformServiceImpl(); 
         Platform newPlatform=null;
         if (platform !=null) {
             if (platform.build() !=null) {
                 // platformService.save(platform.build());
                 newPlatform=platform.build();
                 experimentService.assignPlatform(experimentsUri, newPlatform);
             }
         }
             
     }
      public void dataBlocksToDatabase(List<DataBlock> dataBlocks, URI run1Uri, RunService runService) {
         File newDataBlock=null;
         URI dataBlock1Uri=null;
         DataBlockService dataBlockService = new DataBlockServiceImpl();
         
         for (DataBlock dataBlock : dataBlocks) {
          // dataBlock1Uri = dataBlockService.save(new DataBlock());
           dataBlock1Uri = runService.assignDataBlock(run1Uri, new DataBlock());
           filesToDatabase(dataBlock.getFiles(), dataBlock1Uri, dataBlockService);
          }
      }
     public void filesToDatabase(List<File> files, URI dataBlock1Uri, DataBlockService dataBlockService ) {
         File newfile=null;
         URI file1Uri=null;
         FileService fileService = new FileServiceImpl();
         for (File file : files) {
             System.out.println("pretty files Json = " + new CdmBuilder().prettyPrint(file));
            // file1Uri =  fileService.save(file);
          dataBlockService.assignFile(dataBlock1Uri, file);
          }
     }
     public boolean   saveCdm(com.sra.biotech.submittool.cdm.Cdm cdm) throws GenericException {
	     boolean isSaved= false;  
         SubmitRestPersistenceClient app =  new SubmitRestPersistenceClient();
         final String submissionUrl = RestClientConfiguration.BASE_URL + "/"  + "submissions";
         final String studiesUrl = RestClientConfiguration.BASE_URL + "/" + "studies";
        
         
         String printStr = new CdmBuilder().prettyPrint(cdm);
      
         for (com.sra.biotech.submittool.cdm.Submission submission :  cdm.getSubmissions()) {
          
                 Mapper mapper = new DozerBeanMapper();

                 com.sra.biotech.submittool.entity.Submission destObject
                         = mapper.map(submission, com.sra.biotech.submittool.entity.Submission.class);
                 destObject.setName(submission.getContactInfo().getSubmissionName());
                 destObject.setLabName(submission.getContactInfo().getLabName());
                 
                 app.submissionToDatabase(destObject);
            }
         isSaved = true;
         return isSaved;
		 
     } 
     public static void main(String args[]) {
	       
            SubmitRestPersistenceClient app =  new SubmitRestPersistenceClient();
            final String submissionUrl = RestClientConfiguration.BASE_URL + "/"  + "submissions";
            final String studiesUrl = RestClientConfiguration.BASE_URL + "/" + "studies";
           // String path = "/projects/oidproject/hix3/data/Pertussis_SRA_submission_template_CDC_V09_dummy.xlsx";
           String path = "Pertussis_SRA_submission_template_CDC_V09.xlsx";
            GroovyExcelParser parser = new GroovyExcelParser();
            String json =  (String)parser.parse(path);
           // System.out.println(json);

            com.sra.biotech.submittool.cdm.Cdm cdm = new CdmBuilder().buildFromJson(json);
            String printStr = new CdmBuilder().prettyPrint(cdm);
            // System.out.println("pretty Json = " + printStr);
            for (com.sra.biotech.submittool.cdm.Submission submission :  cdm.getSubmissions()) {
             
                    Mapper mapper = new DozerBeanMapper();

                    com.sra.biotech.submittool.entity.Submission destObject
                            = mapper.map(submission, com.sra.biotech.submittool.entity.Submission.class);
                    destObject.setName(submission.getContactInfo().getSubmissionName());
                    destObject.setLabName(submission.getContactInfo().getLabName());
                  System.out.println("pretty Json = " + new CdmBuilder().prettyPrint(destObject));
                    try {
						app.submissionToDatabase(destObject);
					} catch (GenericException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                 
            }
		 
    }
     
     
    
}
