import org.agrona.concurrent.UnsafeBuffer;
import rfq.RequestForQuoteEncoder;

import java.nio.ByteBuffer;

public class RfqExample {


    private static final RequestForQuoteEncoder REQUEST_FOR_QUOTE_ENCODER = new RequestForQuoteEncoder();

    public static void main(String[] args) {

        final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4096);
        final UnsafeBuffer directBuffer = new UnsafeBuffer(byteBuffer);

//        REQUEST_FOR_QUOTE_ENCODER.wrapAndApplyHeader()
    }
}
