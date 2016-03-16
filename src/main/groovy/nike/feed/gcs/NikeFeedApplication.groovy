package nike.feed.gcs

import groovy.sql.Sql

import javax.sql.DataSource

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

@SpringBootApplication
class NikeFeedApplication extends WebMvcConfigurerAdapter{

    @Autowired
    DataSource dataSource

    static void main(String[] args) {
        SpringApplication.run NikeFeedApplication, args
    }

    @Bean
    Sql sql() {
        new Sql(dataSource)
    }    
}
