package com.wordnik.swagger.generator;

import com.wordnik.swagger.model.*;
import com.wordnik.swagger.config.*;
import com.wordnik.swagger.sample.util.*;
import com.wordnik.swagger.config.FilterFactory;

import javax.servlet.http.HttpServlet;

import java.util.List;
import java.util.ArrayList;

public class Bootstrap extends HttpServlet {
  static {
    // do any additional initialization here, such as set your base path programmatically as such:
    // ConfigFactory.config().setBasePath("http://www.foo.com/");

    // add a custom filter
    FilterFactory.setFilter(new CustomFilter());

    ApiInfo info = new ApiInfo(
      "Swagger Generator",                             /* title */
      "This is an online swagger codegen server.  You can find out more " + 
      "at <a href=\"https://github.com/wordnik/swagger-generator\">https://github.com/wordnik/swagger-generator</a> or on irc.freenode.net, #swagger.",
      "http://helloreverb.com/terms/",                  /* TOS URL */
      "apiteam@wordnik.com",                            /* Contact */
      "Apache 2.0",                                     /* license */
      "http://www.apache.org/licenses/LICENSE-2.0.html" /* license URL */
    );

    ConfigFactory.config().setApiInfo(info);
  }
}
