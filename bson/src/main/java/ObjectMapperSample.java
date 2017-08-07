import com.fasterxml.jackson.databind.ObjectMapper;
import de.undercouch.bson4jackson.BsonFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectMapperSample {
    public static void main(String[] args) throws Exception {
        //create dummy POJO
        Person bob = new Person();
        bob.setName("Bob");

        //serialize data
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
        mapper.writeValue(baos, bob);

        byte[] buffer = baos.toByteArray();

        //deserialize data
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        Person clone_of_bob = mapper.readValue(bais, Person.class);

        assertThat(bob.getName()).isEqualTo(clone_of_bob.getName());
    }

}