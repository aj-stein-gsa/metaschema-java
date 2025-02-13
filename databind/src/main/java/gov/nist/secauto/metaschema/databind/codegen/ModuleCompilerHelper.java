/*
 * SPDX-FileCopyrightText: none
 * SPDX-License-Identifier: CC0-1.0
 */

package gov.nist.secauto.metaschema.databind.codegen;

import gov.nist.secauto.metaschema.core.model.IModule;
import gov.nist.secauto.metaschema.core.util.ObjectUtils;
import gov.nist.secauto.metaschema.databind.IBindingContext;
import gov.nist.secauto.metaschema.databind.codegen.config.DefaultBindingConfiguration;
import gov.nist.secauto.metaschema.databind.codegen.config.IBindingConfiguration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.Owning;

import java.io.IOException;
import java.lang.module.ModuleDescriptor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.tools.DiagnosticCollector;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * This class provides methods to generate and dynamically compile Java code
 * based on a Module. The {@link #newClassLoader(Path, ClassLoader)} method can
 * be used to get a {@link ClassLoader} for Java code previously generated by
 * this class.
 */
public final class ModuleCompilerHelper {
  private static final Logger LOGGER = LogManager.getLogger(ModuleCompilerHelper.class);

  private ModuleCompilerHelper() {
    // disable construction
  }

  /**
   * Create a new classloader capable of loading Java classes generated in the
   * provided {@code classDir}.
   *
   * @param classDir
   *          the directory where generated Java classes have been compiled
   * @param parent
   *          the classloader to delegate to when the created class loader cannot
   *          load a class
   * @return the new class loader
   */
  @SuppressWarnings("resource")
  @Owning
  @NonNull
  public static ClassLoader newClassLoader(
      @NonNull final Path classDir,
      @NonNull final ClassLoader parent) {
    return ObjectUtils.notNull(AccessController.doPrivileged(
        (PrivilegedAction<ClassLoader>) () -> {
          try {
            return new URLClassLoader(new URL[] { classDir.toUri().toURL() }, parent);
          } catch (MalformedURLException ex) {
            throw new IllegalStateException("unable to configure class loader", ex);
          }
        }));
  }

  /**
   * Generate and compile Java class, representing the provided Module
   * {@code module} and its related definitions, using the default binding
   * configuration.
   *
   * @param module
   *          the Module module to generate Java classes for
   * @param classDir
   *          the directory to generate the classes in
   * @return information about the generated classes
   * @throws IOException
   *           if an error occurred while generating or compiling the classes
   */
  @NonNull
  public static IProduction compileMetaschema(
      @NonNull IModule module,
      @NonNull Path classDir)
      throws IOException {
    return compileModule(module, classDir, new DefaultBindingConfiguration());
  }

  /**
   * Generate and compile Java class, representing the provided Module
   * {@code module} and its related definitions, using the provided custom
   * {@code bindingConfiguration}.
   *
   * @param module
   *          the Module module to generate Java classes for
   * @param classDir
   *          the directory to generate the classes in
   * @param bindingConfiguration
   *          configuration settings with directives that tailor the class
   *          generation
   * @return information about the generated classes
   * @throws IOException
   *           if an error occurred while generating or compiling the classes
   */
  @NonNull
  public static IProduction compileModule(
      @NonNull IModule module,
      @NonNull Path classDir,
      @NonNull IBindingConfiguration bindingConfiguration) throws IOException {
    IProduction production = JavaGenerator.generate(module, classDir, bindingConfiguration);
    List<IGeneratedClass> classesToCompile = production.getGeneratedClasses().collect(Collectors.toList());

    List<Path> classes = ObjectUtils.notNull(classesToCompile.stream()
        .map(IGeneratedClass::getClassFile)
        .collect(Collectors.toUnmodifiableList()));

    // configure the compiler
    JavaCompilerSupport compiler = new JavaCompilerSupport(classDir);
    compiler.setLogger(new JavaCompilerSupport.Logger() {

      @Override
      public boolean isInfoEnabled() {
        return LOGGER.isInfoEnabled();
      }

      @Override
      public boolean isDebugEnabled() {
        return LOGGER.isDebugEnabled();
      }

      @Override
      public void info(String msg) {
        LOGGER.atInfo().log(msg);
      }

      @Override
      public void debug(String msg) {
        LOGGER.atDebug().log(msg);
      }
    });

    // determine if we need to use the module path
    boolean useModulePath = false;
    Module databindModule = IBindingContext.class.getModule();
    if (databindModule != null) {
      ModuleDescriptor descriptor = databindModule.getDescriptor();
      if (descriptor != null) {
        // add the databind module to the task
        compiler.addRootModule(ObjectUtils.notNull(descriptor.name()));
        useModulePath = true;
      }
    }

    handleClassAndModulePath(compiler, useModulePath);

    // perform compilation
    JavaCompilerSupport.CompilationResult result = compiler.compile(classes);

    if (!result.isSuccessful()) {
      // log compilation diagnostics
      DiagnosticCollector<?> diagnostics = new DiagnosticCollector<>();
      if (LOGGER.isErrorEnabled()) {
        LOGGER.error(diagnostics.getDiagnostics().toString());
      }
      throw new IllegalStateException(String.format("failed to compile classes: %s%nClasspath: %s%nModule Path: %s%n%s",
          classesToCompile.stream()
              .map(clazz -> clazz.getClassName().canonicalName())
              .collect(Collectors.joining(",")),
          diagnostics.getDiagnostics().toString(),
          compiler.getClassPath().stream()
              .collect(Collectors.joining(":")),
          compiler.getModulePath().stream()
              .collect(Collectors.joining(":"))));
    }
    return production;
  }

  private static void handleClassAndModulePath(JavaCompilerSupport compiler, boolean useModulePath) {
    String classPath = System.getProperty("java.class.path");
    String modulePath = System.getProperty("jdk.module.path");
    if (useModulePath) {
      // use classpath and modulepath from the JDK
      if (classPath != null) {
        Arrays.stream(classPath.split(":")).forEachOrdered(compiler::addToClassPath);
      }

      if (modulePath != null) {
        Arrays.stream(modulePath.split(":")).forEachOrdered(compiler::addToModulePath);
      }
    } else {
      // use classpath only
      if (classPath != null) {
        Arrays.stream(classPath.split(":")).forEachOrdered(compiler::addToClassPath);
      }

      if (modulePath != null) {
        Arrays.stream(modulePath.split(":")).forEachOrdered(compiler::addToClassPath);
      }
    }

  }
}
