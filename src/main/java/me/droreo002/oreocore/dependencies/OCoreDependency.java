package me.droreo002.oreocore.dependencies;

import lombok.Getter;

public enum OCoreDependency {

    MYSQL_DRIVER(
            "mysql",
            "mysql-connector-java",
            "8.0.21",
            "1eb96e373b225d952aaca5320b9e0635"
    ),
    SQLITE_DRIVER(
            "org.xerial",
            "sqlite-jdbc",
            "3.32.3.2",
            "d16a8924a16b95ee67c5db67f8d9a48b"
    );

    @Getter
    private Dependency dependency;

    OCoreDependency(String groupId, String artifactId, String version, String md5) {
        this.dependency = new Dependency(name(), groupId, artifactId, version, md5);
    }
}
