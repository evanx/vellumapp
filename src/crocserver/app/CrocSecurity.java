/*
 * Source https://code.google.com/p/vellum by @evanxsummers

       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements. See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.  
 */
package crocserver.app;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base32;

/**
 *
 * @author evan.summers
 */
public class CrocSecurity {

    public static String createSecret() {
        byte[] bytes = new byte[10];
        new SecureRandom().nextBytes(bytes);
        return new Base32().encodeAsString(bytes);
    }

    public static Mac createHmac(byte[] secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec signKey = new SecretKeySpec(secret, "HmacSHA1");
        mac.init(signKey);
        return mac;
    }

    public static String createCode(byte[] secret, String string, long value) throws Exception {
        Mac mac = createHmac(secret);
        mac.update(string.getBytes());
        return new Base32().encodeAsString(mac.doFinal(toByteArray(value)));
    }

    public static String createCode(byte[] secret, long value) throws Exception {
        Mac mac = createHmac(secret);
        return new Base32().encodeAsString(mac.doFinal(toByteArray(value)));
    }

    private static byte[] toByteArray(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(value);
        return buffer.array();
    }

    public static String getTotpUrl(String userName, String serverName, String secret) {
        return String.format("otpauth://totp/%s@%s?secret=%s", userName, serverName, secret);
    }

    public static String getQrCodeUrl(String userName, String serverName, String secret) {
        return "https://chart.googleapis.com/chart?chs=200x200&chld=M|0&cht=qr&chl="
                + "otpauth%3A%2F%2Ftotp%2F" + userName + '@' + serverName
                + "%3Fsecret%3D" + secret;
    }
}
