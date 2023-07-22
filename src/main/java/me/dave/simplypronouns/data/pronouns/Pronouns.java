package me.dave.simplypronouns.data.pronouns;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Pronouns(int id, @NotNull String pronouns, @Nullable String customFormat) {}
