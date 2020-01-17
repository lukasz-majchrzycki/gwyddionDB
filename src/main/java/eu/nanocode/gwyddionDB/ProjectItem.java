package eu.nanocode.gwyddionDB;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "ProjectItem", uniqueConstraints = {
        @UniqueConstraint(columnNames = "PROJECT_ID") })
public class ProjectItem {
	@Id
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
	
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	public ProjectItem() {
		this.creationTime = LocalDateTime.now();
		this.modificationTime = LocalDateTime.now();
	}
	
	public ProjectItem(String projectName) {
		this();
		this.projectName = projectName;

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
	
	public String getCreationTimeString() {
		return creationTime.format(formatter);
	}

	public String getModificationTimeString() {
		return modificationTime.format(formatter);
	}

	public long getProjectID() {
		return projectID;
	}
	
	public void changeModificationTime() {
		this.modificationTime = LocalDateTime.now();
	}
	
	protected long generateID() {
		return this.projectID= this.hashCode();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creationTime == null) ? 0 : creationTime.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((modificationTime == null) ? 0 : modificationTime.hashCode());
		result = prime * result + ((projectName == null) ? 0 : projectName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectItem other = (ProjectItem) obj;
		if (creationTime == null) {
			if (other.creationTime != null)
				return false;
		} else if (!creationTime.equals(other.creationTime))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (modificationTime == null) {
			if (other.modificationTime != null)
				return false;
		} else if (!modificationTime.equals(other.modificationTime))
			return false;
		if (projectName == null) {
			if (other.projectName != null)
				return false;
		} else if (!projectName.equals(other.projectName))
			return false;
		return true;
	}
	
}
