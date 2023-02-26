package org.shiloh.nio.test.transfer;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * Nio 数据传输单元测试
 *
 * @author shiloh
 * @date 2023/2/25 16:51
 */
public class DataTransferTests {
    /**
     * transferTo 测试：把源通道的数据传输到目标通道中
     *
     * @author shiloh
     * @date 2023/2/25 16:54
     */
    @Test
    public void testTransferTo() {
        final File srcFile = new File("D:\\src.txt");
        try (
                final FileInputStream fis = new FileInputStream(srcFile);
                final FileChannel src = fis.getChannel();
                final FileOutputStream fos = new FileOutputStream("D:\\dest.txt");
                final FileChannel dest = fos.getChannel()
        ) {
            // 创建 byte buffer，小文件一次性读取即可
            final ByteBuffer byteBuffer = ByteBuffer.allocate((int) srcFile.length());
            // 把输入流通道的数据读取到输出流通道中
            src.transferTo(0, byteBuffer.limit(), dest);
            System.out.println("数据传输完毕。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * transferFrom 测试：把来自源通道的数据传输到目标通道中
     *
     * @author shiloh
     * @date 2023/2/25 17:00
     */
    @Test
    public void testTransferFrom() {
        final File srcFile = new File("D:\\src.txt");
        try (
                final FileInputStream fis = new FileInputStream(srcFile);
                final FileChannel src = fis.getChannel();
                final FileOutputStream fos = new FileOutputStream("D:\\dest.txt");
                final FileChannel dest = fos.getChannel()
        ) {
            // 创建 byte buffer，小文件一次性读取即可
            final ByteBuffer byteBuffer = ByteBuffer.allocate((int) srcFile.length());
            // 把输入流通道的数据读取到输出流通道中
            dest.transferFrom(src, 0, byteBuffer.limit());
            System.out.println("数据传输完毕。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 分散读取和聚合写入的测试
     * <p>
     * 使用场景：可以使用一个缓冲区数组，自动地根据需要去分配缓冲区的大小。可以减少内存消耗。网络IO也可以使用
     *
     * @author shiloh
     * @date 2023/2/25 17:04
     */
    @Test
    public void testScatteringReadAndGatheringWrite() {
        final File srcFile = new File("test01.txt");
        try (
                final FileInputStream fis = new FileInputStream(srcFile);
                final FileChannel src = fis.getChannel();
                final FileOutputStream fos = new FileOutputStream("dest01.txt");
                final FileChannel dest = fos.getChannel()
        ) {
            // 创建三个大小为5的缓冲区
            final ByteBuffer byteBuffer1 = ByteBuffer.allocate(5);
            final ByteBuffer byteBuffer2 = ByteBuffer.allocate(5);
            final ByteBuffer byteBuffer3 = ByteBuffer.allocate(5);
            // 创建一个缓冲区数组
            final ByteBuffer[] byteBuffers = {byteBuffer1, byteBuffer2, byteBuffer3};
            // 循环写入到缓冲区数组中，分散读取
            long read;
            long totalLength = 0;
            while ((read = src.read(byteBuffers)) != -1) {
                totalLength += read;
                Arrays.stream(byteBuffers)
                        .map(buffer -> "position = " + buffer.position() + ", limit = " + buffer.limit())
                        .forEach(System.out::println);
                // 切换模式
                Arrays.stream(byteBuffers).forEach(ByteBuffer::flip);
                // 聚合写入到文件输出通道
                dest.write(byteBuffers);
                // 清空缓冲区
                Arrays.stream(byteBuffers).forEach(ByteBuffer::clear);
                System.out.println();
            }
            System.out.println("totalLength = " + totalLength);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试直接缓冲区和非直接缓冲区读取数据的性能区别：
     * 复制一个大小为 186MB 的文件：
     * 直接缓冲区耗时：199ms
     * 非直接缓冲区耗时：266ms
     *
     * @author shiloh
     * @date 2023/2/25 17:18
     */
    @Test
    public void testDifferBetweenDirectMemoryAndNonDirectMemory() {
        final long start = System.currentTimeMillis();
        final File srcFile = new File("D:\\test.gz");
        try (
                final FileInputStream fis = new FileInputStream(srcFile);
                final FileChannel src = fis.getChannel();
                final FileOutputStream fos = new FileOutputStream("D:\\dest.gz");
                final FileChannel dest = fos.getChannel()
        ) {
            // 创建直接缓冲区
            final ByteBuffer byteBuffer = ByteBuffer.allocate(5 * 1024 * 1024);
            while (src.read(byteBuffer) != -1) {
                // 切换读写模式
                byteBuffer.flip();
                dest.write(byteBuffer);
                // 清空缓冲区
                byteBuffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        final long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start) + "ms");
    }
}
