import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.types.OllamaModelType;
import io.github.ollama4j.utils.OptionsBuilder;

import java.io.IOException;

public class Ollama {

    String host = "http://localhost:11434/";

    public String getOllamaResponse(String prompt) throws OllamaBaseException, IOException, InterruptedException {

        OllamaAPI ollamaAPI = new OllamaAPI(host);

        ollamaAPI.setRequestTimeoutSeconds(200);

        OllamaResult result =
                ollamaAPI.generate("gemma2:2b", prompt, true, new OptionsBuilder().build());
        return result.getResponse();
    }
}