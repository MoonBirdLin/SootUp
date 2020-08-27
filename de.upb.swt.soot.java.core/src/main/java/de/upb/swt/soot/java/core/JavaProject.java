package de.upb.swt.soot.java.core;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2019-2020 Markus Schmidt, Linghui Luo and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import de.upb.swt.soot.core.Project;
import de.upb.swt.soot.core.Scope;
import de.upb.swt.soot.core.SourceTypeSpecifier;
import de.upb.swt.soot.core.inputlocation.AnalysisInputLocation;
import de.upb.swt.soot.core.inputlocation.ClassLoadingOptions;
import de.upb.swt.soot.core.inputlocation.DefaultSourceTypeSpecifier;
import de.upb.swt.soot.java.core.language.JavaLanguage;
import de.upb.swt.soot.java.core.views.JavaView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nonnull;

/**
 * Project Implementation for analyzing Java.
 *
 * @author Markus Schmidt
 * @author Linghui Luo
 */
public class JavaProject extends Project {

  private JavaViewBuilder viewBuilder;

  public JavaProject(
      JavaLanguage language,
      @Nonnull List<AnalysisInputLocation> inputLocations,
      @Nonnull SourceTypeSpecifier sourceTypeSpecifier) {
    super(language, inputLocations, JavaIdentifierFactory.getInstance(), sourceTypeSpecifier);
    this.viewBuilder = new JavaViewBuilder(this);
  }

  @Nonnull
  @Override
  public JavaView createOnDemandView() {
    return viewBuilder.createOnDemandView();
  }

  @Nonnull
  @Override
  public JavaView createOnDemandView(
      @Nonnull Function<AnalysisInputLocation, ClassLoadingOptions> classLoadingOptionsSpecifier) {
    return viewBuilder.createOnDemandView(classLoadingOptionsSpecifier);
  }

  @Nonnull
  @Override
  public JavaView createFullView() {
    return viewBuilder.createFullView();
  }

  @Nonnull
  @Override
  public JavaView createView(Scope s) {
    return viewBuilder.createView(s);
  }

  /**
   * Creates a {@link JavaProject} builder.
   *
   * @return A {@link JavaProjectBuilder}.
   */
  @Nonnull
  public static JavaProjectBuilder builder(JavaLanguage language) {
    return new JavaProjectBuilder(language);
  }

  public static class JavaProjectBuilder {
    private final List<AnalysisInputLocation> analysisInputLocations = new ArrayList<>();
    private SourceTypeSpecifier sourceTypeSpecifier = DefaultSourceTypeSpecifier.getInstance();
    private final JavaLanguage language;

    public JavaProjectBuilder(JavaLanguage language) {
      this.language = language;
    }

    @Nonnull
    public JavaProjectBuilder setSourceTypeSpecifier(SourceTypeSpecifier sourceTypeSpecifier) {
      this.sourceTypeSpecifier = sourceTypeSpecifier;
      return this;
    }

    @Nonnull
    public JavaProjectBuilder addClassPath(
        Collection<AnalysisInputLocation> analysisInputLocations) {
      this.analysisInputLocations.addAll(analysisInputLocations);
      return this;
    }

    @Nonnull
    public JavaProjectBuilder addClassPath(AnalysisInputLocation analysisInputLocation) {
      this.analysisInputLocations.add(analysisInputLocation);
      return this;
    }

    @Nonnull
    JavaProjectBuilder addModulePath(Collection<AnalysisInputLocation> analysisInputLocation) {
      this.analysisInputLocations.addAll(analysisInputLocation);
      return this;
    }

    @Nonnull
    JavaProjectBuilder addModulePath(AnalysisInputLocation analysisInputLocation) {
      this.analysisInputLocations.add(analysisInputLocation);
      return this;
    }

    @Nonnull
    public JavaProject build() {
      return new JavaProject(language, analysisInputLocations, sourceTypeSpecifier);
    }
  }
}
