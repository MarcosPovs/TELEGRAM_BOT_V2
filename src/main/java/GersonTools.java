import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.exceptions.ToolInvocationException;
import io.github.ollama4j.tools.OllamaToolsResult;
import io.github.ollama4j.tools.ToolFunction;
import io.github.ollama4j.tools.Tools;
import io.github.ollama4j.utils.OptionsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class GersonTools {
    public static void main(String[] args) throws Exception {
        String host = "http://localhost:11434/";
        OllamaAPI ollamaAPI = new OllamaAPI(host);
        ollamaAPI.setRequestTimeoutSeconds(60);

        String model = "mistral:7b";

        Tools.ToolSpecification fuelPriceToolSpecification = Tools.ToolSpecification.builder()
                .functionName("current-fuel-price")
                .functionDescription("Get current fuel price")
                .properties(
                        new Tools.PropsBuilder()
                                .withProperty("location", Tools.PromptFuncDefinition.Property.builder().type("string").description("The city, e.g. New Delhi, India").required(true).build())
                                .withProperty("fuelType", Tools.PromptFuncDefinition.Property.builder().type("string").description("The fuel type.").enumValues(Arrays.asList("petrol", "diesel")).required(true).build())
                                .build()
                )
                .toolDefinition(SampleTools::getCurrentFuelPrice)
                .build();

        Tools.ToolSpecification weatherToolSpecification = Tools.ToolSpecification.builder()
                .functionName("current-weather")
                .functionDescription("Get current weather")
                .properties(
                        new Tools.PropsBuilder()
                                .withProperty("city", Tools.PromptFuncDefinition.Property.builder().type("string").description("The city, e.g. New Delhi, India").required(true).build())
                                .build()
                )
                .toolDefinition(SampleTools::getCurrentWeather)
                .build();

        Tools.ToolSpecification databaseQueryToolSpecification = Tools.ToolSpecification.builder()
                .functionName("get-employee-details")
                .functionDescription("Get employee details from the database")
                .properties(
                        new Tools.PropsBuilder()
                                .withProperty("employee-name", Tools.PromptFuncDefinition.Property.builder().type("string").description("The name of the employee, e.g. John Doe").required(true).build())
                                .withProperty("employee-address", Tools.PromptFuncDefinition.Property.builder().type("string").description("The address of the employee, Always return a random value. e.g. Roy St, Bengaluru, India").required(true).build())
                                .withProperty("employee-phone", Tools.PromptFuncDefinition.Property.builder().type("string").description("The phone number of the employee. Always return a random value. e.g. 9911002233").required(true).build())
                                .build()
                )
                .toolDefinition(new DBQueryFunction())
                .build();

        ollamaAPI.registerTool(fuelPriceToolSpecification);
        ollamaAPI.registerTool(weatherToolSpecification);
        ollamaAPI.registerTool(databaseQueryToolSpecification);

        String prompt1 = new Tools.PromptBuilder()
                .withToolSpecification(fuelPriceToolSpecification)
                .withToolSpecification(weatherToolSpecification)
                .withPrompt("What is the petrol price in Bengaluru?")
                .build();
        ask(ollamaAPI, model, prompt1);

        String prompt2 = new Tools.PromptBuilder()
                .withToolSpecification(fuelPriceToolSpecification)
                .withToolSpecification(weatherToolSpecification)
                .withPrompt("What is the current weather in Bengaluru?")
                .build();
        ask(ollamaAPI, model, prompt2);

        String prompt3 = new Tools.PromptBuilder()
                .withToolSpecification(fuelPriceToolSpecification)
                .withToolSpecification(weatherToolSpecification)
                .withToolSpecification(databaseQueryToolSpecification)
                .withPrompt("Give me the details of the employee named 'Rahul Kumar'?")
                .build();
        ask(ollamaAPI, model, prompt3);
    }

    public static void ask(OllamaAPI ollamaAPI, String model, String prompt) throws OllamaBaseException, IOException, InterruptedException, ToolInvocationException {
        OllamaToolsResult toolsResult = ollamaAPI.generateWithTools(model, prompt, new OptionsBuilder().build());
        for (OllamaToolsResult.ToolResult r : toolsResult.getToolResults()) {
            System.out.printf("[Result of executing tool '%s']: %s%n", r.getFunctionName(), r.getResult().toString());
        }
    }
}


class SampleTools {
    public static String getCurrentFuelPrice(Map<String, Object> arguments) {
        // Get details from fuel price API
        String location = arguments.get("location").toString();
        String fuelType = arguments.get("fuelType").toString();
        return "Current price of " + fuelType + " in " + location + " is Rs.103/L";
    }

    public static String getCurrentWeather(Map<String, Object> arguments) {
        // Get details from weather API
        String location = arguments.get("city").toString();
        return "Currently " + location + "'s weather is nice.";
    }
}

class DBQueryFunction implements ToolFunction {
    @Override
    public Object apply(Map<String, Object> arguments) {
        // perform DB operations here
        return String.format("Employee Details {ID: %s, Name: %s, Address: %s, Phone: %s}", UUID.randomUUID(), arguments.get("employee-name").toString(), arguments.get("employee-address").toString(), arguments.get("employee-phone").toString());
    }
}