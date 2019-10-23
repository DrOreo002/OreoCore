/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.droreo002.oreocore.dependencies;

import com.google.common.collect.ImmutableList;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.dependencies.classloader.LoaderType;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.List;

public class Dependency {

    private static final String MAVEN_CENTRAL_REPO = "https://repo1.maven.org/maven2/";
    private static final String LUCK_MIRROR_REPO = "https://nexus.lucko.me/repository/maven-central/";
    private static final String MAVEN_FORMAT = "%s/%s/%s/%s-%s.jar";

    @Getter
    private final String name;
    @Getter
    private final List<URL> urls;
    @Getter
    private final String version;
    @Getter
    private final byte[] checksum;
    @Getter @Setter
    private boolean autoLoad;
    @Getter @Setter
    private LoaderType loaderType;

    public Dependency(String name, String groupId, String artifactId, String version, String checksum, LoaderType loaderType) {
        this.name = name;
        this.autoLoad = true;
        this.loaderType = loaderType;

        String path = String.format(MAVEN_FORMAT,
                rewriteEscaping(groupId).replace(".", "/"),
                rewriteEscaping(artifactId),
                version,
                rewriteEscaping(artifactId),
                version
        );
        try {
            this.urls = ImmutableList.of(
                    new URL(LUCK_MIRROR_REPO + path),
                    new URL(MAVEN_CENTRAL_REPO + path)
            );
        } catch (MalformedURLException e) {
            throw new RuntimeException(e); // propagate
        }
        this.version = version;
        this.checksum = Base64.getDecoder().decode(checksum);
    }

    private static String rewriteEscaping(String s) {
        return s.replace("{}", ".");
    }
}
