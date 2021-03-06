package eu.nanocode.gwyddionDB;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
 
@Entity
@Table(name = "AfmImage", uniqueConstraints = {
        @UniqueConstraint(columnNames = "IMAGE_ID") })
public class AfmImage implements Serializable {	
	
	private static final long serialVersionUID = -1798070786993154676L;
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	@Id
	@Column(name = "IMAGE_ID")
	private long imageID;
	
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
	
	protected long generateID() {
		return this.imageID= this.hashCode();
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
	
	public String getCreationTimeString() {
		return creationTime.format(formatter);
	}

	public String getModificationTimeString() {
		return modificationTime.format(formatter);
	}
	
	public static enum Prefix {
		PICO("p", "pico",900e-12 , 0.0, 1.0e12),
		NANO("n", "nano",900.0e-9 , 0.9e-9, 1.0e9),
		MICRO("μ", "micro", 900.0e-6, 0.9e-6, 1.0e6),
		MILI("m", "mili", 900.0e-3, 0.9e-3, 1.0e3),
		NON("", "", 900.0  , 0.9, 1.0),
		KILO("k", "kilo", 900e3  , 0.9e3, 1.0e-3),
		MEGA("M", "mega", 900e6  , 0.9e6, 1.0e-6),
		GIGA("G", "giga", 900e9  , 0.9e9, 1.0e-9),
		TERA("T", "tera", 900e12  , 0.9e12, 1.0e-12),
		PETA("P", "peta", 900e15  , 0.9e15, 1.0e-15);
		double lower, upper, scale;
		String name, symbol;
		private Prefix(String symbol, String name, double upper, double lower, double scale ) {
			this.lower = lower;
			this.upper = upper;
			this.name = name;
			this.symbol = symbol;
			this.scale = scale;
		}
		
	}
	
	public static Prefix getPrefix(double x) {
		for(Prefix p: Prefix.values()) {
			if(x>p.lower & x<p.upper) {
				return p;
			}
		}
		return Prefix.NON;
	}
	
	public double getMinZWithPrefix() {
		return minZ * getPrefix(maxZ).scale;
	}
	public double getMaxZWithPrefix() {
		return maxZ * getPrefix(maxZ).scale;
	}
	
	public String getSi_unit_xyWithPrefix() {
		return getPrefix( (xreal>=yreal ? xreal : yreal) ).symbol + si_unit_xy;
	}

	public String getSi_unit_zWithPrefix() {
		return getPrefix(maxZ).symbol + si_unit_z;
	}	
	
	public double getXrealWithPrefix() {
		return xreal*getPrefix( (xreal>=yreal ? xreal : yreal) ).scale;
	}
	public double getYrealWithPrefix() {
		return yreal*getPrefix( (xreal>=yreal ? xreal : yreal) ).scale;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creationTime == null) ? 0 : creationTime.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		long temp;
		temp = Double.doubleToLongBits(maxZ);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minZ);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((modificationTime == null) ? 0 : modificationTime.hashCode());
		result = prime * result + ((si_unit_xy == null) ? 0 : si_unit_xy.hashCode());
		result = prime * result + ((si_unit_z == null) ? 0 : si_unit_z.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		temp = Double.doubleToLongBits(xreal);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + xres;
		temp = Double.doubleToLongBits(yreal);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + yres;
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
		AfmImage other = (AfmImage) obj;
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
		if (Double.doubleToLongBits(maxZ) != Double.doubleToLongBits(other.maxZ))
			return false;
		if (Double.doubleToLongBits(minZ) != Double.doubleToLongBits(other.minZ))
			return false;
		if (modificationTime == null) {
			if (other.modificationTime != null)
				return false;
		} else if (!modificationTime.equals(other.modificationTime))
			return false;
		if (si_unit_xy == null) {
			if (other.si_unit_xy != null)
				return false;
		} else if (!si_unit_xy.equals(other.si_unit_xy))
			return false;
		if (si_unit_z == null) {
			if (other.si_unit_z != null)
				return false;
		} else if (!si_unit_z.equals(other.si_unit_z))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (Double.doubleToLongBits(xreal) != Double.doubleToLongBits(other.xreal))
			return false;
		if (xres != other.xres)
			return false;
		if (Double.doubleToLongBits(yreal) != Double.doubleToLongBits(other.yreal))
			return false;
		if (yres != other.yres)
			return false;
		return true;
	}
	
}
