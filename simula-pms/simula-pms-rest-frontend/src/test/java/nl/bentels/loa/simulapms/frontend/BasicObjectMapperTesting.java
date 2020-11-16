package nl.bentels.loa.simulapms.frontend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.neovisionaries.i18n.CountryCode;

import nl.bentels.loa.simulapms.frontend.persons.PersonsResource.AddressDTO;

public class BasicObjectMapperTesting {

    private final ObjectReader reader = new ObjectMapper().reader();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void whenArrayOfStringRead_thenOkay() throws IOException {
        String input = """
                ["Test0", "Test1", "Test2"]
                """;
//        @SuppressWarnings("unchecked")
//        List<String> output = reader.readValue(input, List.class);
        List<String> output = mapper.readValue(input, new TypeReference<List<String>>() {
        });
        assertEquals(3, output.size(), () -> "Unexpected size");
        assertEquals("Test0", output.get(0), () -> "Wrong element");
        assertEquals("Test1", output.get(1), () -> "Wrong element");
        assertEquals("Test2", output.get(2), () -> "Wrong element");
    }

    @Test
    public void whenObjectRead_thenOkay() throws IOException {
        String input = """
                {
                    "lines" : ["Appelstraat 23", "3567 HJ", "Appelscha"],
                    "country" : "NL"
                }
                """;
//        JsonNode objTree = reader.readTree(input);
        JsonNode objTree = reader.readTree(input);
        String country = objTree.get("country").asText();
        assertEquals("NL", country, () -> "Wrong country");

        Iterator<JsonNode> elements = objTree.get("lines").elements();
        assertEquals("Appelstraat 23", elements.next().asText(), () -> "Wrong element");
        assertEquals("3567 HJ", elements.next().asText(), () -> "Wrong element");
        assertEquals("Appelscha", elements.next().asText(), () -> "Wrong element");
    }

    @Test
    public void whenObjectUnmarshalled_thenOkay() throws IOException {
        String input = """
                {
                "lines" : ["Appelstraat 23", "3567 HJ", "Appelscha"],
                "country" : "NL"
                }
                """;
//        JsonNode objTree = reader.readTree(input);
        AddressDTO output = mapper.readValue(input, AddressDTO.class);
        assertEquals(CountryCode.NL, output.getCountry(), () -> "Wrong country");

        Iterator<String> elements = output.getLines().iterator();
        assertEquals("Appelstraat 23", elements.next(), () -> "Wrong element");
        assertEquals("3567 HJ", elements.next(), () -> "Wrong element");
        assertEquals("Appelscha", elements.next(), () -> "Wrong element");
    }
}
