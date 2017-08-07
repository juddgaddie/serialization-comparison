import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import de.undercouch.bson4jackson.BsonFactory;
import de.undercouch.bson4jackson.BsonGenerator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ManualSample {
    public static void main(String[] args) throws Exception {
        //create dummy POJO
        Person bob = new Person();
        bob.setName("Bob");

        //create factory
        BsonFactory factory = new BsonFactory();

        //performance optimization
        factory.enable(BsonGenerator.Feature.ENABLE_STREAMING);

        //serialize data
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonGenerator gen = factory.createJsonGenerator(baos);
        gen.writeStartObject();
        gen.writeFieldName("name");
        gen.writeString(bob.getName());
        gen.close();

        //deserialize data
        ByteArrayInputStream bais = new ByteArrayInputStream(
                baos.toByteArray());
        JsonParser parser = factory.createJsonParser(bais);
        Person clone_of_bob = new Person();
        parser.nextToken();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = parser.getCurrentName();
            parser.nextToken();
            if ("name".equals(fieldname)) {
                clone_of_bob.setName(parser.getText());
            }
        }

        assertThat(bob.getName()).isEqualTo(clone_of_bob.getName());
    }
}