package eu.nanocode.gwyddionDB;

import java.util.List;

public interface AfmDBConnection {
	
	public AfmImage getAfmImage(long imageID);
	public List<AfmImage> getAll(long projectId);
	
	public AfmImage sendAfmImage(long projectId, AfmImage afmImage);
	public List<AfmImage> sendAll(long projectId, List<AfmImage> afmImageList);
	
	public boolean changeImageProject(long imageId, long oldProjectId, long newProjectId);
	
	public List<ProjectItem> getProjectList();
	public ProjectItem addProject (String projectName);
	public ProjectItem removeProject (long projectId);
	public boolean removeImage (long imageId);
	
	public long getImageCount(long projectId);
}
