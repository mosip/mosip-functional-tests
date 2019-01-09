package util;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

/**
 * Implementaion for taking screenshot
 * 
 * @author M1044288
 *
 */
public class ScreenshotUtil {
	
	/**
	 * 
	 * Take screenshot of screen size
	 * File Name as based on timestamp
	 * Image is of .jpeg format
	 * @throws IOException 
	 * 
	 */
	public static String takeScreenshot() throws IOException {
		String encodstring="";
		BufferedImage image = null;
		
		try {
			
			try {
				image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			File outputfile = new File("outputImage.jpg");
			ImageIO.write(image, "jpg", outputfile);
			encodstring = encodeFileToBase64Binary(outputfile); 
		} catch (HeadlessException e1) {
			e1.printStackTrace();
		}
		try {
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
			ImageIO.write(image, "jpeg", new File(System.getProperty("user.dir")+"\\failureImages\\"+"failure"+timeStamp+".jpeg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return encodstring;
	}
	private static String encodeFileToBase64Binary(File file){
        String encodedfile = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            encodedfile = Base64.encodeBase64(bytes).toString();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return encodedfile;
    }
}
