package com.leroy.magmobile.api.matchers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;
import com.leroy.core.configuration.Log;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import ru.leroymerlin.qa.core.clients.base.Response;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

public class IsValid extends TypeSafeMatcher<Response<?>> {

    private Class<?> pojoClass;
    private ProcessingReport report;

    public IsValid(Class<?> pojoClass) {
        this.pojoClass = pojoClass;
    }

    @Override
    public boolean matchesSafely(Response<?> resp) {
        ObjectMapper objectMapper = new ObjectMapper()
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(
                objectMapper, JsonSchemaConfig.nullableJsonSchemaDraft4());
        JsonNode jsonSchema = jsonSchemaGenerator.generateJsonSchema(pojoClass);

        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        JsonValidator validator = factory.getValidator();
        try {
            report = validator.validate(jsonSchema, resp.asJson(JsonNode.class), true);
        } catch (Exception err) {
            Log.error(err.getMessage());
        }
        return report != null && report.isSuccess();
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Data should match Json Schema");
    }

    @Override
    protected void describeMismatchSafely(Response<?> item, Description mismatchDescription) {
        for (ProcessingMessage processingMessage : report) {
            String pointer = processingMessage.asJson().get("instance").get("pointer").asText();
            mismatchDescription.appendText(pointer + " : " + processingMessage.getMessage());
            mismatchDescription.appendText("\n          ");
        }
        mismatchDescription.appendText("\nResponse ");
        super.describeMismatchSafely(item, mismatchDescription);
    }

    public static IsValid valid(Class<?> pojoClass) {
        return new IsValid(pojoClass);
    }

}
