package org.rootservices.authorization.http.controller.resource;


import org.rootservices.otter.controller.Resource;
import org.rootservices.otter.controller.entity.Request;
import org.rootservices.otter.controller.entity.Response;
import org.rootservices.otter.controller.entity.StatusCode;


public class NotFoundResource extends Resource {
    public static String URL = "/notFound";
    public Response get(Request request, Response response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response post(Request request, Response response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response put(Request request, Response response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response delete(Request request, Response response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response connect(Request request, Response response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response options(Request request, Response response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response trace(Request request, Response response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response patch(Request request, Response response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }

    public Response head(Request request, Response response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        return response;
    }
}
