package eu.nanocode.gwyddionDB;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;

@SuppressWarnings("unchecked")
public class GwyddionDbConn implements AfmDBConnection{
	Session session;
	
	public long countImages() {
		List<Long> i = session.createQuery("select count(o) from " + AfmImage.class.getName() + " o").getResultList();
		return i.get(0);
	}
	
	public long countProjects() {
		List<Long> i = session.createQuery("select count(o) from " + ProjectItem.class.getName() + " o").getResultList();
		return i.get(0);
	}
	
	public long countLinks() {
		List<Long> i = session.createQuery("select count(o) from " + ProjectImageLink.class.getName() + " o").getResultList();
		return i.get(0);
	}
	
	public GwyddionDbConn(Session session) {
		this.session = session;
	}

	@Override
	public AfmImage getAfmImage(long imageID) {
		List<AfmImage> obj;
		obj = session.createQuery("select o from " + AfmImage.class.getName() + " o where o.imageID='" + imageID + "'").getResultList();
		return obj.get(0);
	}

	@Override
	public List<AfmImage> getAll(long projectId) {
		List<AfmImage> list;
		list = session.createQuery(	"select o from " + AfmImage.class.getName() + " o join " +
									ProjectImageLink.class.getName() + " p "
											+ " ON o.imageID=p.imageID where p.projectID='"+ projectId +"'").getResultList();
		return list;
	}

	private AfmImage saveImage(long projectId, AfmImage afmImage) {
		long id = afmImage.generateID();
		session.save(afmImage);
		session.save(new ProjectImageLink(id,projectId));
		return afmImage;
	}
	@Override
	public AfmImage sendAfmImage(long projectId, AfmImage afmImage) {
		AfmImage img = this.saveImage(projectId, afmImage);
		session.getTransaction().commit();
		return img;
	}

	@Override
	public List<AfmImage> sendAll(long projectId, List<AfmImage> afmImageList) {
		List<AfmImage> newList= new ArrayList<>();
		for(AfmImage afmImage : afmImageList) {
			newList.add(this.saveImage(projectId, afmImage) );
		}
		session.getTransaction().commit();
		return newList;
	}

	@Override
	public List<ProjectItem> getProjectList() {
		List<ProjectItem> list;
		list = session.createQuery("select o from " + ProjectItem.class.getName() + " o").getResultList();
		return list;
	}

	@Override
	public ProjectItem addProject(String projectName) {
		ProjectItem proj = new ProjectItem(projectName);
		long id = proj.generateID();
		session.save(proj);
		session.getTransaction().commit();
		return proj;
	}
	
	public boolean removeAllImagesFromProject(long projectId) {
		List<AfmImage> imgList = session.createQuery("select o from " + AfmImage.class.getName() + " o join " +
				ProjectImageLink.class.getName() + " p "
				+ " ON o.imageID=p.imageID where p.projectID='"+ projectId +"'").getResultList();
		for(AfmImage img :imgList) {
			session.createQuery("delete AfmImage  where imageID="+img.getImageID()).executeUpdate();
		}
		return true;
	}

	@Override
	public ProjectItem removeProject(long projectId) {
		List<ProjectItem> proj = session.createQuery("select o from " + ProjectItem.class.getName() + " o where projectID="+projectId).getResultList();
		this.removeAllImagesFromProject(projectId);
		session.createQuery("delete ProjectItem  where projectID="+projectId).executeUpdate();
		session.createQuery("delete ProjectImageLink  where projectID="+projectId).executeUpdate();
		return proj.get(0) ;
	}

	@Override
	public boolean removeImage(long imageId) {
		session.createQuery("delete AfmImage  where imageID="+imageId).executeUpdate();
		session.createQuery("delete ProjectImageLink  where imageID="+imageId).executeUpdate();
		return true;
	}

	@Override
	public boolean changeImageProject(long imageId, long oldProjectId, long newProjectId) {
		session.createQuery("delete ProjectImageLink  where imageID="+imageId+ " AND projectID="+oldProjectId).executeUpdate();
		session.save(new ProjectImageLink(imageId,newProjectId));
		return true;
	}

	@Override
	public long getImageCount(long projectId) {
		List<Long> i = session.createQuery("select count(o) from " + AfmImage.class.getName() + " o join " +
											ProjectImageLink.class.getName() + " p "
											+ " ON o.imageID=p.imageID where p.projectID='"+ projectId +"'").getResultList();
		return i.get(0);
	}

}
