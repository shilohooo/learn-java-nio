package org.shiloh.nio.test.channel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * NIO - {@link java.nio.channels.SocketChannel} 单元测试
 *
 * @author shiloh
 * @date 2022/12/7 12:22
 */
public class SocketChannelTests {
    public static void main(String[] args) {
        try {
            // 获取 ServerSocketChannel，通过 ServerSocketChannel.open() 方法可以获取到服务器的通道
            final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            // 绑定地址，监听某个端口
            final InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 8088);
            serverSocketChannel.bind(inetSocketAddress);
            // 创建一个缓冲区
            final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            // 接收数据
            while (true) {
                // 获取 SocketChannel，accept() 方法可以获得一个 SocketChannel，也就是客户端的连接通道。
                final SocketChannel socketChannel = serverSocketChannel.accept();
                System.out.println("remote address: " + socketChannel.getRemoteAddress());
                while ((socketChannel.read(byteBuffer)) != -1) {
                    // 反转读写模式
                    byteBuffer.flip();
                    // 打印接收到的数据
                    final byte[] container = new byte[byteBuffer.limit()];
                    // 将字节从缓冲区传输到给定的目标数组
                    byteBuffer.get(container, 0, byteBuffer.limit());
                    System.out.println(new String(container));
                    // 清空缓冲区
                    byteBuffer.clear();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
