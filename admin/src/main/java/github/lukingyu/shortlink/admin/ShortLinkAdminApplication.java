package github.lukingyu.shortlink.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("github.lukingyu.shortlink.admin.mapper")
public class ShortLinkAdminApplication {

    void main() {
        SpringApplication.run(ShortLinkAdminApplication.class);
    }
}