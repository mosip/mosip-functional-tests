package io.mosip.ivv.orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.ivv.core.exceptions.RigInternalError;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

public class DataproviderTest {

    @Test(expected = Test.None.class)
    public void dataproviderOutput() throws RigInternalError {
        Object[][] dataArray = Orchestrator.dataProvider();
        for(int i=0; i<dataArray.length; i++){
            Object [] scenarioRow = dataArray[i];
            System.out.println(scenarioRow[0]);
            System.out.println(scenarioRow[2]);
            System.out.println(scenarioRow[3]);
            ObjectMapper mapper = new ObjectMapper();
            try {
                String scenarioInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(scenarioRow[1]);
                System.out.println(scenarioInString);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
