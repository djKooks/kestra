package org.kestra.storage.local;

import io.micronaut.context.annotation.ConfigurationProperties;
import lombok.Getter;

import javax.inject.Singleton;
import java.nio.file.Path;

@Singleton
@Getter
@ConfigurationProperties("kestra.storage.local")
public class LocalConfig {
    Path basePath;
}