package org.rootservices.authorization.parse;


import org.rootservices.authorization.parse.exception.OptionalException;
import org.rootservices.authorization.parse.exception.ParseException;
import org.rootservices.authorization.parse.exception.RequiredException;
import org.rootservices.authorization.parse.validator.OptionalParam;
import org.rootservices.authorization.parse.validator.RawType;
import org.rootservices.authorization.parse.validator.RequiredParam;
import org.rootservices.authorization.parse.validator.SupportedTypes;
import org.rootservices.authorization.parse.validator.excpeption.EmptyValueError;
import org.rootservices.authorization.parse.validator.excpeption.MoreThanOneItemError;
import org.rootservices.authorization.parse.validator.excpeption.NoItemsError;
import org.rootservices.authorization.parse.validator.excpeption.ParamIsNullError;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;


public class Parser {
    private static String TO_OBJ_ERROR = "Could not construct to object";
    private static String FIELD_ERROR = "Could not set field value";
    private static String CNF_ERROR = "Could not find target class";
    private static String CONSTRUCT_ERROR = "Could not construct field object";;
    private static String REQ_ERROR="Required field failed validation";
    private static String OPT_ERROR="Optional field failed validation";
    private static String DELIMITTER = " ";
    private OptionalParam optionalParam;
    private RequiredParam requiredParam;

    public Parser(OptionalParam optionalParam, RequiredParam requiredParam) {
        this.optionalParam = optionalParam;
        this.requiredParam = requiredParam;
    }

    /**
     * Translates params to an instance of clazz. The definition of class must be annotated
     * with @Parameter on the field level.
     *
     * @param clazz
     * @param fields
     * @param params
     * @return a new instance of clazz
     * @throws RequiredException
     * @throws OptionalException
     * @throws ParseException
     */
    public Object to(Class clazz, List<ParamEntity> fields, Map<String, List<String>> params) throws RequiredException, OptionalException, ParseException {
        Object o;
        try {
            o = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ParseException(TO_OBJ_ERROR, e);
        }

        for(ParamEntity field: fields) {
            Field f = field.getField();
            Parameter p = field.getParameter();

            List<String> values = params.get(p.name());
            try {
                validate(f.getName(), p.name(), values, p.required());
            } catch (OptionalException e) {
                e.setTarget(o);
                throw e;
            }

            try {
                if (f.getGenericType() instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) f.getGenericType();
                    String rawType = pt.getRawType().getTypeName();

                    if (RawType.LIST.getTypeName().equals(rawType) && values == null) {
                        ArrayList arrayList = new ArrayList();
                        f.set(o, arrayList);
                    } else if (RawType.LIST.getTypeName().equals(rawType)) {
                        ArrayList arrayList = new ArrayList();
                        List<String> parsedValues = stringToList(values.get(0));
                        for(String parsedValue: parsedValues) {
                            Object item = make(pt.getActualTypeArguments()[0].getTypeName(), parsedValue);
                            arrayList.add(item);
                        }
                        f.set(o, arrayList);
                    } else if (RawType.OPTIONAL.getTypeName().equals(rawType) && values == null) {
                        f.set(o, Optional.empty());
                    } else if (RawType.OPTIONAL.getTypeName().equals(rawType)) {
                        Object item = make(pt.getActualTypeArguments()[0].getTypeName(), values.get(0));
                        f.set(o, Optional.of(item));
                    }
                } else {
                    Object item = make(f.getGenericType().getTypeName(), values.get(0));
                    f.set(o, item);
                }
            } catch (IllegalAccessException e) {
                throw new ParseException(FIELD_ERROR, e);
            }
        }
        return o;
    }

    public boolean validate(String field, String param, List<String> input, boolean required) throws RequiredException, OptionalException {
        boolean validated;
        if (required) {
            try {
                validated = requiredParam.run(input);
            } catch (EmptyValueError | MoreThanOneItemError | NoItemsError | ParamIsNullError e) {
                throw new RequiredException(REQ_ERROR, e, field, param);
            }
        } else {
            try {
                validated = optionalParam.run(input);
            } catch (EmptyValueError | MoreThanOneItemError e) {
                throw new OptionalException(OPT_ERROR, e, field, param);
            }
        }
        return validated;
    }

    /**
     * Translate a string that is space delimited into a List of Strings
     * OAUTH2 will pass in a space delimited string for a url parameter value
     * That should be parsed into a list.
     *
     * @param items
     * @return
     */
    public List<String> stringToList(String items) {
        List<String> list = new ArrayList<>();
        for(String item: items.split(DELIMITTER)) {
            list.add(item);
        }
        return list;
    }
    /**
     * Attempts to translate a String, input, to the desired object type, className.
     *
     * @param className
     * @param input
     * @return a new Object with the type of, className
     */
    public Object make(String className, String input) throws ParseException {
        Object item;
        Class target;
        try {
            target = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ParseException(CNF_ERROR, e);
        }

        try {
            Constructor ctor = target.getConstructor(String.class);
            item = ctor.newInstance(input);
        } catch (NoSuchMethodException e) {
            item = makeFromListOfTypes(className, input);
        } catch (IllegalAccessException|InstantiationException|InvocationTargetException e) {
            throw new ParseException(CONSTRUCT_ERROR, e);
        }

        return item;
    }

    public Object makeFromListOfTypes(String className, String input) throws ParseException {
        Object item;

        if (SupportedTypes.UUID.getType().equals(className)) {
            item = UUID.fromString(input);
        } else {
            throw new ParseException(CONSTRUCT_ERROR);
        }

        return item;
    }

    public List<ParamEntity> reflect(Class clazz) {
        List<ParamEntity> fields = new ArrayList<>();
        for(Field field: clazz.getDeclaredFields()) {
            Parameter p = field.getAnnotation(Parameter.class);
            if (p != null) {
                field.setAccessible(true);
                fields.add(new ParamEntity(field, p));
            }
        }
        return fields;
    }
}
