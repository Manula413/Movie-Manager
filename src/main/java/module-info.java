module com.manula413.movie_manager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.slf4j;
    requires java.desktop;


    opens com.manula413.movie_manager.Controller to javafx.fxml;

    exports com.manula413.movie_manager;
}
