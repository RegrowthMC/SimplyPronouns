package org.lushplugins.simplypronouns.hooks.filter;

import org.jetbrains.annotations.Nullable;
import org.lushplugins.lushchatfilter.api.LushChatFilterAPI;
import org.lushplugins.lushchatfilter.filter.FlagAction;
import org.lushplugins.lushchatfilter.filter.processor.Result;

public class LushChatFilterHook extends FilterHook {

    @Override
    public @Nullable String filter(String string) {
        Result result = LushChatFilterAPI.filterString(string);
        return result.action() == FlagAction.NONE ? result.output() : null;
    }
}
