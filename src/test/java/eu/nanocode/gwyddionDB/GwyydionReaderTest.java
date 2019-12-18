package eu.nanocode.gwyddionDB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GwyydionReaderTest 
{
	private List<AfmImage> afmImageList;
	
	@BeforeEach
	void init() {
		afmImageList=null;
	}
	private File getFileFromResources(String fileName) {

        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return new File(resource.getFile());
        }
	}
	
	@Test
	public void testWrongFileExceptions() {
		assertThrows(IllegalArgumentException.class, ()->{
				afmImageList = new GwyddionReader().readAfmFile(this.getFileFromResources("IllegalArgumentException.gwy"));
		});
		
		assertThrows(IllegalArgumentException.class, ()->{
			afmImageList = new GwyddionReader().readAfmFile(this.getFileFromResources("IllegalArgumentException2.gwy"));
		});
	}
	
	@Test
    public void readingDataTest()
    {
		Scanner sc=null;
		int i;
    	File file = this.getFileFromResources("test.gwy");
       	try {
			afmImageList = new GwyddionReader().readAfmFile(file);
		} catch (IOException e) {
			fail();
			e.printStackTrace();
		}
       
       	assertEquals(2, afmImageList.size() );
       	assertEquals("Scaled Data 1", afmImageList.get(0).getTitle());
       	assertEquals("Detail 2", afmImageList.get(1).getTitle());
       	
       	assertEquals(8, afmImageList.get(0).getXres());		//pixel resolution of images
       	assertEquals(8, afmImageList.get(0).getYres());
       	assertEquals(8, afmImageList.get(1).getXres());
       	assertEquals(4, afmImageList.get(1).getYres());
       	
       	assertEquals(64,afmImageList.get(0).afmMap.size());
       	assertEquals(32,afmImageList.get(1).afmMap.size());
       	
       	assertEquals(10.0E-6, afmImageList.get(0).getXreal());	//real resolution of images
       	assertEquals(10.0E-6, afmImageList.get(0).getYreal());
       	assertEquals(10.0E-6, afmImageList.get(1).getXreal());
       	assertEquals(5.0E-6, afmImageList.get(1).getYreal());
       	
    	assertEquals(0.0, afmImageList.get(0).getMinZ());		//z-scale min and max
    	assertEquals(4.199999999999975E-9, afmImageList.get(0).getMaxZ());
    	assertEquals(6.444390992906632E-10, afmImageList.get(1).getMinZ());
    	assertEquals(2.836869881887839E-9, afmImageList.get(1).getMaxZ());
    	
    	assertEquals("m", afmImageList.get(0).si_unit_xy);		//xy and z units
    	assertEquals("m", afmImageList.get(0).si_unit_z);
    	assertEquals("m", afmImageList.get(1).si_unit_xy);
    	assertEquals("m", afmImageList.get(1).si_unit_z);
    	
    	try {
			sc = new Scanner(this.getFileFromResources("test_image1.txt"));
		} catch (FileNotFoundException e) {
			fail();
			e.printStackTrace();
		}
    	
    	for(i=0;i<afmImageList.size();i++) {
    		assertEquals(Double.parseDouble(sc.next() ),afmImageList.get(1).afmMap.get(i));
    	}
    	
    	try {
			sc = new Scanner(this.getFileFromResources("test_image2.txt"));
		} catch (FileNotFoundException e) {
			fail();
			e.printStackTrace();
		}
    	
    	for(i=0;i<afmImageList.size();i++) {
    		assertEquals(Double.parseDouble(sc.next() ),afmImageList.get(0).afmMap.get(i));
    	}
    }
}
