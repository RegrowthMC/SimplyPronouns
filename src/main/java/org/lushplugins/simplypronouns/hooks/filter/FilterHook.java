package org.lushplugins.simplypronouns.hooks.filter;

import org.jetbrains.annotations.Nullable;

public abstract class FilterHook {

    public abstract @Nullable String filter(String string);
}
