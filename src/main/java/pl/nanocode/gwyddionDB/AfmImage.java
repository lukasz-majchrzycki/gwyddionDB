package pl.nanocode.gwyddionDB;

import java.util.ArrayList;

public class AfmImage {	
	public String title;
	private double minZ, maxZ;		//z-scale min and max
	private double xreal, yreal;	//horizontal and vertical dimensions in physical units
	protected String si_unit_xy, si_unit_z;	//unit of lateral and z-scale dimensions
	
	protected ArrayList<Double> afmMap;		//z-scale data
	private int xres, yres;	//horizontal and vertical size in pixels
	
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
}
