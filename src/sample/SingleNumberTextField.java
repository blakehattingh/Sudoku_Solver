package sample;

import javafx.scene.control.TextField;

import java.util.regex.Pattern;

public class SingleNumberTextField extends TextField {
    private static Pattern integerPattern = Pattern.compile("[1-9]*");
    @Override
    public void replaceText(int start, int end, String text)
    {
        if (validate(text))
        {
            super.replaceText(start, end, text);
            verify();
        }
    }

    @Override
    public void replaceSelection(String text)
    {
        if (validate(text))
        {
            super.replaceSelection(text);
            verify();
        }
    }

    private boolean validate(String text)
    {
        return (integerPattern.matcher(text).matches());
    }

    private void verify() {
        if (getText().length() > 1) {
            setText(getText().substring(1));
            positionCaret(1);

        }

    }
}
