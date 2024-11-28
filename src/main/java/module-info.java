module com.manula413.movie_manager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires javafx.base;
    requires org.slf4j;
    requires java.desktop;
    requires jbcrypt;
    requires com.google.gson;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires mysql.connector.j;


    opens com.manula413.movie_manager.controller to javafx.fxml;
    exports com.manula413.movie_manager;
}
