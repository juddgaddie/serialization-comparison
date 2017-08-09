package com.transficc;

import com.google.flatbuffers.FlatBufferBuilder;
import com.transficc.rfq.generated.Leg;
import com.transficc.rfq.generated.Rfq;

import java.nio.ByteBuffer;

import static org.assertj.core.api.Assertions.assertThat;

public class RfqExample {

    private static final short NO_OF_DEALERS = (short) 3;
    private static final String REQUEST_ID = "requestId";
    private static final String INSTRUMENT_ID_1 = "instrumentId1";
    private static final String INSTRUMENT_ID_2 = "instrumentId2";
    private static final String REF_ID_1 = "refId1";
    private static final String REF_ID_2 = "refId2";

    public static void main(String[] args) {
        FlatBufferBuilder flatBufferBuilder = new FlatBufferBuilder();
        int requestId = flatBufferBuilder.createString(REQUEST_ID);

        int instrumentIdOffset1 = flatBufferBuilder.createString(INSTRUMENT_ID_1);
        int instrumentIdOffset2 = flatBufferBuilder.createString(INSTRUMENT_ID_2);
        int refId1 = flatBufferBuilder.createString(REF_ID_1);
        int refId2 = flatBufferBuilder.createString(REF_ID_2);

        int leg1Offset = Leg.createLeg(flatBufferBuilder, refId1, instrumentIdOffset1, 3, (byte) 1, 100.1);
        int leg2Offset = Leg.createLeg(flatBufferBuilder, refId2, instrumentIdOffset2, 8, (byte) 2, 102.1);
        int[] legAr = {leg1Offset, leg2Offset};
        int legsVector = Rfq.createLegsVector(flatBufferBuilder, legAr);

        Rfq.startRfq(flatBufferBuilder);

        Rfq.addLegs(flatBufferBuilder, legsVector);
        Rfq.addRequestId(flatBufferBuilder, requestId);
        Rfq.addNoOfDealers(flatBufferBuilder, NO_OF_DEALERS);
        int endOffset = Rfq.endRfq(flatBufferBuilder);

        Rfq.finishRfqBuffer(flatBufferBuilder, endOffset);

        ByteBuffer buf = flatBufferBuilder.dataBuffer();

        Rfq rfq = Rfq.getRootAsRfq(buf);
        assertThat(rfq.noOfDealers()).isEqualTo(NO_OF_DEALERS);
        assertThat(rfq.requestId()).isEqualTo(REQUEST_ID);

        Leg leg1 = rfq.legs(0);
        assertThat(leg1.referenceId()).isEqualTo(REF_ID_1);
        assertThat(leg1.instrumentId()).isEqualTo(INSTRUMENT_ID_1);

        Leg leg2 = rfq.legs(1);
        assertThat(leg2.referenceId()).isEqualTo(REF_ID_2);
        assertThat(leg2.instrumentId()).isEqualTo(INSTRUMENT_ID_2);

    }
}
