module space.nasa.spaceapi {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.web;
	requires com.dlsc.formsfx;
	requires org.kordamp.bootstrapfx.core;
	requires com.google.gson;
	requires java.net.http;
	requires javafx.media;
	opens space.nasa.spaceapi to javafx.fxml;
	exports space.nasa.spaceapi;
	exports space.nasa.spaceapi.controllers to javafx.fxml;
	opens space.nasa.spaceapi.controllers to javafx.fxml;
	opens space.nasa.spaceapi.models to com.google.gson;
	exports space.nasa.spaceapi.models to com.google.gson;
}