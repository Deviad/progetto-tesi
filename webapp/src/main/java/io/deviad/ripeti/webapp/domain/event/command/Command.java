package io.deviad.ripeti.webapp.domain.event.command;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type", defaultImpl = VoidCommand.class)
@JsonSubTypes({
        @JsonSubTypes.Type(name = Register.TYPE, value = Register.class),
        @JsonSubTypes.Type(name = Remove.TYPE, value = Remove.class),
        @JsonSubTypes.Type(name = Update.TYPE, value = Update.class)
})
public interface Command {
}

class VoidCommand implements Command {

}
