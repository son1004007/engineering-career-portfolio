package io.github.son1004007.opsmate.demo;

import io.github.son1004007.opsmate.infrastructure.llm.LlmProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** 공개 범위와 현재 개방 상태만 보여주는 설치 없는 데모 시작 화면. */
@Controller
public class HomeController {

    private final DemoProperties demoProperties;
    private final LlmProperties llmProperties;

    public HomeController(DemoProperties demoProperties, LlmProperties llmProperties) {
        this.demoProperties = demoProperties;
        this.llmProperties = llmProperties;
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) String status, Model model) {
        model.addAttribute("demoOpen", demoProperties.isEnabled() && demoProperties.isStartEnabled());
        model.addAttribute("modelEnabled", llmProperties.isEnabled());
        model.addAttribute("expired", "expired".equals(status));
        model.addAttribute("busy", "busy".equals(status));
        model.addAttribute("closed", "closed".equals(status));
        return "index";
    }
}
