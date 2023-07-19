package com.imss.sivimss.solipagos.controller;

import org.junit.Test;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockserver.model.HttpStatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.imss.sivimss.solipagos.base.BaseTest;
import com.imss.sivimss.solipagos.client.MockModCatalogosClient;
import com.imss.sivimss.solipagos.security.jwt.JwtTokenProvider;
import com.imss.sivimss.solipagos.util.JsonUtil;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@WithMockUser(username="10796223", password="123456",roles = "ADMIN")
public class SoliPagosControllerTest extends BaseTest {
	 @Autowired
	 private JwtTokenProvider jwtTokenProvider;

	 @BeforeEach
	 public void setup() {
	    this.mockMvc = MockMvcBuilders
	                .webAppContextSetup(this.context)
	                .apply(springSecurity())
	                .build();
	 }
	 
	 @Test
	 @DisplayName("lista tipos solicitud")
	 @Order(1)
	 public void listaTipos() throws Exception {
	       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	       String myToken = jwtTokenProvider.createTokenTest(authentication.getPrincipal().toString());
	       MockModCatalogosClient.listaTiposSol(HttpStatusCode.OK_200, JsonUtil.readFromJson("json/request/lista_tipos_mock.json"), JsonUtil.readFromJson("json/response/response_lista_tipos.json"), myToken, mockServer);
	       this.mockMvc.perform(post("/solipagos/lista-tipsoli")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .accept(MediaType.APPLICATION_JSON)
	                    .header("Authorization","Bearer " + myToken)
	                    .content(JsonUtil.readFromJson("json/request/lista_tipos_controller.json"))
	                    .with(csrf()))
	                .andDo(print())
	                .andExpect(status().isOk());
	 }
	 
	 @Test
	 @DisplayName("busqueda")
	 @Order(2)
	 public void buscar() throws Exception {
	       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	       String myToken = jwtTokenProvider.createTokenTest(authentication.getPrincipal().toString());
	       MockModCatalogosClient.buscarSoli(HttpStatusCode.OK_200, JsonUtil.readFromJson("json/request/buscar_solic_mock.json"), JsonUtil.readFromJson("json/response/response_buscar_solic.json"), myToken, mockServer);
	       this.mockMvc.perform(post("/solipagos/buscar")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .accept(MediaType.APPLICATION_JSON)
	                    .header("Authorization","Bearer " + myToken)
	                    .content(JsonUtil.readFromJson("json/request/buscar_solic_controller.json"))
	                    .with(csrf()))
	                .andDo(print())
	                .andExpect(status().isOk());
	 }
	 
	 @Test
	 @DisplayName("detalle")
	 @Order(3)
	 public void detalle() throws Exception {
	       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	       String myToken = jwtTokenProvider.createTokenTest(authentication.getPrincipal().toString());
	       MockModCatalogosClient.detalle(HttpStatusCode.OK_200, JsonUtil.readFromJson("json/request/detalle_solic_mock.json"), JsonUtil.readFromJson("json/response/response_detalle_solic.json"), myToken, mockServer);
	       this.mockMvc.perform(post("/solipagos/detalle")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .accept(MediaType.APPLICATION_JSON)
	                    .header("Authorization","Bearer " + myToken)
	                    .content(JsonUtil.readFromJson("json/request/detalle_solic_controller.json"))
	                    .with(csrf()))
	                .andDo(print())
	                .andExpect(status().isOk());
	 }
	 
	 @Test
	 @DisplayName("generar")
	 @Order(4)
	 public void generar() throws Exception {
	       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	       String myToken = jwtTokenProvider.createTokenTest(authentication.getPrincipal().toString());
	       MockModCatalogosClient.generarSoli(HttpStatusCode.OK_200, JsonUtil.readFromJson("json/request/genera_solic_mock.json"), JsonUtil.readFromJson("json/response/response_genera_solic.json"), myToken, mockServer);
	       this.mockMvc.perform(post("/solipagos/generar")
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .accept(MediaType.APPLICATION_JSON)
	                    .header("Authorization","Bearer " + myToken)
	                    .content(JsonUtil.readFromJson("json/request/genera_solic_controller.json"))
	                    .with(csrf()))
	                .andDo(print())
	                .andExpect(status().isOk());
	 }
}
