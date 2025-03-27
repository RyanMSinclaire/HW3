module FoundationCode {
	requires javafx.controls;
	requires java.sql;
    requires java.desktop;

    opens application to javafx.base, javafx.graphics, javafx.fxml;
    opens mainClassesUser to javafx.base;
}
