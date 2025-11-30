package org.drk.reto2diad.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.drk.reto2diad.session.AuthService;
import org.drk.reto2diad.session.SimpleSessionService;
import org.drk.reto2diad.user.User;
import org.drk.reto2diad.user.UserService;
import org.drk.reto2diad.utils.JavaFXUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField txtContraseña;
    @FXML private TextField txtCorreo;
    @FXML private Label info;
    @FXML private ComboBox<String> cmbUsuarios;

    private UserService userService;
    private AuthService authService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userService = new UserService();
        authService = new AuthService(userService);
        configurarComboUsuarios();
        cmbUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) txtCorreo.setText(newV.replace("*", ""));
        });
    }

    private void configurarComboUsuarios() {
        var users = userService.findAll();
        cmbUsuarios.setItems(FXCollections.observableArrayList(
                users.stream().map(this::formatCorreoAdmin).toList()
        ));
    }

    private String formatCorreoAdmin(User u) {
        String base = u.getEmail();
        return Boolean.TRUE.equals(u.getIs_admin()) ? base + "*" : base;
    }

    @FXML
    public void entrar(ActionEvent actionEvent) {
        var userOpt = authService.validateUser(txtCorreo.getText(), txtContraseña.getText());
        if (userOpt.isPresent()) {
            var user = userOpt.get();
            SimpleSessionService sessionService = new SimpleSessionService();
            sessionService.login(user);
            sessionService.setObject("id", user.getId());
            JavaFXUtil.showModal(Alert.AlertType.CONFIRMATION, "Login Exitoso",
                    "Bienvenido " + user.getEmail() + "!", "Has iniciado sesión correctamente.");
            JavaFXUtil.setScene("/org/drk/reto2diad/main-view.fxml");
        } else {
            JavaFXUtil.showModal(Alert.AlertType.ERROR, "Login", "Credenciales inválidas", "Revisa correo y contraseña.");
        }
    }

    @FXML
    public void Salir(ActionEvent e) {
        System.exit(0);
    }
}
