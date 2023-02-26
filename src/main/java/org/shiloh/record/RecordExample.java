package org.shiloh.record;

/**
 * 一个特殊的类，更简单的数据载体
 * @author shiloh
 * @date 2022/12/7 10:52
 */
public record RecordExample(Long id, String username, String email) {
    public void printUserInfo() {
        System.out.printf("id: %d, username: %s, email: %s\n", this.id, this.username, this.email);
    }
}
