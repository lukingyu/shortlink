package github.lukingyu.shortlink.base.constant;

public final class RedisCacheConstant {

    public static final String LOCK_USER_REGISTER_KEY = "short-link:lock_user-register:";
    public static final String LOGIN_USER_TOKEN_KEY = "short-link:login_";

    /**
     * 短链接跳转关系，前缀 Key
     */
    public static final String SHORT_LINK_GOTO_KEY = "short-link:goto_%s";

    /**
     * 短链接跳转锁前缀 Key //todo
     */
    public static final String SHORT_LINK_REDIRECT_LOCK_KEY = "short-link:redirect_lock_%s";

    /**
     * 短链接跳转空值前缀 Key
     */
    public static final String SHORT_LINK_NULL_GOTO_KEY = "short-link:null_goto_%s";
}