# Aula 3 - Primeira refatoração

Com base no exercício do quadrado da aula 2, criamos classes para reforçar três conceitos que já vinhamos 
trabalhando diretamente na OpenGL:

1. Classe ArrayBuffer: Para representar um buffer com um dos atributos do vértice (posição, cor, etc);
1. Classe IndexBuffer: Para representar o Index Buffer;
1. Classe Shader: Que representa o shader program completo. 

Todas as classes conhecem o id do objeto gerenciado e possuem método de bind/unbind, tornando a vinculação mais simples.

Com as classes, a criação do array buffer fica simplificada de:

```java
float[] vertexData = new float[] {
    -0.5f,  0.5f,   //Vertice 0
     0.5f,  0.5f,   //Vertice 1
    -0.5f, -0.5f,   //Vertice 2
     0.5f, -0.5f    //Vertice 3
};

positions = glGenBuffers();
glBindBuffer(GL_ARRAY_BUFFER, positions);
glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
glBindBuffer(GL_ARRAY_BUFFER, 0);
glBindVertexArray(0);
```

Para:

```java
positions = new ArrayBuffer(
     2,			    //Element size (vec2)
    -0.5f,  0.5f,   //Vertice 0
     0.5f,  0.5f,   //Vertice 1
    -0.5f, -0.5f,   //Vertice 2
     0.5f, -0.5f    //Vertice 3
);
```

Já a atribuição de um valor ao shader também fica simplificada. O shader já contém métodos setUniform para todos os 
principais tipos utilizados por nós, incluindo tipos de JOGL como o matrix4. Por exemplo, o código:

```java
//Criamos um objeto da classe FloatBuffer
FloatBuffer transform = BufferUtils.createFloatBuffer(16);

//Criamos uma matriz de rotação e a enviamos para o buffer transform
new Matrix4f().rotateY(angle).get(transform);

//Procuramos pelo id da variável uWorld, dentro do shader
int uWorld = glGetUniformLocation(shader, "uWorld");

// Copiamos os dados do buffer para a variável que está no shader
glUniformMatrix4fv(uWorld, false, transform);
```

Transforma-se em:

```java
shader.setUniform("uWorld", new Matrix4f().rotateY(angle));
```

Já a atribuição de um atributo muda de:
```java
int aPosition = glGetAttribLocation(shader, "aPosition");
glEnableVertexAttribArray(aPosition);
glBindBuffer(GL_ARRAY_BUFFER, positions);
glVertexAttribPointer(aPosition, 2, GL_FLOAT, false, 0, 0);
```

Para:
```java
shader.setAttribute("aPosition", positions);
```

Como os objetos também se encarregam de dar bind e unbind em momentos apropriados, o código da faxina também é 
simplificado.