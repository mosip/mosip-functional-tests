import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.ivv.core.dtos.ParserInputDTO;
import io.mosip.ivv.core.dtos.Scenario;
import io.mosip.ivv.core.exceptions.RigInternalError;
import io.mosip.ivv.core.utils.Utils;
import io.mosip.ivv.dg.DataGenerator;
import io.mosip.ivv.parser.Parser;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

public class ScenarioDataTest {
    private Parser parser;

    @Before
    public void init() {
        String configFile = Paths.get(System.getProperty("user.dir"),"..", "ivv-orchestrator","config.properties").normalize().toString();
        Properties properties = Utils.getProperties(configFile);
        ParserInputDTO parserInputDTO = new ParserInputDTO();
        parserInputDTO.setConfigProperties(properties);
        parserInputDTO.setScenarioSheet(Paths.get(configFile, "..", properties.getProperty("ivv.path.scenario.sheet")).normalize().toString());
        parser = new Parser(parserInputDTO);
    }

    @Test
    public void scenarioData() throws RigInternalError {
        ArrayList<Scenario> scenariosToRun = parser.getScenarios();
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(scenariosToRun);
            System.out.println(jsonInString);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
