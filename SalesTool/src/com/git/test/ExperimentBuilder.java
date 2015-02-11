/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sra.biotech.submittool.cdm.builder;
import java.util.Date;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import com.sra.biotech.submittool.cdm.experiment.Experiment;
import com.sra.biotech.submittool.cdm.experiment.Library;
import com.sra.biotech.submittool.cdm.experiment.Platform;
import com.sra.biotech.submittool.cdm.experiment.Run;
import com.sra.biotech.submittool.cdm.experiment.DataBlock;
import com.sra.biotech.submittool.cdm.experiment.File;
import com.sra.biotech.submittool.cdm.sample.Sample;
import com.sra.biotech.submittool.cdm.study.Study;
import com.sra.biotech.submittool.cdm.experiment.TypeLibraryLayouts;
import com.sra.biotech.submittool.cdm.experiment.TypeFileTypes;
import com.sra.biotech.submittool.cdm.experiment.TypeLibrarySelections;
import com.sra.biotech.submittool.cdm.experiment.TypeLibrarySources;
import com.sra.biotech.submittool.cdm.experiment.TypeLibraryStrategies;
import com.sra.biotech.submittool.cdm.adapters.AttributeTypeLibraryLayoutsDeserializer;
import com.sra.biotech.submittool.cdm.adapters.AttributeTypeLibrarySelectionsDeserializer;
import com.sra.biotech.submittool.cdm.adapters.AttributeTypeLibrarySourcesDeserializer;
import com.sra.biotech.submittool.cdm.adapters.AttributeTypeLibraryStrategiesDeserializer;
import com.sra.biotech.submittool.cdm.adapters.AttributeTypeFileTypesDeserializer;
import java.util.List;

/**
 *
 * @author hix3
 */
public class ExperimentBuilder {
    private String alias;
    private String  studyAlias;
    private String sampleAlias;
    private List<Run> runs;
    private String designDescription;
    private Platform platform;
    private Library library;
    public ExperimentBuilder alias(String alias) {
        this.alias = alias;
        return this;
    }
    /*
    public ExperimentBuilder study(Study study) {
        this.study = study;
        return this;
    }

    public ExperimentBuilder sample(Sample sample) {
        this.sample = sample;
        return this;
    }
    */

    public ExperimentBuilder runs(List<Run> runs) {
        this.runs = runs;
        return this;
    }

    public ExperimentBuilder designDescription(String designDescription) {
        this.designDescription = designDescription;
        return this;
    }

    public ExperimentBuilder platform(Platform platform) {
        this.platform = platform;
        return this;
    }

    public ExperimentBuilder library(Library library) {
        this.library = library;
        return this;
    }
    public Experiment build() {
        Experiment instance = new Experiment();

        instance.setAlias(alias);
        instance.setStudyAlias(studyAlias);
        instance.setSampleAlias(sampleAlias);
        instance.setRuns(runs);
        instance.setDesignDescription(designDescription);
        instance.setPlatform(platform);
        instance.setLibrary(library);

        return instance;
    }
    public Experiment buildFromJson(String json) {
		//System.out.println(" Experiment buildFromJson = " + json);
	
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(TypeLibraryLayouts.class, new AttributeTypeLibraryLayoutsDeserializer());
	   	gsonBuilder.registerTypeAdapter(TypeLibrarySelections.class, new AttributeTypeLibrarySelectionsDeserializer());
	   	gsonBuilder.registerTypeAdapter(TypeLibrarySources.class, new AttributeTypeLibrarySourcesDeserializer());
	   	gsonBuilder.registerTypeAdapter(TypeLibraryStrategies.class, new AttributeTypeLibraryStrategiesDeserializer());
	   	gsonBuilder.registerTypeAdapter(TypeFileTypes.class, new AttributeTypeFileTypesDeserializer());
	   	 
		Gson gson = gsonBuilder.create();

		Experiment instance = null;
		JsonReader reader = null;
		try {
			reader = new JsonReader(new StringReader(json));

		} catch (Exception e) {
			// TODO: handle exception
		}
		
		try {

			// instance = gson.fromJson(new FileReader("data/study.json"),
			// Experiment.class);
		
			Library library = new LibraryBuilder().buildFromJson(json);
			 
			instance = gson.fromJson(reader, Experiment.class);
			Run run = new Run();
			//List<File> files = new FileBuilder().buildFromGson(gson);
			//List<File> files =instance.getFiles();
			DataBlock dataBlock = new DataBlockBuilder().buildFromJson(json);
			for (File file : dataBlock.getFiles())
			{
				file.setType(TypeFileTypes.FASTQ);
			}
			//dataBlock.setFiles(files);
			run.getDataBlocks().add(dataBlock);
			instance.getRuns().add(run);
			instance.setLibrary(library);
			final String jsonStr = gson.toJson(instance);
			JsonParser parser = new JsonParser();
			JsonObject json2 = parser.parse(jsonStr).getAsJsonObject();

			Gson gson2 = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
			String prettyJson = gson.toJson(json2);
			System.out.println("Deserializer " + " Experiment");
			//System.out.println(prettyJson);
			 

		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return instance;
	}

}

