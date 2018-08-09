package br.com.caelum.leilao.teste;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.path.xml.XmlPath;

import br.com.caelum.leilao.modelo.Leilao;
import br.com.caelum.leilao.modelo.Usuario;

public class LeilaoWSTest {
	
	private Usuario usuario;
	private Leilao geladeira;

	@Before
	public void setUp() {
		usuario = new Usuario(1L,"Mauricio Aniche", "mauricio.aniche@caelum.com.br");
		geladeira = new Leilao(1L, "Geladeira", 800.0, usuario, false);
	}
	
	@Test
	public void deveRetornarOLeilaoBuscado() {
		JsonPath path = given().
						//parameter("usuario.id", 1). se for post passa por baixo dos panos
						queryParam("leilao.id", 1). // sempre passa na url
						header("Accept","application/json").
						get("/leiloes/show").
						andReturn().
						jsonPath();
		Leilao leilao = path.getObject("leilao",Leilao.class);
		assertEquals(geladeira, leilao);
	}
	
	@Test
	public void deveRetornarOTotalDeLeiloes() {
		XmlPath path = given().
					   header("Accept", "application/xml").
					   get("/leiloes/total").
					   andReturn().
					   xmlPath();
		int total = path.getInt("int");
		int esperado = 2;
		
		assertEquals(esperado, total);
	}
	
	@Test
	public void deveAdicionarUmLeilaoEDepoisRemoverOMesmo() {
		XmlPath retorno = given()
			.header("Accept","application/xml")
			.contentType("application/xml")
			.body(geladeira)
		.expect()
			.statusCode(200)
		.when()
			.post("/leiloes")
		.andReturn()
			.xmlPath();
		
		Leilao resposta = retorno.getObject("leilao", Leilao.class);
		
		assertEquals(geladeira, resposta);
		
		//deletando aqui
		given()
			.contentType("application/xml")
			.body(resposta)
		.expect()
			.statusCode(200)
		.when()
			.delete("/leiloes/deletar")
		.andReturn()
			.asString();
	}

}
