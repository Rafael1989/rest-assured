package br.com.caelum.leilao.teste;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.path.xml.XmlPath;
import com.jayway.restassured.response.Header;

import br.com.caelum.leilao.modelo.Usuario;

import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

public class UsuarioWSTest {
	
	private Usuario mauricio;
	private Usuario guilherme;

	@Before
	public void setUp() {
		mauricio = new Usuario(1L, "Mauricio Aniche","mauricio.aniche@caelum.com.br");
		guilherme = new Usuario(2L, "Guilherme Silveira","guilherme.silveira@caelum.com.br");
		
		//Aqui podemos definir o endereço de homologação ou outros ambientes que não sejam locais
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8080;
	}
	
	@Test
	public void deveRetornarListaDeUsuarios() {
		XmlPath path = given().
						header("Accept", "application/xml").
						get("/usuarios").
						andReturn().
						xmlPath();
		
		/*Usuario usuario1 = path.getObject("list.usuario[0]", Usuario.class);
		Usuario usuario2 = path.getObject("list.usuario[1]", Usuario.class);*/
		List<Usuario> usuarios = path.getList("list.usuario", Usuario.class);
		
		assertEquals(mauricio, usuarios.get(0));
		assertEquals(guilherme, usuarios.get(1));
	}
	
	@Test
	public void deveRetornarOUsuarioBuscado() {
		JsonPath path = given().
						//parameter("usuario.id", 1). se for post passa por baixo dos panos
						queryParam("usuario.id", 1). // sempre passa na url
						header("Accept","application/json").
						get("/usuarios/show").
						andReturn().
						jsonPath();
		Usuario usuario = path.getObject("usuario",Usuario.class);

		//System.out.println(path.getString("usuario.nome"));
		
		assertEquals(mauricio, usuario);
	}
	
	@Test
	public void deveAdicionarUmUsuarioEDepoisDeletarOMesmo() {
		Usuario joao = new Usuario("Joao da Silva","joao@dasilva.com");
		
		XmlPath retorno = given()
			.header("Accept","application/xml")
			.contentType("application/xml")
			.body(joao)
		.expect()
			.statusCode(200)
		.when()
			.post("/usuarios")
		.andReturn()
			.xmlPath();
		
		Usuario resposta = retorno.getObject("usuario", Usuario.class);
		
		assertEquals("Joao da Silva", resposta.getNome());
		assertEquals("joao@dasilva.com", resposta.getEmail());
		
		
		//deletando aqui
		given()
			.contentType("application/xml")
			.body(resposta)
		.expect()
			.statusCode(200)
		.when()
			.delete("/usuarios/deleta")
		.andReturn()
			.asString();
	}
	
}
