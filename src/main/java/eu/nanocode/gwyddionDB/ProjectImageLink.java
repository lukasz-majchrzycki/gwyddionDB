package eu.nanocode.gwyddionDB;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ProjectImageLink")
public class ProjectImageLink {
	
	@Column(name = "IMAGE_ID")
	private long imageID;

	@Column(name = "PROJECT_ID")
	private long projectID;

	public ProjectImageLink(long imageID, long projectID) {
		super();
		this.imageID = imageID;
		this.projectID = projectID;
	}

	public long getImageID() {
		return imageID;
	}

	public long getProjectID() {
		return projectID;
	}
}
