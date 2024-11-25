module com.manula413.movie_manager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;
    requires java.desktop;
    requires jbcrypt;




    opens com.manula413.movie_manager.controller to javafx.fxml;

    exports com.manula413.movie_manager;
}
