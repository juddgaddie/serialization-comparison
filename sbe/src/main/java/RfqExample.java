import baseline.MessageHeaderDecoder;
import org.agrona.concurrent.UnsafeBuffer;
import rfq.MessageHeaderEncoder;
import rfq.RequestForQuoteDecoder;
import rfq.RequestForQuoteEncoder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.*;

public class RfqExample {


    public static final String FILE_PATH = "rfq-serilized.raw";

    private static final RequestForQuoteEncoder REQUEST_FOR_QUOTE_ENCODER = new RequestForQuoteEncoder();
    private static final RequestForQuoteDecoder REQUEST_FOR_QUOTE_DECODER = new RequestForQuoteDecoder();
    private static final MessageHeaderEncoder MESSAGE_HEADER_ENCODER = new MessageHeaderEncoder();
    private static final MessageHeaderDecoder MESSAGE_HEADER_DECODER = new MessageHeaderDecoder();


    public static void main(String[] args) {

        System.out.println("\n*** Rfq Example ***");

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4096);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

        REQUEST_FOR_QUOTE_ENCODER.wrapAndApplyHeader(directBuffer, 0, MESSAGE_HEADER_ENCODER)
                .noOfDealers(1)
                .requestId("requestId");

        REQUEST_FOR_QUOTE_ENCODER.legsCount(2)
                .next()
                .instrumentId("instrument1")
                .quantity(10)
                .referenceId("refid1")
                .settlementDateInMillis(1000)
                .next()
                .instrumentId("instrument2")
                .quantity(20)
                .referenceId("refid2")
                .settlementDateInMillis(2000);

        byteBuffer.limit(MessageHeaderEncoder.ENCODED_LENGTH + REQUEST_FOR_QUOTE_ENCODER.encodedLength());
        writeToFile(FILE_PATH,byteBuffer);

        // Decode the encoded message

        int bufferOffset = 0;
        MESSAGE_HEADER_DECODER.wrap(directBuffer, bufferOffset);

        // Lookup the applicable flyweight to decode this type of message based on templateId and version.
        final int templateId = MESSAGE_HEADER_DECODER.templateId();
        if (templateId != RequestForQuoteEncoder.TEMPLATE_ID)
        {
            throw new IllegalStateException("Template ids do not match");
        }

        final int actingBlockLength = MESSAGE_HEADER_DECODER.blockLength();
        final int actingVersion = MESSAGE_HEADER_DECODER.version();

        bufferOffset += MESSAGE_HEADER_DECODER.encodedLength();

        REQUEST_FOR_QUOTE_DECODER.wrap(directBuffer, bufferOffset, actingBlockLength, actingVersion);
        final StringBuilder output = new StringBuilder();
        REQUEST_FOR_QUOTE_DECODER.appendTo(output);
        System.out.println(output.toString());
    }

    private static void writeToFile(final String filePath, ByteBuffer byteBuffer) {
        try (FileChannel channel = FileChannel.open(Paths.get(filePath), READ, WRITE, CREATE)) {
            channel.write(byteBuffer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
