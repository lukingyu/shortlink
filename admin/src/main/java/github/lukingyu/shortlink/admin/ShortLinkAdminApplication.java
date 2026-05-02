package github.lukingyu.shortlink.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan({"github.lukingyu.shortlink.admin.mapper", "github.lukingyu.shortlink.base.mvc.mapper"})
@ComponentScan({"github.lukingyu.shortlink.admin", "github.lukingyu.shortlink.base"})
public class ShortLinkAdminApplication {

    void main() {
        SpringApplication.run(ShortLinkAdminApplication.class);
    }
}