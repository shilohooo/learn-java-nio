package org.shiloh.nio.test.selector;

import org.junit.Test;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO - Selector 单元测试
 * <p>
 * NIO 常常被叫做非阻塞 IO，主要是因为 NIO 在网络通信中的非阻塞特性被广泛使用。
 * <p>
 * NIO 实现了 IO 多路复用中的 Reactor 模型，一个线程 Thread 使用一个选择器 Selector 通过轮询的方式去监听多个通道 Channel 上的事件，
 * 从而让一个线程就可以处理多个事件。
 * <p>
 * 通过配置监听的通道 Channel 为非阻塞，那么当 Channel 上的 IO 事件还未到达时，就不会进入阻塞状态一直等待，
 * <p>
 * 而是继续轮询其它 Channel，找到 IO 事件已经到达的 Channel 执行。
 * <p>
 * 因为创建和切换线程的开销很大，因此使用一个线程来处理多个事件而不是一个线程处理一个事件具有更好的性能。
 * <p>
 * 应该注意的是，只有套接字 Channel 才能配置为非阻塞，而 FileChannel 不能，为 FileChannel 配置非阻塞也没有意义。
 *
 * @author shiloh
 * @date 2022/12/2 16:56
 * @see <a href="https://pdai.tech/md/java/io/java-io-nio.html">原文链接</a>
 */
public class SelectorTests {
    /**
     * 选择器测试
     * <p>
     * 通道必须配置为非阻塞模式，否则使用选择器就没有任何意义了，因为如果通道在某个事件上被阻塞，那么服务器就不能响应其它事件，
     * 必须等待这个事件处理完毕才能去处理其它事件，显然这和选择器的作用背道而驰。
     * <p>
     * 在将通道注册到选择器上时，还需要指定要注册的具体事件，主要有以下几类:
     * <ul>
     *     <li>{@link SelectionKey#OP_ACCEPT}</li>
     *     <li>{@link SelectionKey#OP_WRITE}</li>
     *     <li>{@link SelectionKey#OP_READ}</li>
     *     <li>{@link SelectionKey#OP_CONNECT}</li>
     * </ul>
     * <p>
     * 可以看出每个事件可以被当成一个位域，从而组成事件集整数。例如:
     * <pre>
     *     final int eventSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
     * </pre>
     *
     * @author shiloh
     * @date 2022/12/2 16:58
     */
    @Test
    public void test() throws Exception {
        try (
                // 创建选择器
                final Selector selector = Selector.open();
                // 将通道注册到选择器上
                final ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()
        ) {
            // 将通道配置为非阻塞模式
            serverSocketChannel.configureBlocking(false);
            // 注册 ACCEPT 事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            // 使用 select() 来监听到达的事件，它会一直阻塞直到有至少一个事件到达。
            final int num = selector.select();
            // 获取到达的事件
            final Set<SelectionKey> selectionKeys = selector.selectedKeys();
            final Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()) {
                final SelectionKey selectionKey = keyIterator.next();
                if (selectionKey.isAcceptable()) {
                    // 可以接收新的连接
                } else if (selectionKey.isReadable()) {
                    // 可以读取
                } else if (selectionKey.isWritable()) {
                    // 可以写入
                } else if (selectionKey.isConnectable()) {
                    // 连接完成
                }
                keyIterator.remove();
            }
        }
    }
}
