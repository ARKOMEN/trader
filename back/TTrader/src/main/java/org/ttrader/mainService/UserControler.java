package org.ttrader.mainService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.json.Json;
import javax.json.JsonObject;

@RestController
@RequestMapping("/")
public class UserControler {

//    @GetMapping("/hello")
//    public JsonObject hello() {
//        return Json.createObjectBuilder().add("Hello", "world").build();
//    }
//
//    @GetMapping("/recomend")
//    public JsonObject recomend() {
//        //...
//    }
//
//    @GetMapping("/news")
//    public JsonObject news() {
//        //...
//    }
//
//    @GetMapping("/ticker/{ticker}")
//    public JsonObject ticker(@PathVariable String ticker) {
//        //...
//    }
//
//    @GetMapping("/compare/{ticker1}/{ticker2}")
//    public JsonObject compare(@PathVariable String ticker1, @PathVariable String ticker2) {
//        //...
//    }
}
