package github.lukingyu.shortlink.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan({"github.lukingyu.shortlink.project.mapper", "github.lukingyu.shortlink.base.mvc.mapper"})
@ComponentScan({"github.lukingyu.shortlink.project", "github.lukingyu.shortlink.base"})
public class ShortLinkApplication {

    void main() {
        SpringApplication.run(ShortLinkApplication.class);
    }
}
