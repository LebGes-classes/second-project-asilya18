import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;

public class JsonDataSerializer {
    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static void saveToJson(String filename, Object object) throws IOException {
        mapper.writeValue(new File(filename), object);
    }

    public static <T> T loadFromJson(String filename, Class<T> valueType) throws IOException {
        return mapper.readValue(new File(filename), valueType);
    }
}