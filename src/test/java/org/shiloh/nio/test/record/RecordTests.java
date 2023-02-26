package org.shiloh.nio.test.record;

import org.junit.Test;
import org.shiloh.record.RecordExample;

import java.net.URI;

/**
 * @author shiloh
 * @date 2022/12/7 10:59
 */
public class RecordTests {
    @Test
    public void testPrintRecord() {
        final RecordExample recordExample = new RecordExample(1L, "shiloh", "shiloh@gmail.com");
        recordExample.printUserInfo();
        final String textBlock = """
                select * from sys_user where id = %d;
                """;
        System.out.println(textBlock);
        System.out.println(URI.create("https://qingtengschool.com?id=1&flag=true").getQuery());
    }
}
