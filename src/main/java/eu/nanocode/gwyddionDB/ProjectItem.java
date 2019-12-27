package eu.nanocode.gwyddionDB;

import java.time.LocalDateTime;

public class ProjectItem {
	private String projectName;
	private LocalDateTime creationTime;
	private LocalDateTime modificationTime;
	private int imagesNo;
	private long projectID;
	
	public ProjectItem(String projectName) {
		this.projectName = projectName;
		this.creationTime = LocalDateTime.now();
		this.modificationTime = LocalDateTime.now();
		this.imagesNo = 0;
		this.projectID = ( (long) this.creationTime.hashCode() )+ ( ((long) this.projectName.hashCode()) <<32 );
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public LocalDateTime getCreationTime() {
		return creationTime;
	}

	public LocalDateTime getModificationTime() {
		return modificationTime;
	}

	public int getImagesNo() {
		return imagesNo;
	}

	public long getProjectID() {
		return projectID;
	}
	
	public void changeModificationTime() {
		this.modificationTime = LocalDateTime.now();
	}
	
	
}
