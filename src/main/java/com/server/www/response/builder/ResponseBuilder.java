package com.server.www.response.builder;

public class ResponseBuilder {
    private static final String OUTPUT = "<html><head><title>Example</title></head><body><p>Worked!!!</p></body></html>";
    private static final String OUTPUT_HEADERS = """
            HTTP/1.1 200 OK\r
            Content-Type: text/html\r
            Content-Length:\s""";
    private static final String OUTPUT_END_OF_HEADERS = "\r\n\r\n";

    public static String buildDefault(){
        return OUTPUT_HEADERS + OUTPUT.length() + OUTPUT_END_OF_HEADERS + OUTPUT;
    }
}
