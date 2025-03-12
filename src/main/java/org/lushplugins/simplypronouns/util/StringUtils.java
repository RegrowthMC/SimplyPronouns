package org.lushplugins.simplypronouns.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class StringUtils {

    public static String parseString(@Nullable String string, @NotNull Function<String, String> stringModifier) {
        return string != null ? stringModifier.apply(string) : null;
    }
}
