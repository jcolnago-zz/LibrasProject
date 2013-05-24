/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package validation;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class InputMaskValidation implements ChangeListener<String>{

    public static final String TEXTONLY     = "^([a-z]+)$";
    public static final String NUMBERONLY   = "^([0-9]+)$";
    public static final String PASSWORD     = "^([a-z]+|[0-9]+)$";

    private static final String STYLE = "-fx-effect: dropshadow(gaussian, red, 4, 0.0, 0, 0);";

    public final BooleanProperty erroneous = new SimpleBooleanProperty(false);

    private final String mask;
    private final int max_lenght;
    private final TextField control;


    public InputMaskValidation(String mask, TextField control) {
        this.mask = mask;
        this.max_lenght = 0;
        this.control = control;
    }

    public InputMaskValidation(String mask, int max_lenght, TextField control) {
        this.mask = mask;
        this.max_lenght = max_lenght;
        this.control = control;
    }

    @Override
    public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
        erroneous.setValue(!newValue.matches(mask) || ((max_lenght > 0) ? newValue.length() > max_lenght : false) || newValue.length() == 0);
        control.setStyle( erroneous.get() ? STYLE : "-fx-effect: null;");
    }

}
