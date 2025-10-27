package com.jquinss.passwordmanager.controllers;

import com.jquinss.passwordmanager.data.UserProfile;
import com.jquinss.passwordmanager.exceptions.InvalidKeyPairException;
import com.jquinss.passwordmanager.exceptions.LoadKeyPairException;
import com.jquinss.passwordmanager.exceptions.UserAlreadyExistsException;
import com.jquinss.passwordmanager.managers.DatabaseManager;
import com.jquinss.passwordmanager.managers.SettingsManager;
import com.jquinss.passwordmanager.util.misc.CryptoUtils;
import com.jquinss.passwordmanager.util.misc.DialogBuilder;
import com.jquinss.passwordmanager.util.password.PasswordStrength;
import com.jquinss.passwordmanager.util.password.PasswordStrengthChecker;
import com.jquinss.passwordmanager.util.password.PasswordStrengthCriteria;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.synedra.validatorfx.Check;
import net.synedra.validatorfx.Validator;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.net.URL;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserProfileSetUpPaneController implements Initializable {
    public ImageView publicKeyQuestionMarkImageView;
    public ImageView privateKeyQuestionMarkImageView;
    @FXML
    private TextField userProfileNameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField publicKeyTextField;
    @FXML
    private Button createAccountButton;
    @FXML
    private Button publicKeySelectButton;
    @FXML
    private Button privateKeySelectButton;
    @FXML
    private CheckBox generateKeyPairCheckBox;
    @FXML
    private TextField privateKeyTextField;
    @FXML
    private Label message;

    private final Validator validator = new Validator();

    private final PasswordStrengthChecker passwordStrengthChecker = new PasswordStrengthChecker();

    private UserProfilesPaneController userProfilesPaneController;

    private Stage stage;

    @FXML
    private void selectPublicKey() {
        selectFile("Select the public key", publicKeyTextField);
    }

    @FXML
    public void selectPrivateKey() {
        selectFile("Select the private key", privateKeyTextField);
    }

    private void selectFile(String dialogTitle, TextField textField) {
        FileChooser fileChooser = DialogBuilder.buildFileChooser(dialogTitle);
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            textField.setText(file.toString());
        }
    }

    @FXML
    public void cancelMenu() {
        stage.close();
    }

    @FXML
    public void createUserProfile() {
        try {
            String userProfileName = userProfileNameTextField.getText();
            String password = passwordField.getText();

            validateUserProfileName(userProfileName);

            KeyPair keyPair = getKeyPair();
            validateKeyPair(keyPair);

            UserProfile userProfile = createUserProfile(userProfileName, password, keyPair);

            DatabaseManager.getInstance().addUserProfile(userProfile);
            userProfilesPaneController.addUserProfileToList(userProfile);

            showSuccessMessage("The profile has been created", 3);
            clearFields();
        }
        catch (UserAlreadyExistsException | LoadKeyPairException | InvalidKeyPairException e) {
            showErrorMessage(e.getMessage(), 4);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidAlgorithmParameterException | SQLException |
                NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private KeyPair getKeyPair() throws LoadKeyPairException, NoSuchAlgorithmException {
        if (generateKeyPairCheckBox.isSelected()) {
            return CryptoUtils.generateKeyPair(SettingsManager.getInstance().getKeyPairAlgorithm(),
                    SettingsManager.getInstance().getKeyPairLengthInBits());
        }
        else {
            return CryptoUtils.loadKeyPairFromPEMFile(publicKeyTextField.getText(), privateKeyTextField.getText());
        }
    }

    private void validateUserProfileName(String userProfileName) throws UserAlreadyExistsException, SQLException {
        Optional<UserProfile> result = DatabaseManager.getInstance().getUserProfileByName(userProfileName);
        if (result.isPresent()) {
            throw new UserAlreadyExistsException("Profile" + result.get().getName() + " already exists");
        }
    }

    private void validateKeyPair(KeyPair keyPair) throws InvalidKeyPairException {
        try {
            if (!CryptoUtils.isValidKeyPair(keyPair)) {
                throw new InvalidKeyPairException("The key-pair is not valid");
            }
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private UserProfile createUserProfile(String name, String password, KeyPair keyPair) throws NoSuchAlgorithmException, InvalidKeySpecException,
            InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        byte[] salt = CryptoUtils.generateSaltBytes(SettingsManager.getInstance().getSaltLengthInBytes());
        byte[] passwordHash = CryptoUtils.getHashFromString(password,
                SettingsManager.getInstance().getPasswordHashLengthInBytes(), salt);
        byte[] publicKey = keyPair.getPublic().getEncoded();
        byte[] privateKey = keyPair.getPrivate().getEncoded();

        IvParameterSpec ivParameterSpec = CryptoUtils.getIvParameterSpec(SettingsManager.getInstance().getIvParameterSpecLengthInBytes());
        byte[] iv = ivParameterSpec.getIV();
        SecretKey key = CryptoUtils.getSecretKeyFromPassword(password, salt);
        byte[] encryptedPrivateKey = CryptoUtils.encrypt(privateKey, SettingsManager.getInstance().getSymmetricEncryptionAlgorithm(),
                key, ivParameterSpec);

        UserProfile user = new UserProfile(name, passwordHash);
        user.setPublicKey(publicKey);
        user.setPrivateKey(encryptedPrivateKey);
        user.setPasswordSalt(salt);
        user.setPrivateKeyIV(iv);

        return user;
    }

    void setUserProfilesController(UserProfilesPaneController userProfilesPaneController) {
        this.userProfilesPaneController = userProfilesPaneController;
    }

    private void initializeBindings() {
        publicKeySelectButton.disableProperty().bind(generateKeyPairCheckBox.selectedProperty());
        publicKeyTextField.disableProperty().bind(generateKeyPairCheckBox.selectedProperty());
        privateKeySelectButton.disableProperty().bind(generateKeyPairCheckBox.selectedProperty());
        privateKeyTextField.disableProperty().bind(generateKeyPairCheckBox.selectedProperty());
    }

    private void initializeValidator() {
        createRequiredTextFieldsCheck();
        createPasswordsMatchCheck();
        createMinPasswordStrengthCriteriaCheck();
        createKeyPairSelectionCheck();
    }

    private void createRequiredTextFieldsCheck() {
        createRequiredTextFieldCheck(userProfileNameTextField);
        createRequiredTextFieldCheck(passwordField);
        createRequiredTextFieldCheck(confirmPasswordField);
    }

    private void createRequiredTextFieldCheck(TextField textField) {
        validator.createCheck()
                .withMethod(this::required)
                .dependsOn("text", textField.textProperty())
                .decorates(textField)
                .immediate();
    }

    private void createPasswordsMatchCheck() {
        validator.createCheck()
                .withMethod(c -> {
                    if (!c.get("passwordField").equals(c.get("confirmPasswordField"))) {
                        c.error("Passwords do not match");
                    }
                })
                .dependsOn("passwordField", passwordField.textProperty())
                .dependsOn("confirmPasswordField", confirmPasswordField.textProperty())
                .decorates(passwordField)
                .decorates(confirmPasswordField)
                .immediate();
    }

    private void createMinPasswordStrengthCriteriaCheck() {
        validator.createCheck()
                .withMethod(c -> {
                    String pwd = c.get("passwordField");
                    PasswordStrength pwdStrength = passwordStrengthChecker.checkPasswordStrength(pwd);
                    PasswordStrength minPwdStrength = SettingsManager.getInstance().getMinPasswordStrength();
                    if (pwdStrength.getValue() < minPwdStrength.getValue()) {
                        PasswordStrengthCriteria pwdStrengthCriteria =
                                passwordStrengthChecker.getCriteria(minPwdStrength);
                        c.error("The password must meet the following requirements:\n" + pwdStrengthCriteria.toString());
                    }
                })
                .dependsOn("passwordField", passwordField.textProperty())
                .decorates(passwordField)
                .immediate();
    }

    private void createKeyPairSelectionCheck() {
        validator.createCheck()
                .withMethod(c -> {
                    boolean isGenerateKeyPairCheckBoxSelected = c.get("generateKeyPairCheckBox");
                    String publicKeyText = c.get("publicKeyTextField");
                    String privateKeyText = c.get("privateKeyTextField");
                if (!isGenerateKeyPairCheckBoxSelected && (publicKeyText.isEmpty() || privateKeyText.isEmpty())) {
                    c.error("You must select a public and private key files");
                }
                })
                .dependsOn("generateKeyPairCheckBox", generateKeyPairCheckBox.selectedProperty())
                .dependsOn("publicKeyTextField", publicKeyTextField.textProperty())
                .dependsOn("privateKeyTextField", privateKeyTextField.textProperty())
                .decorates(publicKeyTextField)
                .decorates(privateKeyTextField)
                .immediate();
    }

    private void required(Check.Context context) {
        String text = context.get("text");
        if (text == null || text.isEmpty()) {
            context.error("This field is required");
        }
    }

    private void showTemporaryMessage(String text, String styleClass, int delay) {
        message.getStyleClass().remove(message.getStyleClass().toString());
        message.getStyleClass().add(styleClass);
        message.setText(text);
        message.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(delay));
        pause.setOnFinished(e -> message.setVisible(false));
        pause.play();
    }

    private void showErrorMessage(String text, int delay) {
        showTemporaryMessage(text, "error-message", delay);
    }

    private void showSuccessMessage(String text, int delay) {
        showTemporaryMessage(text, "success-message", delay);
    }

    private void clearFields() {
        userProfileNameTextField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        publicKeyTextField.clear();
        privateKeyTextField.clear();
    }

    private void setToolTips() {
        setToolTipOnImageView(publicKeyQuestionMarkImageView, "Select a PEM encoded public key");
        setToolTipOnImageView(privateKeyQuestionMarkImageView, "Select a PEM encoded private key");
    }

    private void setToolTipOnImageView(ImageView imageView, String text) {
        Tooltip.install(imageView, new Tooltip(text));
    }

    void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeBindings();
        initializeValidator();
        createAccountButton.disableProperty().bind(validator.containsErrorsProperty());
        setToolTips();
    }
}