package me.droreo002.oreocore.dependencies;

import lombok.Getter;
import me.droreo002.oreocore.dependencies.classloader.LoaderType;

public enum OCoreDependency {


    MARIADB_DRIVER(
            "org{}mariadb{}jdbc",
            "mariadb-java-client",
            "2.4.0",
            "G346tblA35aJS8q1a3dQVZdU7Q7isGMzhwftoz6MZqU=",
            LoaderType.ISOLATED
    ),
    MYSQL_DRIVER(
            "mysql",
            "mysql-connector-java",
            "5.1.47",
            "5PhASPOSsrN7r0ao1QjkuN2uKG0gnvmVueEYhSAcGSM=",
            LoaderType.REFLECTION
    ),
    H2_DRIVER(
            "com.h2database",
            "h2",
            "1.4.198",
            "Mt1rFJy3IqpMLdTUCnSpzUHjKsWaTnVaZuV1NmDWHUY=",
            LoaderType.ISOLATED
    ),
    SQLITE_DRIVER(
            "org.xerial",
            "sqlite-jdbc",
            "3.25.2",
            "pF2mGr7WFWilM/3s4SUJMYCCjt6w1Lb21XLgz0V0ZfY=",
            LoaderType.ISOLATED
    ),
    HIKARI(
            "com{}zaxxer",
            "HikariCP",
            "3.3.1",
            "SIaA1yzGHOZNpZNoIt903f5ScJrIB3u8CT2cNkaLcy0=",
            LoaderType.REFLECTION
    ),
    MONGODB_DRIVER(
            "org.mongodb",
            "mongo-java-driver",
            "3.10.1",
            "IGjdjTH4VjqnqGUdVe8u+dKfzKkpCG1NR11TE8ieCdU=",
            LoaderType.REFLECTION
    );

    @Getter
    private Dependency dependency;

    OCoreDependency(String groupId, String artifactId, String version, String checksum, LoaderType loaderType) {
        this.dependency = new Dependency(name(), groupId, artifactId, version, checksum, loaderType);
    }
}
