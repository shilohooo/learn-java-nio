package org.shiloh.nio.test.channel;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * NIO - {@link FileChannel} 文件读写单元测试
 * <p>
 * Channel 本身是不存储任何数据的，它只负责数据的运输，要和 Buffer 一起使用。
 *
 * @author shiloh
 * @date 2022/12/2 16:38
 */
public class FileChannelTests {
    private static final String SRC = "D:\\src.txt";
    private static final String DEST = "D:\\dest.txt";

    /**
     * 使用 FileChannel 快速复制文件
     *
     * @author shiloh
     * @date 2022/12/2 16:39
     */
    @Test
    public void testFastCopy() throws Exception {
        try (
                // 获取源文件的输入字节流
                final FileInputStream fileInputStream = new FileInputStream(SRC);
                // 获取输入字节流的文件通道
                final FileChannel srcChannel = fileInputStream.getChannel();
                // 获取目标文件的输出字节流
                final FileOutputStream fileOutputStream = new FileOutputStream(DEST);
                // 获取输出字节流的文件通道
                final FileChannel destChannel = fileOutputStream.getChannel()
        ) {
            // 创建缓冲区，分配 1024 字节
            // 主要分成两种方式：JVM 堆内内存块 Buffer，堆外内存块 Buffer
            // 下面是创建堆内内存块 Buffer 的方式（非直接缓冲区），该方式所创建的字节缓冲区位于JVM堆中，即JVM内部所维护的字节数组。
            // final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            // 下面是创建堆外内存块 Buffer 的方式（直接缓冲区），该方式所创建的字节缓冲区是由操作系统本地代码创建的内存缓冲数组。
            // 直接缓冲区的使用场景：
            // 1.Java 程序与本地磁盘，Socket 传输数据
            // 2.大文件对象，使用时不会占用JVM内存。
            // 3.不需要频繁创建，生命周期较长，能重复使用的情况。
            // 排除以上情况，建议使用堆内内存块缓冲区，只有数据量达到一定的量级，使用直接缓冲区才会有一定的优势。
            final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
            while (true) {
                // 从输入通道中读取数据到缓冲区中
                final int read = srcChannel.read(byteBuffer);
                // 读取到 -1 表示到文件末尾了
                if (read == -1) {
                    break;
                }
                System.out.printf("本次读取到了 %d 字节\n", read);
                // 切换读写模式
                byteBuffer.flip();
                // 换缓冲区中的内容写入到输出文件中
                destChannel.write(byteBuffer);
                // 清空缓冲区
                byteBuffer.clear();
            }
        }
    }
}
