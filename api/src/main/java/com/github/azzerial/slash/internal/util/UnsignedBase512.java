/*
 * Copyright 2021 Robin Mercier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.azzerial.slash.internal.util;

import java.math.BigInteger;

public final class UnsignedBase512 {

    private static final BigInteger _512 = BigInteger.valueOf(512);
    private static final String NUMERALS =
        /* U+0030 -> U+0039 */ "0123456789" +
        /* U+0041 -> U+005A */ "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
        /* U+0061 -> U+007A */ "abcdefghijklmnopqrstuvwxyz" +
        /* U+00C0 -> U+00D6 */ "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ" +
        /* U+00D8 -> U+00DF */ "ØÙÚÛÜÝÞß" +
        /* U+00E0 -> U+00F6 */ "àáâãäåæçèéêëìíîïðñòóôõö" +
        /* U+00F8 -> U+00FF */ "øùúûüýþÿ" +
        /* U+0100 -> U+0148 */ "ĀāĂăĄąĆćĈĉĊċČčĎďĐđĒēĔĕĖėĘęĚěĜĝĞğĠġĢģĤĥĦħĨĩĪīĬĭĮįİıĲĳĴĵĶķĸĹĺĻļĽľĿŀŁłŃńŅņŇň" +
        /* U+014A -> U+017F */ "ŊŋŌōŎŏŐőŒœŔŕŖŗŘřŚśŜŝŞşŠšŢţŤťŦŧŨũŪūŬŭŮůŰűŲųŴŵŶŷŸŹźŻżŽžſ" +
        /* U+0180 -> U+01BF */ "ƀƁƂƃƄƅƆƇƈƉƊƋƌƍƎƏƐƑƒƓƔƕƖƗƘƙƚƛƜƝƞƟƠơƢƣƤƥƦƧƨƩƪƫƬƭƮƯưƱƲƳƴƵƶƷƸƹƺƻƼƽƾƿ" +
        /* U+01C4 -> U+01CC */ "ǄǅǆǇǈǉǊǋǌ" +
        /* U+01CD -> U+01DC */ "ǍǎǏǐǑǒǓǔǕǖǗǘǙǚǛǜ" +
        /* U+01DD -> U+01FF */ "ǝǞǟǠǡǢǣǤǥǦǧǨǩǪǫǬǭǮǯǰǱǲǳǴǵǶǷǸǹǺǻǼǽǾǿ" +
        /* U+0200 -> U+0217 */ "ȀȁȂȃȄȅȆȇȈȉȊȋȌȍȎȏȐȑȒȓȔȕȖȗ" +
        /* U+0218 -> U+021B */ "ȘșȚț" +
        /* U+021C -> U+0229 */ "ȜȝȞȟȠȡȢȣȤȥȦȧȨȩ" +
        /* U+022A -> U+0233 */ "ȪȫȬȭȮȯȰȱȲȳ" +
        /* U+0241 -> U+024F */ "ɁɂɃɄɅɆɇɈɉɊɋɌɍɎɏ" +
        /* U+0250 -> U+0295 */ "ɐɑɒɓɔɕɖɗɘəɚɛɜɝɞɟɠɡɢɣɤɥɦɧɨɩɪɫɬɭɮɯɰɱɲɳɴɵɶɷɸɹɺɻɼɽɾɿʀʁʂʃʄʅʆʇʈʉʊʋʌʍʎʏʐʑʒʓʔʕ";

    /* Constructors */

    private UnsignedBase512() {}

    /* Methods */

    public static byte parseByte(String value) {
        return parseBigInteger(value).byteValueExact();
    }

    public static short parseShort(String value) {
        return parseBigInteger(value).shortValueExact();
    }

    public static int parseInt(String value) {
        return parseBigInteger(value).intValueExact();
    }

    public static long parseLong(String value) {
        return parseBigInteger(value).longValueExact();
    }

    public static BigInteger parseBigInteger(String value) {
        if (value == null) {
            throw new NullPointerException("value must not be null");
        }
        if (value.isEmpty()) {
            throw new IllegalArgumentException("value must not be empty");
        }
        if (!canDecode(value)) {
            throw new IllegalArgumentException("value contains characters not present in the encoding");
        }

        BigInteger n = BigInteger.ZERO;

        for (int i = 0; i != value.length(); i++) {
            final char c = value.charAt(value.length() - 1 - i);
            n = n.add(BigInteger.valueOf(NUMERALS.indexOf(c)).multiply(_512.pow(i)));
        }
        return n;
    }

    public static String toString(byte value) {
        return toString(BigInteger.valueOf(value));
    }

    public static String toString(short value) {
        return toString(BigInteger.valueOf(value));
    }

    public static String toString(int value) {
        return toString(BigInteger.valueOf(value));
    }

    public static String toString(long value) {
        return toString(BigInteger.valueOf(value));
    }

    public static String toString(BigInteger value) {
        if (value == null) {
            throw new NullPointerException("value must not be null");
        }
        if (value.signum() == -1) {
            throw new IllegalArgumentException("value must not be negative");
        }

        final StringBuilder sb = new StringBuilder();
        int i;

        while (value.signum() != 0) {
            i = value.mod(_512).intValueExact();
            sb.append(NUMERALS.charAt(i));
            value = value.divide(_512);
        }
        return sb.reverse().toString();
    }

    /* Internal */

    private static boolean canDecode(String s) {
        for (char c : s.toCharArray()) {
            if (!NUMERALS.contains(String.valueOf(c))) {
                return false;
            }
        }
        return true;
    }
}
