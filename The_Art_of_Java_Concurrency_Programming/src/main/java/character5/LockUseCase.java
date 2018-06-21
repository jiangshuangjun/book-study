package character5;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock的使用方式
 */
public class LockUseCase {

    /**
     * (1) 在finally块中释放锁，目的是保证在获取到锁之后，最终能够被释放。
     * (2) 不要将获取锁的过程写在try块中，因为如果在获取锁（自定义锁的实现）时发生了异常，异常抛出的同时，也会导致锁无故释放。
     */
    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
        } finally {
            lock.unlock();
        }
    }
}
