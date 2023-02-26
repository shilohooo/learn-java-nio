package org.shiloh.nio.test.buffer;

import org.junit.Test;

import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * NIO - {@link ByteBuffer} 单元测试
 *
 * @author shiloh
 * @date 2022/12/7 12:08
 */
public class ByteBufferTests {
    /**
     * {@link ByteBuffer} 测试
     *
     * @author shiloh
     * @date 2022/12/7 11:46
     */
    @Test
    public void byteBufferTest() {
        final var msg = "Hello NIO ByteBuffer";
        // 创建一个固定大小的 byteBuffer 实例，实际返回的是 HeapByteBuffer 类型
        // position（当前位置，默认从 0 开始） = 0，capacity（缓冲区所包含的元素数量） = 1024，limit（限制，不能读取或者写入的位置） = 1024
        final var byteBuffer = ByteBuffer.allocate(1024);
        // 写入数据到 buffer 中
        final byte[] bytes = msg.getBytes(UTF_8);
        // put 多少数据，position 就会增加多少
        // 此时 position = 0 + bytes.length
        byteBuffer.put(bytes);
        // 反转读写模式，缓冲区是双向的，既可以写入也可以读取，但不能同时进行，需要切换。
        // 此时 limit = position，然后将 position 重置为0
        byteBuffer.flip();
        // 读取数据
        final byte[] container = new byte[bytes.length];
        int i = 0;
        // hasRemaining(): 缓冲区中是否还有数据，至少还有一个数据才会返回 true
        // 当 position 不小于 limit 时，hasRemaining 返回 false，数据读取完成
        // 容量不变，通过控制 position 和 limit 的值来控制读写的数据
        while (byteBuffer.hasRemaining()) {
            // get 之后，position + 1
            final var data = byteBuffer.get();
            System.out.printf("当前读取到的字符为：%s，对应的 ASCII码为：%d\n", ((char) data), data);
            container[i] = data;
            i++;
        }
        System.out.println(new String(container, UTF_8));
    }
}
