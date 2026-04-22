module com.reservationstudio {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.reservationstudio to javafx.fxml;
    exports com.reservationstudio;

    opens com.reservationstudio.controller to javafx.fxml;
    exports com.reservationstudio.controller;

    opens com.reservationstudio.model to javafx.fxml;
    exports com.reservationstudio.model;

    opens com.reservationstudio.util to javafx.fxml;
    exports com.reservationstudio.util;

    opens com.reservationstudio.data to javafx.fxml;
    exports com.reservationstudio.data;

}
