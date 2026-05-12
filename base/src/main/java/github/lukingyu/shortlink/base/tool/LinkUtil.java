package github.lukingyu.shortlink.base.tool;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

public final class LinkUtil {


    /**
     * 获取该有效期的剩余时间
     * @param validDate 有效期
     * @return 剩余毫秒数
     */
    public static long getRestMilliSeconds(Date validDate) {
        if (validDate == null) return 2626560000L; // 默认30天
        Duration duration = Duration.between(validDate.toInstant(), LocalDateTime.now());
        return duration.isNegative() ? 0 : duration.toMillis();
    }


}
