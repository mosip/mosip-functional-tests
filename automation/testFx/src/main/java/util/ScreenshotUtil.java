package util;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;

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
	 * 
	 */
	public static void takeScreenshot() {
		
		BufferedImage image = null;
		
		try {
			image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		} catch (HeadlessException | AWTException e1) {
			e1.printStackTrace();
		}
		try {
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
			ImageIO.write(image, "jpeg", new File(System.getProperty("user.dir")+"\\Screenshots\\"+"_failure"+timeStamp+".jpeg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
