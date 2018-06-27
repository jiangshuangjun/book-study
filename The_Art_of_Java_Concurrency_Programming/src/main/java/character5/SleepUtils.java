package character5;

import java.util.concurrent.TimeUnit;

public class SleepUtils {

    public static void second(int time) {
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
