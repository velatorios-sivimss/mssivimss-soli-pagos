package com.imss.sivimss.solipagos.client;

import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public class MockModCatalogosClient {
	
	public static void listaTiposSol(HttpStatusCode httpStatusCode, String request , String response, String token, MockServerClient mockServer) {
        String path = "/mssivimss-mod-catalogos/generico/consulta";
        mockServer.when(HttpRequest.request().withMethod(HttpMethod.POST.name())
                                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withHeader("Authorization", "Bearer " + token)
                                .withHeader("Host","localhost:1080")
                                .withHeader("Connection","keep-alive")
                                .withPath(path)
                        //.withBody(request)
                )
                .respond(HttpResponse.response().withStatusCode(httpStatusCode.code())
                        .withBody(response));
    }

	public static void buscarSoli(HttpStatusCode httpStatusCode, String request , String response, String token, MockServerClient mockServer) {
        String path = "/mssivimss-mod-catalogos/generico/paginado";
        mockServer.when(HttpRequest.request().withMethod(HttpMethod.POST.name())
                                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withHeader("Authorization", "Bearer " + token)
                                .withHeader("Host","localhost:1080")
                                .withHeader("Connection","keep-alive")
                                .withPath(path)
                        //.withBody(request)
                )
                .respond(HttpResponse.response().withStatusCode(httpStatusCode.code())
                        .withBody(response));
    }
	
	public static void detalle(HttpStatusCode httpStatusCode, String request , String response, String token, MockServerClient mockServer) {
        String path = "/mssivimss-mod-catalogos/generico/consulta";
        mockServer.when(HttpRequest.request().withMethod(HttpMethod.POST.name())
                                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withHeader("Authorization", "Bearer " + token)
                                .withHeader("Host","localhost:1080")
                                .withHeader("Connection","keep-alive")
                                .withPath(path)
                        //.withBody(request)
                )
                .respond(HttpResponse.response().withStatusCode(httpStatusCode.code())
                        .withBody(response));
    }
     
	public static void agregarSoli(HttpStatusCode httpStatusCode, String request , String response, String token, MockServerClient mockServer) {
        String path = "/mssivimss-mod-catalogos/generico/crear";
        mockServer.when(HttpRequest.request().withMethod(HttpMethod.POST.name())
                                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withHeader("Authorization", "Bearer " + token)
                                .withHeader("Host","localhost:1080")
                                .withHeader("Connection","keep-alive")
                                .withPath(path)
                        //.withBody(request)
                )
                .respond(HttpResponse.response().withStatusCode(httpStatusCode.code())
                        .withBody(response));
    }
	
	public static void aprobarSoli(HttpStatusCode httpStatusCode, String request , String response, String token, MockServerClient mockServer) {
        String path = "/mssivimss-mod-catalogos/generico/actualizar";
        mockServer.when(HttpRequest.request().withMethod(HttpMethod.POST.name())
                                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withHeader("Authorization", "Bearer " + token)
                                .withHeader("Host","localhost:1080")
                                .withHeader("Connection","keep-alive")
                                .withPath(path)
                        //.withBody(request)
                )
                .respond(HttpResponse.response().withStatusCode(httpStatusCode.code())
                        .withBody(response));
    }
	
	public static void cancelarSoli(HttpStatusCode httpStatusCode, String request , String response, String token, MockServerClient mockServer) {
        String path = "/mssivimss-mod-catalogos/generico/actualizar";
        mockServer.when(HttpRequest.request().withMethod(HttpMethod.POST.name())
                                .withHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withHeader("Authorization", "Bearer " + token)
                                .withHeader("Host","localhost:1080")
                                .withHeader("Connection","keep-alive")
                                .withPath(path)
                        //.withBody(request)
                )
                .respond(HttpResponse.response().withStatusCode(httpStatusCode.code())
                        .withBody(response));
    }
	
	
	
}
