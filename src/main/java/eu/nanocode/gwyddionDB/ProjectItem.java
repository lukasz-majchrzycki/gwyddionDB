package eu.nanocode.gwyddionDB;

import java.net.URI;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "ProjectItem", uniqueConstraints = {
        @UniqueConstraint(columnNames = "PROJECT_ID") })
public class ProjectItem {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "PROJECT_ID")
	private long projectID;
	
	@Column(name = "PROJECT_NAME", unique = true, nullable = false, length = 100)
	private String projectName;

	@Column(name = "CREATION_TIME", unique = false, nullable = false)
	private LocalDateTime creationTime;
	
	@Column(name = "MODIFICATION", unique = false, nullable = false)
	private LocalDateTime modificationTime;
	
	@Column(name = "DESCRIPTION", unique = false, nullable = true, length = 500)
	public String description;
	
	@Column(name = "DESTINATION", unique = false, nullable = true, length = 500)
	public URI destination;
	
	public ProjectItem(String projectName) {
		this.projectName = projectName;
		this.creationTime = LocalDateTime.now();
		this.modificationTime = LocalDateTime.now();
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

	public long getProjectID() {
		return projectID;
	}
	
	public void changeModificationTime() {
		this.modificationTime = LocalDateTime.now();
	}
	
	
}
