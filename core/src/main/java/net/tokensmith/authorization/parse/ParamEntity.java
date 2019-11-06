package net.tokensmith.authorization.parse;

import java.lang.reflect.Field;

/**
 * Created by tommackenzie on 4/1/17.
 */
public class ParamEntity {
    Field field;
    Parameter parameter;

    public ParamEntity(Field field, Parameter parameter) {
        this.field = field;
        this.parameter = parameter;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }
}
