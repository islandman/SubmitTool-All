package com.sra.biotech.submittool.cdm.experiment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sra.biotech.submittool.cdm.sample.Sample;
import com.sra.biotech.submittool.cdm.study.Study;

public class Experiment implements Serializable {
	private String alias =""; 
	private String studyAlias="";
	private String sampleAlias="";		
	private List<Run> runs = new ArrayList<Run>();
	private String designDescription="";
	private Platform platform = new Platform();
	private Library library = new Library();
        private List<Platform> platforms = new ArrayList<Platform>();
        private List<Library> libraries = new ArrayList<Library>();

  

	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public List<Run> getRuns() {
		return runs;
	}
	public void setRuns(List<Run> runs) {
		this.runs = runs;
	}
	public String getDesignDescription() {
		return designDescription;
	}
	public void setDesignDescription(String designDescription) {
		this.designDescription = designDescription;
	}
	public Platform getPlatform() {
		return platform;
	}
	public void setPlatform(Platform platform) {
		this.platform = platform;
	}
	public Library getLibrary() {
		return library;
	}
	public void setLibrary(Library library) {
		this.library = library;
	}
	public String getStudyAlias() {
		return studyAlias;
	}
	public void setStudyAlias(String studyId) {
		this.studyAlias = studyId;
	}
	public String getSampleAlias() {
		return sampleAlias;
	}
	public void setSampleAlias(String sampleId) {
		this.sampleAlias= sampleId;
	}
        public List<Platform> getPlatforms() {
            return platforms;
        }

        public void setPlatforms(List<Platform> platforms) {
            this.platforms = platforms;
        }

        public List<Library> getLibraries() {
            return libraries;
        }

        public void setLibraries(List<Library> libraries) {
            this.libraries = libraries;
        }
	
}
