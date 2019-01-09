import java.io.IOException;
import java.io.OutputStream;

public class Snippet {
	public static void main(String[] args) {
		
	
	try {
	    // Execute command
		String command =
		        "java -jar registration-ui-1.0.0-SNAPSHOT.jar";
		  
		//String command = "cmd /c start cmd.exe; java -jar registration-ui-1.0.0-SNAPSHOT.jar";
	    Process child = Runtime.getRuntime().exec(command);
	
	    // Get output stream to write from it
	    OutputStream out = child.getOutputStream();
	
	    out.write("cd C:/ /r/n".getBytes());
	    out.flush();
	    out.write("dir /r/n".getBytes());
	    out.close();
	} catch (IOException e) {
	}
}
}