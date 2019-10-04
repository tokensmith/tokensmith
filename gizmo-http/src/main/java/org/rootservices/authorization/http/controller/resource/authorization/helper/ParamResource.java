package org.rootservices.authorization.http.controller.resource.authorization.helper;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.rootservices.authorization.parse.ParamEntity;
import org.rootservices.authorization.parse.Parser;
import org.rootservices.authorization.parse.exception.OptionalException;
import org.rootservices.authorization.parse.exception.ParseException;
import org.rootservices.authorization.parse.exception.RequiredException;
import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class ParamResource<T> extends Resource {
    protected static Logger logger = LogManager.getLogger(ParamResource.class);

    private Class<T> paramType;
    private List<ParamEntity> paramFields;

    protected Parser parser;

    public void memoizeQueryParams() {
        if(paramType == null) {
            paramType = (Class<T>) ((ParameterizedType) getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[0];
        }

        if (paramFields == null) {
            paramFields = parser.reflect(paramType);
        }
    }

    public Response get(Request request, Response response) {

        memoizeQueryParams();

        T params;
        try {
            params = (T) parser.to(paramType, paramFields, request.getQueryParams());
        } catch (RequiredException e) {
            handleRequiredFromParams(request, response, e);
            return response;
        } catch (OptionalException e) {
            handleOptFromParams(request, response, e);
            return response;
        } catch (ParseException e) {
            handleParseExceptionFromParams(request, response, e);
            return response;
        }

        return get(request, response, params);
    }

    public Response post(Request request, Response response) {

        memoizeQueryParams();

        T params;
        try {
            params = (T) parser.to(paramType, paramFields, request.getQueryParams());
        } catch (RequiredException e) {
            handleRequiredFromParams(request, response, e);
            return response;
        } catch (OptionalException e) {
            handleOptFromParams(request, response, e);
            return response;
        } catch (ParseException e) {
            handleParseExceptionFromParams(request, response, e);
            return response;
        }

        return post(request, response, params);
    }

    public abstract void handleOptFromParams(Request request, Response response, OptionalException e);
    public abstract void handleRequiredFromParams(Request request, Response response, RequiredException e);
    public abstract void handleParseExceptionFromParams(Request request, Response response, ParseException e);

    public abstract Response get(Request request, Response response, T queryParams);
    public abstract Response post(Request request, Response response, T queryParams);
}
