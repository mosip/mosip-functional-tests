import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class Demo {
public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
	JSONObject data = (JSONObject) new JSONParser().parse(new FileReader(new File("output.json")));
	//System.out.println(data);
	JSONObject response = (JSONObject) data.get("response");
	System.out.println(response.get("zip-bytes"));
	byte[] b = response.get("zip-bytes").toString().getBytes(Charset.forName("UTF-16"));
	
	File targetFile = new File("packet.zip");
OutputStream outStream = new FileOutputStream(targetFile);
outStream.write(b);
}
}
