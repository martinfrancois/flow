/*
 * Copyright 2000-2026 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.component.dependency;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.shared.ui.LoadMode;

/**
 * Loads a JavaScript file into the browser at runtime as a {@code <script>}
 * element. The referenced file is served as a static resource by the servlet
 * container; it is not bundled. Use {@link JsModule} when the file should be
 * processed as a bundle source by Vite at build time.
 * <p>
 * Source locations (same as {@link StyleSheet}):
 * <ul>
 * <li>Application projects: {@code src/main/resources/META-INF/resources/}. In
 * Spring Boot projects {@code src/main/resources/public/} or
 * {@code src/main/resources/static/} are also served.</li>
 * <li>Add-on JARs: {@code META-INF/resources/}.</li>
 * </ul>
 * <p>
 * URL resolution follows the same rules as {@link StyleSheet#value()} —
 * {@code context://}, {@code base://}, absolute URLs, and bare relative paths
 * are all supported. Use {@code context://foo.js} to load
 * {@code META-INF/resources/foo.js} regardless of the Vaadin servlet mapping.
 * <p>
 * For adding multiple JavaScript files for a single component, use this
 * annotation multiple times. It is guaranteed that dependencies will be loaded
 * only once.
 * <p>
 * <b>Deprecated bundled interpretation:</b> Historically, a bare relative URL
 * (e.g. {@code @JavaScript("./foo.js")}) was collected by the scanner and
 * bundled like {@link JsModule}. This behavior is deprecated. To opt into the
 * new runtime semantics, prefix the value with {@code context://},
 * {@code base://}, or start it with {@code /}. Bare-relative values still
 * bundle but emit a build-time warning. A future major release will flip the
 * default so that bare relatives also resolve against the context root.
 * <p>
 * NOTE: It's not possible to execute a function defined in a bundled JavaScript
 * module via
 *
 * <pre>
 *
 * <code>
 * UI.getCurrent().getPage().executeJs("some_function_definied_in_module()");
 * </code>
 * </pre>
 *
 * because the function is private there (unless it's explicitly exposed). The
 * JavaScript where the function is defined should be either external or it
 * should be added using {@link Page#addJavaScript(String)}: in this case all
 * declared functions become available in the global scope.
 *
 *
 * @author Vaadin Ltd
 * @since 1.0
 * @see Page#addJavaScript(String)
 * @see JsModule
 * @see StyleSheet
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
@Repeatable(JavaScript.Container.class)
public @interface JavaScript {

    /**
     * JavaScript file URL to load before using the annotated {@link Component}
     * in the browser.
     * <p>
     * URL resolution rules (same as {@link StyleSheet#value()}), in order:
     * <ul>
     * <li>{@code http://...}, {@code https://...}, {@code //...} — used
     * unchanged.</li>
     * <li>{@code context://foo.js} — resolved against the servlet context root
     * (independent of the Vaadin servlet mapping).</li>
     * <li>{@code base://foo.js} — resolved against the page's {@code <base>}
     * URI, i.e. the Vaadin servlet mapping path.</li>
     * <li>{@code /foo.js} — used unchanged as an absolute server path.</li>
     * <li>Any other value (bare relative, {@code "./foo.js"},
     * {@code "../foo.js"}) is currently treated as a bundle source for
     * backwards compatibility — see the deprecation notice on the class
     * Javadoc. Migrate to {@code @JavaScript("context://foo.js")} for runtime
     * loading or to {@link JsModule} for bundling.</li>
     * </ul>
     *
     * @return a JavaScript file URL
     */
    String value();

    /**
     * Defines if the JavaScript should be loaded only when running in
     * development mode (for development tooling etc.) or if it should always be
     * loaded.
     * <p>
     * By default, scripts are always loaded.
     *
     * @return {@code true} to load the script only in development mode,
     *         {@code false} to always load it
     */
    boolean developmentOnly() default false;

    /**
     * Determines the dependency load mode. Refer to {@link LoadMode} for the
     * details.
     *
     * @return load mode for the dependency
     */
    LoadMode loadMode() default LoadMode.EAGER;

    /**
     * Internal annotation to enable use of multiple {@link JavaScript}
     * annotations.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    @Documented
    public @interface Container {

        /**
         * Internally used to enable use of multiple {@link JavaScript}
         * annotations.
         *
         * @return an array of the JavaScript annotations
         */
        JavaScript[] value();
    }

}
