// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.ide.actions.runAnything.activity;

import com.intellij.ide.actions.runAnything.RunAnythingCache;
import com.intellij.ide.actions.runAnything.items.RunAnythingHelpItem;
import com.intellij.ide.actions.runAnything.items.RunAnythingItem;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.ui.EmptyIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

import static com.intellij.ide.actions.runAnything.RunAnythingUtil.fetchProject;

/**
 * This class provides ability to run an arbitrary activity for matched 'Run Anything' input text
 */
public interface RunAnythingActivityProvider<V> {
  ExtensionPointName<RunAnythingActivityProvider> EP_NAME = ExtensionPointName.create("com.intellij.runAnything.executionProvider");

  @Nullable
  V findMatchingValue(@NotNull DataContext dataContext, @NotNull String pattern);

  /**
   * Executes arbitrary activity in IDE if {@code pattern} is matched as {@link #isMatching(DataContext, String)}
   *
   * @param dataContext 'Run Anything' action {@code dataContext}, may retrieve {@link Project} and {@link Module} from here
   * @param value       matching value
   * @return true if succeed, false is failed
   */
  void execute(@NotNull DataContext dataContext, @NotNull V value);

  @Nullable
  default Icon getIcon(@NotNull V value) {
    return EmptyIcon.ICON_16;
  }

  @NotNull
  String getCommand(@NotNull V value);

  @Nullable
  default String getAdText() {
    return null;
  }

  @NotNull
  RunAnythingItem getMainListItem(@NotNull DataContext dataContext, @NotNull V value);

  @Nullable
  default RunAnythingHelpItem getHelpItem(@NotNull DataContext dataContext) {
    return null;
  }

  /**
   * Finds provider that matches {@code pattern}
   *
   * @param dataContext 'Run Anything' action {@code dataContext}, may retrieve {@link Project} and {@link Module} from here
   * @param pattern     'Run Anything' search bar input text
   */
  @Nullable
  static RunAnythingActivityProvider findMatchedProvider(@NotNull DataContext dataContext, @NotNull String pattern) {
    return Arrays.stream(EP_NAME.getExtensions()).filter(provider -> provider.findMatchingValue(dataContext, pattern) != null).findFirst()
                 .orElse(null);
  }

  static void executeMatched(@NotNull DataContext dataContext, @NotNull String pattern) {
    List<String> commands = RunAnythingCache.getInstance(fetchProject(dataContext)).getState().getCommands();
    for (RunAnythingActivityProvider provider : EP_NAME.getExtensions()) {
      Object value = provider.findMatchingValue(dataContext, pattern);
      if (value != null) {
        provider.execute(dataContext, value);
        commands.remove(pattern);
        commands.add(pattern);
        break;
      }
    }
  }
}