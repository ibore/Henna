package me.ibore.http;

public class HttpMethod {

    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String HEAD = "HEAD";
    public static final String DELETE = "DELETE";
    public static final String OPTIONS = "OPTIONS";
    public static final String PATCH = "PATCH";
    public static final String TRACE = "TRACE";


    public static boolean hasRequestBody(String method) {
        switch (method) {
            case GET:
            case HEAD:
            case TRACE:
                return false;
            case POST:
            case PUT:
            case DELETE:
            case OPTIONS:
            case PATCH:
                return true;
            default:
                throw new NullPointerException("Unknown method");
        }
    }

}
