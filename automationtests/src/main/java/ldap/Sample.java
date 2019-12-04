/*package io.mosip.ldap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder;

import javax.swing.JOptionPane;

public class Sample {
	public void runJar()
	{
		try {
	          Runtime runtime = Runtime.getRuntime();
	          runtime.exec("./jar/PreRegE2E.jar");
	      } catch (Exception ex) {
	          JOptionPane.showMessageDialog(null, "Exception occured" + ex);
	      }
	}
	public static void main(String[] args) throws IOException, InterruptedException  {
		//ProcessBuilder pb = new ProcessBuilder("java", "-jar", "./jar/PreRegE2E.jar");
		ProcessBuilder pb= new ProcessBuilder("java", "-jar", "./jar/PreRegE2E.jar");
		Process p = null;
		try {
			p = pb.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			 JOptionPane.showMessageDialog(null, "Exception occured" + e);
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String s = "";
		while((s = in.readLine()) != null){
		    System.out.println(s);
		}
		int status = p.waitFor();
		System.out.println("Exited with status: " + status);
	}
	
	
	
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-ldap</artifactId>
		</dependency>
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.ldap</groupId>
			<artifactId>spring-ldap</artifactId>
			<version>1.2.1</version>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-dao</artifactId>
			<version>2.0.8</version>
		</dependency>
		<dependency>
			<groupId>org.apache.directory.api</groupId>
			<artifactId>api-all</artifactId>
			<version>2.0.0.AM2</version>
		</dependency>
		<!-- https://mvnrepository.com/artifact/commons-pool/commons-pool -->
		<dependency>
			<groupId>commons-pool</groupId>
			<artifactId>commons-pool</artifactId>
		</dependency>



}
*/