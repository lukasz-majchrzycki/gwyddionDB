package eu.nanocode.gwyddionDB;

import java.util.List;

public interface AfmDBConnection {
	
	public AfmImage getAfmImage(long imageID);
	public List<AfmImage> getAll(long projectId);
	
	public long sendAfmImage(long projectId, AfmImage afmImage);
	public boolean sendAll(long projectId, List<AfmImage> afmImageList);
	
	public List<ProjectItem> getProjectList();
	public long addProject (String projectName);
	public boolean removeProject (long projectId);
	public boolean removeImage (long imageId);
}
