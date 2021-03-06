package org.kestra.core.models.flows;

import io.micronaut.core.annotation.Introspected;
import lombok.Builder;
import lombok.Value;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Value
@Builder
@Introspected
public class Input {
    @NotNull
    @NotBlank
    @Pattern(regexp="[a-zA-Z0-9_-]+")
    private String name;

    @NotBlank
    @NotNull
    @Valid
    private Type type;

    @NotNull
    private Boolean required = true;

    public enum Type {
        STRING,
        INT,
        FLOAT,
        DATETIME,
        FILE
    }
}
