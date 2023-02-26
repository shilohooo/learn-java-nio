package org.shiloh.util;

/**
 * @author shiloh
 * @date 2023/2/25 18:53
 */
public final class ByteUtils {
    private ByteUtils() {}

    /**
     * 去掉 byte 数组中的 0，避免乱码
     * <p>
     * ASCII码 0 代表空字符
     *
     * @param data 原数据
     * @return 不包含 0 的字节数组
     * @author shiloh
     * @date 2023/2/25 18:26
     */
    public static byte[] trimByteData(byte[] data) {
        int length = 0;
        for (final byte b : data) {
            if (b == 0) {
                continue;
            }
            length++;
        }
        if (length == 0) {
            return new byte[0];
        }

        final byte[] result = new byte[length];
        System.arraycopy(data, 0, result, 0, length);
        return result;
    }
}
