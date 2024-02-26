/*
 * Copyright 2003-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.gradle.openapi.tasks;

import io.micronaut.gradle.openapi.ParameterMappingModel;
import io.micronaut.gradle.openapi.ResponseBodyMappingModel;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;

public abstract class AbstractOpenApiGenerator<W extends AbstractOpenApiWorkAction<P>, P extends AbstractOpenApiWorkAction.OpenApiParameters> extends DefaultTask {

    @Classpath
    public abstract ConfigurableFileCollection getClasspath();

    @InputFile
    @PathSensitive(PathSensitivity.NONE)
    public abstract RegularFileProperty getDefinitionFile();

    @Input
    public abstract Property<String> getLang();

    @Input
    public abstract Property<String> getInvokerPackageName();

    @Input
    public abstract Property<String> getApiPackageName();

    @Input
    public abstract Property<String> getModelPackageName();

    @Input
    public abstract Property<Boolean> getUseBeanValidation();

    @Input
    public abstract Property<Boolean> getUseOptional();

    @Input
    public abstract Property<Boolean> getUseReactive();

    @Input
    public abstract ListProperty<String> getOutputKinds();

    @Input
    public abstract Property<String> getSerializationFramework();

    @Input
    public abstract Property<Boolean> getAlwaysUseGenerateHttpResponse();

    @Input
    public abstract Property<Boolean> getGenerateHttpResponseWhereRequired();

    @Input
    public abstract Property<String> getDateTimeFormat();

    @Input
    public abstract ListProperty<ParameterMappingModel> getParameterMappings();

    @Input
    public abstract Property<Boolean> getLombok();

    @Input
    public abstract Property<Boolean> getKsp();

    @Input
    public abstract Property<Boolean> getGeneratedAnnotation();

    @Input
    public abstract Property<Boolean> getFluxForArrays();

    @Input
    public abstract ListProperty<ResponseBodyMappingModel> getResponseBodyMappings();

    @Input
    public abstract MapProperty<String, String> getSchemaMapping();

    @Input
    public abstract MapProperty<String, String> getImportMapping();

    @Input
    public abstract MapProperty<String, String> getNameMapping();

    @Input
    public abstract MapProperty<String, String> getTypeMapping();

    @Input
    public abstract MapProperty<String, String> getEnumNameMapping();

    @Input
    public abstract MapProperty<String, String> getModelNameMapping();

    @Input
    public abstract MapProperty<String, String> getInlineSchemaNameMapping();

    @Input
    public abstract MapProperty<String, String> getInlineSchemaOption();

    @Input
    public abstract MapProperty<String, String> getOpenapiNormalizer();

    @Optional
    @Input
    public abstract Property<String> getApiNamePrefix();

    @Optional
    @Input
    public abstract Property<String> getApiNameSuffix();

    @Optional
    @Input
    public abstract Property<String> getModelNamePrefix();

    @Optional
    @Input
    public abstract Property<String> getModelNameSuffix();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory();

    @Inject
    protected abstract WorkerExecutor getWorkerExecutor();

    @Internal
    protected abstract Class<W> getWorkerAction();

    protected abstract void configureWorkerParameters(P params);

    @TaskAction
    public final void execute() {
        getWorkerExecutor().classLoaderIsolation(spec -> spec.getClasspath().from(getClasspath()))
                .submit(getWorkerAction(), params -> {
                    params.getLang().set(getLang());
                    params.getApiPackageName().set(getApiPackageName());
                    params.getInvokerPackageName().set(getInvokerPackageName());
                    params.getSerializationFramework().set(getSerializationFramework());
                    params.getModelPackageName().set(getModelPackageName());
                    params.getUseBeanValidation().set(getUseBeanValidation());
                    params.getUseOptional().set(getUseOptional());
                    params.getUseReactive().set(getUseReactive());
                    params.getDefinitionFile().set(getDefinitionFile());
                    params.getOutputDirectory().set(getOutputDirectory());
                    params.getOutputKinds().set(getOutputKinds());
                    params.getAlwaysUseGenerateHttpResponse().set(getAlwaysUseGenerateHttpResponse());
                    params.getGenerateHttpResponseWhereRequired().set(getGenerateHttpResponseWhereRequired());
                    params.getDateTimeFormat().set(getDateTimeFormat());
                    params.getParameterMappings().set(getParameterMappings());
                    params.getResponseBodyMappings().set(getResponseBodyMappings());
                    params.getFluxForArrays().set(getFluxForArrays());
                    params.getGeneratedAnnotation().set(getGeneratedAnnotation());
                    params.getLombok().set(getLombok());
                    params.getKsp().set(getKsp());

                    params.getSchemaMapping().set(getSchemaMapping());
                    params.getImportMapping().set(getImportMapping());
                    params.getNameMapping().set(getNameMapping());
                    params.getTypeMapping().set(getTypeMapping());
                    params.getEnumNameMapping().set(getEnumNameMapping());
                    params.getModelNameMapping().set(getModelNameMapping());
                    params.getInlineSchemaNameMapping().set(getInlineSchemaNameMapping());
                    params.getInlineSchemaOption().set(getInlineSchemaOption());
                    params.getOpenapiNormalizer().set(getOpenapiNormalizer());
                    params.getApiNamePrefix().set(getApiNamePrefix().orElse(""));
                    params.getApiNameSuffix().set(getApiNameSuffix().orElse(""));
                    params.getModelNamePrefix().set(getModelNamePrefix().orElse(""));
                    params.getModelNameSuffix().set(getModelNameSuffix().orElse(""));

                    configureWorkerParameters(params);
                });
    }
}
