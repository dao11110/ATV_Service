package com.foxconn.fii;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.TimeZone;

@Slf4j
@SpringBootApplication
@RestController
public class Application extends SpringBootServletInitializer implements CommandLineRunner {

    @Autowired
    private AmqpTemplate amqpTemplate;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+7:00"));
    }

    @Bean
    @Primary
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(10);
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        return threadPoolTaskScheduler;
    }

    @RequestMapping("/greeting")
    public String greeting(HttpServletRequest request) {
        return "Welcome to notify system!\n--- VN FII Team ---";
    }

    @Override
    public void run(String... args) throws Exception {
//        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7:00"));
//        Date now = df.parse("2019/05/28 21:15:00");
//        calendar.setTime(now);
//        TimeSpan timeSpan = TimeSpan.from(calendar, TimeSpan.Type.DAILY);
//
//        CivetArticle article = new CivetArticle();
//        article.setAuthor("WS - VN FII Team");
//        article.setTitle(String.format("[%s] %s - %s", "FII", "B04", "LOCKED"));
//        article.setDescription("detail");
//        article.setImageMediaID("414e19f9-b898-4511-b512-a96f05162ae9");
//
//        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:8888" + "/icivet/task/handle")
//                .queryParam("trackingId", 1);
//        article.setUrl(uriBuilder.toUriString());
//
//        CivetMsgBase data = CivetNewsMsg.Create(Collections.singletonList(article));
//
//        CivetMsgBase text = CivetTextMsg.Create("Welcome!");
//
//        ObjectMapper mapper = new ObjectMapper();
//
//        String json = mapper.writeValueAsString(data);
//
//        String message = mapper.writeValueAsString(NotifyMessage.of("test", NotifyMessage.System.CIVET_NEWS, NotifyMessage.Type.USER, "cpegvn", "V0946495", json));
//        amqpTemplate.convertAndSend("notify", "", message);
//
//        String json2 = mapper.writeValueAsString(text);
//        icivetService.sendMessage("Testing new account icivet!", "V0946495");
    }
}
