package eu.nanocode.gwyddionDB;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
 
@Entity
@Table(name = "AfmImage", uniqueConstraints = {
        @UniqueConstraint(columnNames = "IMAGE_ID") })
public class AfmImage implements Serializable {	
	
	private static final long serialVersionUID = -1798070786993154676L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "IMAGE_ID")
	private long imageID;
	
	@Column(name = "PROJECTS_IDS", unique = false, nullable = true)
	private ArrayList<Long> ProjectsIDs;
	
	@Column(name = "TITLE", unique = false, nullable = true, length = 100)
	public String title;
	
	//z-scale min and max
	@Column(name = "MIN_Z", unique = false, nullable = false)
	private double minZ;
	
	@Column(name = "MAX_Z", unique = false, nullable = false)
	private double maxZ;		
	
	//horizontal and vertical dimensions in physical units	
	@Column(name = "X_REAL", unique = false, nullable = false)
	private double xreal;
	
	@Column(name = "Y_REAL", unique = false, nullable = false)
	private double yreal;	
	
	//unit of lateral and z-scale dimensions
	@Column(name = "SI_UNIT_XY", unique = false, nullable = true, length = 100)
	protected String si_unit_xy;
	
	@Column(name = "SI_UNIT_Z", unique = false, nullable = true, length = 100)
	protected String si_unit_z;	
	
	@Column(name = "CREATION_TIME", unique = false, nullable = false)
	private LocalDateTime creationTime;
	
	@Column(name = "MODIFICATION_TIME", unique = false, nullable = false)
	private LocalDateTime modificationTime;
	
	@Column(name = "DESCRIPTION", unique = false, nullable = true, length = 500)
	public String description;
	
	//z-scale data
	@Column(name = "AFM_MAP", unique = false, nullable = false, length = 4194304)
	protected ArrayList<Double> afmMap;	
	
	//horizontal and vertical size in pixels
	@Column(name = "X_RES", unique = false, nullable = false)
	private int xres;	
	
	@Column(name = "Y_RES", unique = false, nullable = false)
	private int yres;
	
	public AfmImage() {
		this.creationTime = LocalDateTime.now();
		this.modificationTime = LocalDateTime.now();
		this.description="";
	}

	/*getters and setters*/
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSi_unit_xy() {
		return si_unit_xy;
	}
	public void setSi_unit_xy(String si_unit_xy) {
		this.si_unit_xy = si_unit_xy;
	}
	public String getSi_unit_z() {
		return si_unit_z;
	}
	public void setSi_unit_z(String si_unit_z) {
		this.si_unit_z = si_unit_z;
	}
	public double getMinZ() {
		return minZ;
	}
	public double getMaxZ() {
		return maxZ;
	}
	public double getXreal() {
		return xreal;
	}
	public double getYreal() {
		return yreal;
	}
	public int getXres() {
		return xres;
	}
	public int getYres() {
		return yres;
	}
	public void setMinZ(double minZ) {
		this.minZ = minZ;
	}
	public void setMaxZ(double maxZ) {
		this.maxZ = maxZ;
	}
	public void setXreal(double xreal) {
		this.xreal = xreal;
	}
	public void setYreal(double yreal) {
		this.yreal = yreal;
	}
	public void setXres(int xres) {
		this.xres = xres;
	}
	public void setYres(int yres) {
		this.yres = yres;
	}	
	public long getImageID() {
		return imageID;
	}

	public LocalDateTime getCreationTime() {
		return creationTime;
	}

	public LocalDateTime getModificationTime() {
		return modificationTime;
	}
	public void changeModificationTime() {
		this.modificationTime = LocalDateTime.now();
	}
}
