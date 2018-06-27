package character5;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 自定义同步组件：
 * 该工具在同一时刻，只允许至多两个线程同时访问，超过两个线程的访问将被阻塞
 *
 * 首先，确定访问模式。TwinsLock能够在同一时刻支持多个线程的访问，这显然是共享式访问，
 * 因此，需要使用同步器提供的acquireShared(int args)方法等和Shared相关的方法，
 * 这就要求TwinsLock必须重写tryAcquireShared(int args)方法和tryReleaseShared(int args)方法，
 * 这样才能保证同步器的共享式同步状态的获取与释放方法得以执行。
 *
 * 其次，定义资源数。TwinsLock在同一时刻允许至多两个线程的同时访问，表明同步资源数为2，
 * 这样可以设置初始状态status为2，当一个线程进行获取，status减1，该线程释放，status加1，
 * 状态的合法范围为0、1和2，其中0表示当前已经有两个线程获取了同步资源，此时再有其他线程对同步状态进行获取，
 * 该线程只能被阻塞。在同步状态变更时，需要使用compareAndSet(int expect,int update)方法做原子性保障。
 *
 * 最后，组合自定义同步器。前面的章节提到，自定义同步组件通过组合自定义同步器来完成同步功能，
 * 一般情况下自定义同步器会被定义为自定义同步组件的内部类。
 *
 * TwinsLock实现了Lock接口，提供了面向使用者的接口，使用者调用lock()方法获取锁，随后调用unlock()方法释放锁，
 * 而同一时刻只能有两个线程同时获取到锁。TwinsLock同时包含了一个自定义同步器Sync，而该同步器面向线程访问和同步状态控制。
 * 以共享式获取同步状态为例：同步器会先计算出获取后的同步状态，然后通过CAS确保状态的正确设置，
 * 当tryAcquireShared(int reduceCount)方法返回值大于等于0时，当前线程才获取同步状态，对于上层的TwinsLock而言，
 * 则表示当前线程获得了锁。
 */
public class TwinsLock implements Lock {

    private final Sync sync = new Sync(2);
    private static final class Sync extends AbstractQueuedSynchronizer {
        Sync(int count) {
            if (count <= 0) {
                throw new IllegalArgumentException("count must large than zero.");
            }
            setState(count);
        }

        @Override
        public int tryAcquireShared(int reduceCount) {
            for (;;) {
                int current = getState();
                int newCount = current - reduceCount;
                if (newCount < 0 || compareAndSetState(current,
                        newCount)) {
                    return newCount;
                }
            }
        }

        @Override
        public boolean tryReleaseShared(int returnCount) {
            for (;;) {
                int current = getState();
                int newCount = current + returnCount;
                if (compareAndSetState(current, newCount)) {
                    return true;
                }
            }
        }
    }

    @Override
    public void lock() {
        sync.acquireShared(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        sync.releaseShared(1);
    }

    @Override
    public Condition newCondition() {
        return null;
    }

}
