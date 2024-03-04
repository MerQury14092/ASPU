package com.merqury.aspu.services;

import java.nio.charset.StandardCharsets;

public class EncodingConverter {
    public static String translateISO8859_1toUTF_8(String ISO8859_1){
        return new String(ISO8859_1.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }
}
