package io.deviad.ripeti.webapp.domain.valueobject.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
    This is a value object
 */

@Value
@With
@Getter
@Accessors(fluent = true)
/*
    The following two lines are used to tell Jackson that
    getters/setters are not standard with prefix get/set.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class FirstName {

    @JsonUnwrapped
    private final String firstName;


    public FirstName(String firstName) {
        validate(firstName);
        this.firstName = firstName;
    }

    public void validate(String firstName) {
        Pattern p = Pattern.compile("^[A-Za-z]{3,20}$");
        Matcher m = p.matcher(firstName);
        if (!m.matches()) {
            throw new IllegalArgumentException("Password cannot be accepted");
        }
    }


    @Override
    public boolean equals(Object other) {

        if (!this.getClass().getName().equals(other.getClass().getName())) {
            return false;
        }

        FirstName that = (FirstName) other;

        return this.firstName.equals(that.firstName());

    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        return result;

    }

}
