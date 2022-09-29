package org.owasp.wrongsecrets;

import org.owasp.wrongsecrets.canaries.CanaryCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatsController {

    @Autowired
    private CanaryCounter canaryCounter;
    @Autowired
    private SessionConfiguration sessionConfiguration;

    @Value("${canarytokenURLs}")
    private String[] canaryTokenURLs;

    @GetMapping("/stats")
    public String getStats(Model model) {
        model.addAttribute("canaryCounter", canaryCounter.getTotalCount());
        model.addAttribute("sessioncounter", sessionConfiguration.getCounter());
        model.addAttribute("lastCanaryToken", canaryCounter.getLastToken());
        model.addAttribute("canarytokenURLs", canaryTokenURLs);
        return "stats";
    }
}
