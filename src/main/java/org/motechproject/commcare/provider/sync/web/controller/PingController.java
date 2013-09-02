package org.motechproject.commcare.provider.sync.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/web-api/commcare-provider-sync")
public class PingController {

    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    @ResponseBody
    public String ping() {
        return "CommcareProviderSync Ping Page";
    }
}
