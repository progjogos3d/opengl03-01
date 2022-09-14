package br.pucpr.cg;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

import br.pucpr.mage.*;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

/**
 * Exercicio a) da aula 2
 *
 * Alteração do código final da aula para desenhar um quadrado colorido. Utiliza index buffer para evitar a duplicação
 * de vértices.
 */
public class RotatingSquare implements Scene {
	private Keyboard keys = Keyboard.getInstance();

	/** Esta variável guarda o identificador da malha (Vertex Array Object) do triângulo */
	private int vao;

	/** Representa o IndexBuffer */
	private IndexBuffer indices;

	/** Representa o shader program a ser usado no desenho */
	private Shader shader;

	/** Angulo que o triangulo está */
	private float angle;

	@Override
	public void init() {
		//Define a cor de limpeza da tela
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		//Habilita o teste de profundidade e desliga o desenho do verso dos triangulos
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);

		//------------------------------
		//Carga/Compilação dos shaders
		//------------------------------
		shader = Shader.loadProgram("basic");

		//------------------
		//Criação da malha
		//------------------

		//O processo de criação da malha envolve criar um Vertex Array Object e associar a ele um buffer, com as
		// posições dos vértices do triangulo.

		//Criação do Vertex Array Object (VAO)
		vao = glGenVertexArrays();

		//Informamos a OpenGL que iremos trabalhar com esse VAO
		glBindVertexArray(vao);

		//Criação do buffer de posições
		//------------------------------
		var positions = new ArrayBuffer(
			 2,			    //Element size (vec2)
			-0.5f,  0.5f,   //Vertice 0
			 0.5f,  0.5f,   //Vertice 1
			-0.5f, -0.5f,   //Vertice 2
			 0.5f, -0.5f    //Vertice 3
		);
		shader.setAttribute("aPosition", positions);

		//Criação do buffer de cores
		//------------------------------
		var colors = new ArrayBuffer(
			3, 				  //Element size (vec3)
			1.0f, 0.0f, 0.0f, //Vertice 0
			1.0f, 1.0f, 1.0f, //Vertice 1
			0.0f, 1.0f, 0.0f, //Vertice 2
			0.0f, 0.0f, 1.0f  //Vertice 3
		);
		shader.setAttribute("aColor", colors);

		//Criação do Index Buffer
		indices = new IndexBuffer(
			0, 2, 3,   //Vertices do primeiro triangulo
			0, 3, 1    //Segundo triangulo
		);
		glBindVertexArray(0);
		positions.unbind();
		colors.unbind();
		indices.unbind();
	}

	@Override
	public void update(float secs) {
		//Testa se a tecla ESC foi pressionada
		if (keys.isPressed(GLFW_KEY_ESCAPE)) {
			//Fecha a janela, caso tenha sido
			glfwSetWindowShouldClose(glfwGetCurrentContext(), true);
			return;
		}

		//Somamos alguns graus de modo que o angulo mude 180 graus por segundo
		angle += Math.toRadians(180) * secs;
	}

	@Override
	public void draw() {
		//Solicita a limpeza da tela e do buffer de profundidade
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		//Precisamos dizer qual VAO iremos desenhar
		glBindVertexArray(vao);

		//E qual shader program irá ser usado durante o desenho
		shader.bind();

		//Associação da variável World ao shader
		//--------------------------------------
		shader.setUniform("uWorld", new Matrix4f().rotateY(angle));

		//Comandamos a pintura com indicando que 6 índices serão desenhados
		indices.draw();

		glBindVertexArray(0);

	}

	@Override
	public void deinit() {
	}

	public static void main(String[] args) {
		new Window(new RotatingSquare()).show();
	}
}