package eu.nanocode.gwyddionDB;

import java.util.List;

public interface AfmDBConnectin {
	
	public AfmImage getAfmImage(String projectName, long imageID);
	public List<AfmImage> getAll(String projectName);
	
	public long sendAfmImage(String projectName, AfmImage afmImage);
	public boolean sendAll(String projectName, List<AfmImage> afmImageList);
	
	public List<ProjectItem> getProjectList();
	public boolean addProject (String projectName);
	public boolean removeProject (String projectName);
}
