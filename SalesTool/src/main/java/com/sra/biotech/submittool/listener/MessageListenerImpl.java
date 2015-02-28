package com.sra.biotech.submittool.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

import com.sra.biotech.submittool.cdm.Cdm;
import com.sra.biotech.submittool.cdm.Submission;
import com.sra.biotech.submittool.cdm.builder.CdmBuilder;
import com.sra.biotech.submittool.cdm.builder.SubmissionBuilder;
import com.sra.biotech.submittool.cdm.experiment.Experiment;
import com.sra.biotech.submittool.cdm.experiment.Run;
import com.sra.biotech.submittool.cdm.experiment.File;
import com.sra.biotech.submittool.cdm.experiment.DataBlock;
 
import com.sra.biotech.submittool.persistence.service.BatchDeliveryInfoService;
import com.sra.biotech.submittool.listener.helper.StudyGeneratorHelper;
import com.sra.biotech.submittool.listener.helper.RunGeneratorHelper;
import com.sra.biotech.submittool.listener.helper.SampleGeneratorHelper;
import com.sra.biotech.submittool.listener.helper.SubmissionGeneratorHelper;
import com.sra.biotech.submittool.model.BatchInfo;
import com.sra.biotech.submittool.service.ISraXmlFileGeneratorService;
import com.sra.biotech.submittool.service.SraXmlFileGeneratorServiceImpl;
import com.sra.biotech.submittool.service.SraOneStopTemplateEngine;
import com.sra.biotech.submittool.service.ISraOneStopXmlFileGeneratorService;
import com.sra.biotech.submittool.service.SraTemplates;
//import com.sra.biotech.submittool.service.util.DateUtil;
import com.sra.biotech.submittool.util.DateUtils;
import com.sra.biotech.submittool.util.properties.ISubmitProperties;

 


@Service
public class MessageListenerImpl implements MessageListener

{
	
	@Autowired
	
	//private ISraXmlFileGeneratorService sraXmlFileGeneratorService;
        private ISraOneStopXmlFileGeneratorService sraXmlFileGeneratorService;
	@Autowired
	private ISubmitProperties iSubmitProperties;
	@Autowired
	private BatchDeliveryInfoService batchDeliveryInfoService;
    private static final Logger logger = LoggerFactory.getLogger( MessageListenerImpl.class );
    
    public void processMessage( String message )
    {
    
    	
        //System.out.println( "MessageListener::::::Received message: " + message );
        
        sraXmlFileGeneratorService.setStagingDir(iSubmitProperties.getStagingDir());
        System.out.println("Staging Library "  + iSubmitProperties.getStagingDir());
       // String batchDate = DateUtils.formatSubmitDate();
        Cdm cdm = new CdmBuilder().buildFromJson((String)message);
        //sraXmlFileGeneratorService = new SraOneStopTemplateEngine(cdm);
        System.out.println("Generator service started. CDM passed");
        
       // String submissionXml = sraXmlFileGeneratorService.generateSubmissions(cdm);
        
      //  String submissionXml = new SubmissionGeneratorHelper(sraXmlFileGeneratorService, cdm, batchDate).generateSubmission(); 
        
    }
  
}
