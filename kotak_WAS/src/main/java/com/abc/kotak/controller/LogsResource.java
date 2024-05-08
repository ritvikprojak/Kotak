//package com.abc.kotak.controller;
//
//import com.abc.kotak.web.rest.vm.LoggerVM;
//
//import ch.qos.logback.classic.Level;
////import ch.qos.logback.classic.Logger;
//import ch.qos.logback.classic.LoggerContext;
////import ch.qos.logback.classic.spi.ILoggingEvent;
////import ch.qos.logback.core.Appender;
//
//import com.codahale.metrics.annotation.Timed;
//
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
////import java.util.Iterator;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * Controller for view and managing Log Level at runtime.
// */
//@RestController
//@RequestMapping("/manage") // Updated to "manage" from "management" on 06/12/2019
//@CrossOrigin
//public class LogsResource {
//	
//	LoggerContext context;
//	
//	@GetMapping("/logs")
//    public Object getList() {
//         context = (LoggerContext) LoggerFactory.getILoggerFactory();
//        System.out.println("Hello");
//        return context.getLoggerList()
//            .stream()
//            .map(LoggerVM :: new)
//            .collect(Collectors.toList());
//    }
//
//    @PostMapping("/logs")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @Timed
//    public void changeLevel(@RequestBody LoggerVM jsonLogger) {
//        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
//        context.getLogger(jsonLogger.getName()).setLevel(Level.valueOf(jsonLogger.getLevel()));
//    }
//}
