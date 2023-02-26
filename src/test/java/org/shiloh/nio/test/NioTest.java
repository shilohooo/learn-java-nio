package org.shiloh.nio.test;

import org.junit.Test;

import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Nio 测试用例
 *
 * @author shiloh
 * @date 2022/5/27 17:52
 */
public class NioTest {
    /**
     * 创建缓冲区
     *
     * @author shiloh
     * @date 2022/5/27 17:53
     */
    @Test
    public void testCreateBuffer() {
        // 使用 allocate 方法创建一个指定大小的缓冲区，单位：byte
        final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        // 查看初始化时的 4 个核心变量
        // capacity：容量，表示缓冲区中最大存储数据的容量，一旦声明就不能改变
        // limit：界限，表示缓冲区中可以操作数据的大小（limit 后数据不能进行读写）
        // position：位置，表示缓冲区中正在操作数据的位置
        // mark：标记，表示记录当前 position 的位置，可以通过 reset 恢复到 mark 的位置
        // 0 <= mark <= position <= limit <= capacity
        System.out.println("初始化后 ----> byteBuffer.limit() = " + byteBuffer.limit());
        System.out.println("初始化后 ----> byteBuffer.position() = " + byteBuffer.position());
        System.out.println("初始化后 ----> byteBuffer.capacity() = " + byteBuffer.capacity());
        System.out.println("初始化后 ----> byteBuffer.mark() = " + byteBuffer.mark());

        System.out.println("-----------------------------------------");

        // 添加一些数据到缓冲区中
        final String name = "shiloh";
        byteBuffer.put(name.getBytes(UTF_8));

        // 添加数据后再次查看初始化时的 4 个核心变量
        System.out.println("put完数据之后 ----> byteBuffer.limit() = " + byteBuffer.limit());
        System.out.println("put完数据之后 ----> byteBuffer.position() = " + byteBuffer.position());
        System.out.println("put完数据之后 ----> byteBuffer.capacity() = " + byteBuffer.capacity());
        System.out.println("put完数据之后 ----> byteBuffer.mark() = " + byteBuffer.mark());
    }
}
