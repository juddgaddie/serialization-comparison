package com.transficc.protobuffs;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedOutputStream;
import com.transficc.protobuffs.codecs.Codecs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class EncodeAndDecodeExample {

    public static void main(String[] args) throws IOException {
        ByteString name = ByteString.copyFrom("I am Groot!", StandardCharsets.US_ASCII);
        Codecs.Friends friends = Codecs.Friends.newBuilder().addFriends("Peter").addFriends("Gamora").addFriends("Drax").build();
        Codecs.Person bob = Codecs.Person.newBuilder().setNameBytes(name).putProjects("goodfriends", friends).addEmail("email1").addEmail("email2").build();
        ByteBuffer buffer = ByteBuffer.allocate(10000);

        CodedOutputStream outputStream = CodedOutputStream.newInstance(buffer);
        bob.writeTo(outputStream);
        outputStream.flush();
        buffer.flip();
        System.out.println(buffer);

        Codecs.Person person = Codecs.Person.parseFrom(buffer);
        System.out.println(person);
    }
}
