package org.lushplugins.simplypronouns.gui;

import java.util.function.Function;

public class TextInput {
    private final InputType inputType;
    private final String initialInput;
    private final String prompt;
    private final Function<String, Boolean> textPredicate;

    private TextInput(InputType inputType, String initialInput, String prompt, Function<String, Boolean> textPredicate) {
        this.inputType = inputType;
        this.initialInput = initialInput;
        this.prompt = prompt;
        this.textPredicate = textPredicate;
    }

    public InputType getInputType() {
        return inputType;
    }

    public String getInitialInput() {
        return initialInput;
    }

    public String getPrompt() {
        return prompt;
    }

    public Function<String, Boolean> getTextPredicate() {
        return textPredicate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private InputType inputType;
        private String initialInput;
        private String prompt;
        private Function<String, Boolean> textPredicate;

        private Builder() {}

        public Builder inputType(InputType inputType) {
            this.inputType = inputType;
            return this;
        }

        public Builder initialInput(String initialInput) {
            this.initialInput = initialInput;
            return this;
        }

        public Builder prompt(String prompt) {
            this.prompt = prompt;
            return this;
        }

        public Builder textPredicate(Function<String, Boolean> textPredicate) {
            this.textPredicate = textPredicate;
            return this;
        }

        public TextInput build() {
            return new TextInput(inputType, initialInput, prompt, textPredicate);
        }
    }

    public enum InputType {
        ANVIL,
        SIGN
    }
}
