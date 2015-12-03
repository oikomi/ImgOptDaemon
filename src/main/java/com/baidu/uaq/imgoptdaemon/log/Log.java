package com.baidu.uaq.imgoptdaemon.log;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import java.net.URLClassLoader;

/**
 * Created by baidu on 15/12/3.
 */
public class Log {
    static {
        BasicConfigurator.configure();
        PropertyConfigurator.configure(URLClassLoader.getSystemResourceAsStream("log4j.properties"));
        DOMConfigurator.configure("");
    }

}
