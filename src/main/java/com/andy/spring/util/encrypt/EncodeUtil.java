package com.andy.spring.util.encrypt;

import com.andy.spring.constant.Constant;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Base64;
import org.apache.commons.lang3.StringUtils;

/**
 * base64和url encode等编码工具
 *
 * @author 庞先海 2019-11-14
 */
public class EncodeUtil {

    /**
     * base64 编码
     */
    public static String base64Encode(byte[] bytes, Charset charset) {
        byte[] encodeBytes = Base64.getEncoder().encode(bytes);
        return new String(encodeBytes, charset);
    }

    /**
     * base64 编码
     */
    public static String base64Encode(byte[] bytes) {
        return base64Encode(bytes, Constant.DEFAULT_CHARSET);
    }

    /**
     * base64 编码
     */
    public static String base64Encode(String text, Charset charset) {
        return base64Encode(text.getBytes(charset));
    }

    public static String base64Encode(String text) {
        return base64Encode(text, Constant.DEFAULT_CHARSET);
    }

    public static byte[] base64DecodeByte(String text) {
        return Base64.getDecoder().decode(text);
    }

    /**
     * base64 解密
     */
    public static String base64Decode(String text, Charset charset) {
        return new String(base64DecodeByte(text), charset);
    }

    public static String base64Decode(String text) {
        return base64Decode(text, Constant.DEFAULT_CHARSET);
    }

    public static String urlEncode(String text, Charset charset) {
        try {
            return URLEncoder.encode(text, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }
    }

    public static String urlEncode(String text) {
        return urlEncode(text, Constant.DEFAULT_CHARSET);
    }

    public static String urlDecode(String text, Charset charset) {
        try {
            return URLDecoder.decode(text, charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }
    }

    public static String urlDecode(String text) {
        return urlDecode(text, Constant.DEFAULT_CHARSET);
    }

    public static String unicodeEncode(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        StringWriter writer = new StringWriter(text.length());
        try {
            unicodeEncode(writer, text, true);
        } catch (IOException e) {
            throw new RuntimeException("unicodeEncode failed", e);
        }
        return writer.toString();
    }

    public static void unicodeEncode(Writer out, String str, boolean escapeSingleQuote) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (str == null) {
            return;
        }
        int sz;
        sz = str.length();
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);

            // handle unicode
            if (ch > 0xfff) {
                out.write("\\u" + hex(ch));
            } else if (ch > 0xff) {
                out.write("\\u0" + hex(ch));
            } else if (ch > 0x7f) {
                out.write("\\u00" + hex(ch));
            } else if (ch < 32) {
                switch (ch) {
                    case '\b':
                        out.write('\\');
                        out.write('b');
                        break;
                    case '\n':
                        out.write('\\');
                        out.write('n');
                        break;
                    case '\t':
                        out.write('\\');
                        out.write('t');
                        break;
                    case '\f':
                        out.write('\\');
                        out.write('f');
                        break;
                    case '\r':
                        out.write('\\');
                        out.write('r');
                        break;
                    default:
                        if (ch > 0xf) {
                            out.write("\\u00" + hex(ch));
                        } else {
                            out.write("\\u000" + hex(ch));
                        }
                        break;
                }
            } else {
                switch (ch) {
                    case '\'':
                        if (escapeSingleQuote) {
                            out.write('\\');
                        }
                        out.write('\'');
                        break;
                    case '"':
                        out.write('\\');
                        out.write('"');
                        break;
                    case '\\':
                        out.write('\\');
                        out.write('\\');
                        break;
                    default:
                        out.write(ch);
                        break;
                }
            }
        }
    }

    private static String hex(char ch) {
        return Integer.toHexString(ch).toUpperCase();
    }

    public static String unicodeDecode(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        StringWriter writer = new StringWriter(text.length());
        try {
            unicodeDecode(writer, text);
        } catch (IOException e) {
            throw new RuntimeException("unicodeDecode failed", e);
        }
        return writer.toString();
    }

    public static void unicodeDecode(Writer out, String str) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (str == null) {
            return;
        }
        int sz = str.length();
        StringBuilder unicode = new StringBuilder(4);
        boolean hadSlash = false;
        boolean inUnicode = false;
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (inUnicode) {
                // if in unicode, then we're reading unicode
                // values in somehow
                unicode.append(ch);
                if (unicode.length() == 4) {
                    // unicode now contains the four hex digits
                    // which represents our unicode chacater
                    try {
                        int value = Integer.parseInt(unicode.toString(), 16);
                        out.write((char)value);
                        unicode.setLength(0);
                        inUnicode = false;
                        hadSlash = false;
                    } catch (NumberFormatException nfe) {
                        throw new RuntimeException("Unable to parse unicode value: " + unicode, nfe);
                    }
                }
                continue;
            }
            if (hadSlash) {
                // handle an escaped value
                hadSlash = false;
                switch (ch) {
                    case '\\':
                        out.write('\\');
                        break;
                    case '\'':
                        out.write('\'');
                        break;
                    case '\"':
                        out.write('"');
                        break;
                    case 'r':
                        out.write('\r');
                        break;
                    case 'f':
                        out.write('\f');
                        break;
                    case 't':
                        out.write('\t');
                        break;
                    case 'n':
                        out.write('\n');
                        break;
                    case 'b':
                        out.write('\b');
                        break;
                    case 'u': {
                        // uh-oh, we're in unicode country....
                        inUnicode = true;
                        break;
                    }
                    default:
                        out.write(ch);
                        break;
                }
                continue;
            } else if (ch == '\\') {
                hadSlash = true;
                continue;
            }
            out.write(ch);
        }
        if (hadSlash) {
            // then we're in the weird case of a \ at the end of the
            // string, let's output it anyway.
            out.write('\\');
        }
    }
}
