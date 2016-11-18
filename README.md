# Intro

Para poder entender cómo trabaja el framework de reflection en Scala, primero hay que entender su metamodelo y cómo se representa en runtime. Scala no tiene un lenguaje de representación intermedia propio, sino que se compila a bytecode de Java para ejecutarse sobre la JVM (Java Virtual Machine). Eso significa que un programa Scala se traduce a un programa Java que debe funcionar utilizando sólo las representaciones internas pensadas para Java; por lo tanto durante la ejecución de un programa, las abstracciones propias del metamodelo de Scala **no existen**.

Pero el metamodelo de Scala no incluye al de Java? Y… Más o menos. Por ejemplo, tanto Scala como Java tienen clases, pero las de Java no pueden ser linearizadas con mixins, ni recibir parámetros de clase, lo cual las vuelve construcciones similares, pero no idénticas. Por otro lado Java tiene construcciones como las Interfaces que no son soportadas por Scala.

Entonces cómo puede funcionar? Lo que pasa es que Scala se las ingenia para modelar sus propias abstracciones usando las provistas por Java.

Siendo que Scala puede ejecutar nativamente código Java y que en runtime el programa en Scala es un programa Java, existen varias cosas que podemos realizar utilizando [el framework de reflection de Java][1], sin embargo, la gran mayoría de las cosas van a requerir usar [el framework de reflection de Scala][2], que provee una interfaz basada en [mirrors][3] que se adecúa al metamodelo propio de Scala y no requiere saber nada de cómo trabaja el compilador.

# 3 Niveles de Abstracción

Podemos entender entonces que todo programa Scala trabaja en alguna medida con (al menos) 3 metamodelos: El provisto por Scala, el de Java y la representación en la JVM, los cuales soportan diferentes abstracciones:

![NivelesDeAbstraccion]

*Jerarquía Básica de Tipos en Scala*
![JerarquiaDeTipos]

# Reflection en Java

![ArquitecturaJVM]
Arquitectura básica de la jvm

## ClassLoaders y Linkers
 La JVM carga dinámicamente, linkea e inicializa clases e interfaces. La carga de clases es el proceso de encontrar la representación binaria de una clase o de una interfaz con un nombre y la creación dicha representación. Linkeo es el proceso de tomar una clase o interfaz y combinarlo en el estado de la JVM en tiempo de ejecución para que pueda ser ejecutado. La inicialización de una clase o interfaz consiste en ejecutar la inicialización de la clase o interfaz mediante el método <clinit>. Este método es un método especial que provee el compilador y como no es un nombre válido en el lenguaje Java, no puede ser definido por el usuario directamente en un programa Java.
 
## Pool de constantes en tiempo de ejecución
La JVM inicia creando una clase inicial, utilizando el bootstrap class loader, veremos este paso en detalle después. La JVM luego linkea la clase inicial, la inicializa e invoca el método de la clase pública main. La invocación de este método permite la ejecución del programa. La ejecución de las instrucciones de la JVM que constituye el método principal puede causar el linkeo y la creación de clases e interfaces adicionales, así como su ejecución.

La JVM mantiene un pool de constantes por tipo, esto es una estructura creada en tiempo de ejecución que sirve para diferentes funcionalidades, similares a la tabla de símbolos de un lenguaje de programación.

Todas las representaciones binarias de clases o interfaces poseen una tabla de pool de constantes que se usa para construir el pool de constantes de tiempo de ejecución en el momento de creación de esa clase. Todas las referencias en este último pool son inicialmente simbólicas. Las referencias simbólicas en el pool de constantes de tiempo de ejecución son derivados de la representación de la tabla de constantes de la clases o interfaces de la siguiente manera.

Una referencia simbólica a una clase o interfaz es derivado de la estructura CONSTANT_Class_info en la representación binaria de una clase o interfaz. 

~~~java
CONSTANT_Class_info {
    u1 tag;
    u2 name_index;
}
~~~

El name_index, siempre describe el tipo de estructura al que se está referenciando, incluso si la clase es formada de acuerdo a un array, que son objetos, la descripción en el name_index varía de acuerdo a estas estructuras más complejas también.

Dicha referencia tiene un nombre de la clase o interfaz en la forma retornado por el método Class.getName que es, de acuerdo al tipo:
* Para una clase o interfaz del tipo que no sea array, el nombre es el nombre binario de la clase o interfaz.
* Para una clase del tipo array de n dimensiones, el nombre comienza con n ocurrencia de la letra ASCII "[" continuado con la representación del tipo de elemento.

Por ej.

int[][] -> [[I (Si es un tipo primitivo, sólo se lo describe bajo su nombre)

Thread[] -> [Ljava/lang/Thread; (Si es un objeto del tipo de dato referido, se le debe agregar un caracter L seguido del nombre del binario).

Referencias a un campo de una clase o interfaz es derivado de otra estructura, CONSTANT_Fieldref_info, en la representación binaria de una clase o interfaz. Tal referencia provee el nombre y descriptor de un campo, así como la referencia simbólica de clase o interfaz en la que se puede encontrar el campo.

~~~java
CONSTANT_Fieldref_info {
    u1 tag;
    u2 class_index;
    u2 name_and_type_index;
}
~~~

Referencias a un método de una clase es derivado de la estructura CONSTANT_Methodref_info, en la representación binaria. Esta referencia posee el nombre y descriptor de los métodos así como las referencias simbólicas a la clase en la que se encuentra el método.

~~~java
CONSTANT_Methodref_info {
    u1 tag;
    u2 class_index;
    u2 name_and_type_index;
}
~~~

Referencias a invocaciones en lugares puntuales de la clase o interfaz en formato binario, son derivados de la estructura CONSTANT_InvokeDynamic_info. Dicha estructura nos da la siguiente información

~~~java
CONSTANT_InterfaceMethodref_info {
    u1 tag;
    u2 class_index;
    u2 name_and_type_index;
}
~~~

* Una referencia simbólica al handler de un método, que servirá como método inicial para la instrucción de invokedynamic.
* Una secuencia de referencias simbólicas, string literas, y valores de constantes de tiempo de ejecución que servirán como argumentos estáticos a un método inicial.
* Un nombre de método y un descriptor de método
* Adicionalmente, ciertos valores de tiempo de ejecución que no son referencias simbólicas son derivados de elementos encontrados en la tabla constant_pool
* Un string literal es una referencia a una instancia de la clase String, que es derivado de una estructura, del tipo CONSTANT_String_info. Esta estructura brinda la secuencia de caracteres en Unicode, que constituyen los string literals.

~~~java
CONSTANT_String_info {
    u1 tag;
    u2 string_index;
}
~~~

El lenguaje de programación Java requiere que los string literals idénticos, que poseen la misma secuencia de caracteres, deban referirse a la misma instancia de la clase String. Adicionalmente, en el método String.intern se llama a cualquier string, el resultado es una referencia a la misma instancia de la misma clase que hubiese retornado si el string aparecía como un literal, entonces la siguiente expresión es verdadera:


~~~java
("a" + "b" + "c").intern() == "abc"
~~~

* Valores de constantes en tiempo de ejecución son derivados de las estructuras CONSTANT_Integer_info, CONSTANT_Float_info, CONSTANT_Long_info, or CONSTANT_Double_info. La estructura CONSTANT_Float_info representa valores de formato de acuerdo a IEEE 754 y CONSTANT_Double_info a los doubles del mismo estándar. 
* Las estructuras remanentes en la tabla de constant_pool, de la representación binaria de la clase/interfaz, las estructuras CONSTANT_NameAndType_info and CONSTANT_Utf8_info structures, son usadas solamente de manera indirecta cuando se derivan referencias simbólicas a clases, interfaces, métodos, campos, tipos de métodos y cuándo se derivan string literals y se hacen invocaciones dinámicas.

Sobre un ejemplo sencillo de esto, podemos mencionar que si tenemos una expresión sencilla del tipo
![InicializacionJVM]
*Inicialización de la JVM

Veremos que podemos proveer a la jvm de la clase inicial en la línea de comando. 

por ej. si tenemos nuestro Programa de HolaMundo de la siguiente manera 

~~~java
public class HolaMundo {
   public static void main(String argv[]) {
      System.out.println("Hola Mundo!");
   }
}
~~~

y lo ejecutamos mediante la opción de -verbose:class, podremos decirle explícitamente a java cual es la clase inicial que se cargará

~~~java
java -verbose:class HolaMundo
~~~

Veremos que hay otras maneras de inicializar el comienzo de la ejecución mediante Loaders, que pueden ser definidos por el usuarios y utilizados en la línea de comandos.

## Creación y Carga de binarios
La creación de una clase C con el nombre N consiste en la construcción en el área de métodos de la representación interna de C. La creación es activada y ejecutada por otra clase o interfaz D, que referencia a C en el pool de constantes de tiempo de ejecución. La creación también se puede disparar por D en métodos de invocación dinámicos, o sea, que D referencia a C por medio de reflection.

Si C no es una clase del tipo array, entonces es creado cargando la representación de C utilizando el class loader. Las clases que heredan de clases de Array no tienen una representación binaria externa, son creados por la JVM, más que por un classloader.

Hay dos clases de classloaders, los que son suministrados por la JVM, y los que define el usuario. Cada classloader definido por el usuario es una instancia de una subclase de la clase ClassLoader. 

Hay dos clases de classloaders, los que son suministrados por la JVM, y los que define el usuario. Cada classloader definido por el usuario es una instancia de una subclase de la clase ClassLoader. Las aplicaciones que utilizan esta extensión de classloaders definidos por el usuario, se hacen con el objetivo de extender la manera en la que la JVM dinámicamente carga y crea las clases. Los classloaders creados por el usuario pueden ser utilizados para crear clases que se originan en otros lugares en la máquina local que no están definidos o que deben ser descargados a través de una red, generados al vuelo, o extraídos de algún comprimido o archivo encriptado.

Podemos mencionar un ejemplo de un classloader que baja de internet clases e interfaces:

~~~java
import java.net.*;
import java.io.*;
public class MyNetwordLoader {
   public static void main (String argv[]) throws Exception {

      URLClassLoader loader = new URLClassLoader(new URL[] { new URL("http://www.pepito.com/classes/") });
    
      // Load class from class loader. argv[0] is the name of the class to be loaded
      Class c = loader.loadClass (argv[0]);

      // Create an instance of the class just loaded
      Object o = c.newInstance();

  }
}
~~~

Entonces volviendo a nuestro ejemplo de un classloader con la clase C, un classloader L puede crear a C definiendo directamente o delegando su creación a otro classloader. Si lo hace L directamente, decimos que L define a C. Cuando una classloader delega la creación a otro classloader, el segundo que inicia la carga no es necesariamente el mismo loader que completa la carga y define la clase. Si L crea a C, definiéndolo directamente o delegando, diremos que L inicializa la carga de C.

En tiempo de ejecución, una clase o interfaz es determinada no por su nombre solamente, sino por una tupla, compuesta por su nombre binario y el classloader que lo define. Entonces tal clase pertenece a un paquete de tiempo de ejecución. Este paquete de tiempo de ejecución de una clase es determinado por el nombre del paquete, definiendo el classloader de la clase o interfaz. 

La JVM usa uno de tres procedimientos para crear la clase C denotado por N:
* si N define una clase o interfaz que no es un array, uno de dos métodos siguientes es usados para crear a C
 *  Si D fue definido por el classloader de la jvm, entonces este inicia con la carga de C
 *  si D fue definido por un classloaders definido por un usuario, entonces este inicia la carga de C
* De otra manera N denota una clase del tipo array o derivado de este. Una clase del tipo array es creado directamente por la JVM. Sin embargo el classloader que definió a D es usado en el proceso para crear la clase array C.

Si un error ocurre durante la carga de la clase, entonces una instancia de una subclase de LinkageError, debe ser lanzado en ese punto del programa. Si la JVM trata de cargar la clase C durante la parte de la verificación o resolución, y el classloader que es usado lanza una excepción ClassNotFoundException, entonces la JVM debe lanzar una instancia de la excepción de clase NoClassDefFoundError que hace referencia a la instancia de ClassNotFoundException.

Un classloader con un buen comportamiento, en contexto de que no fallará en cuanto a errores de tipo o a que no lanzará excepciones por el comportamiento que posee, posee las siguientes tres propiedades:

* Dado un mismo nombre, un classloader debería siempre devolver el mismo objeto de esa clase asociada.
* Si un classloader L1 delega la carga de una clase C a otro loader L2, entonces cualquier tipo T que ocurre como la superclase o superinterface directa de C, o como el tipo de campo en C, o como el tipo del parámetro formal de un método o constructor en C, o como un tipo de retorno de un método en C, L1 y L2 deberían retornar el mismo objeto de clase.
* Se un classloader definido por el usuario, busca la representación binaria de clases/interfaces, o carga un grupo de clases relacionadas entre si, entonces debe reflejar los errores de carga solo en los puntos del programa donde se pudieron haber producido sin búsqueda o carga de grupo de clases.

A veces representaremos una clase o interfaz utilizando la notación <N, Ld>, donde N es el nombre de la clase o interfaz y Ld el loader que carga a N. También representaremos una clase o interfaz utilizando la notación NLi, donde N es el nombre de la clase/interfaz, y Li, el classloader que lo inicializa.

## Utilizando el Classloader de la JVM

Los siguientes pasos son usados para cargar y crear una clase C que no hereda de array y cuyo nombre lo llamaremos N utilizando el classloader de la JVM.

Primero, la JVM determina si el classloader por default, ya ha sido registrado como el inicializador de la clase/interfaz denotado por N. Si es así la clase o interfaz es C y no se necesita realizar creación alguna.

De otra manera, la JVM pasa el argumento N y llama al método de carga del classloader por defecto para buscar la representación de C en la plataforma, en el directorio del proyecto o el alguna dependencia definida. Típicamente, una clase o una interfaz estarán representadas utilizando un archivo en un sistema de archivos jerárquico, en el nombre de la clase o interfaz será codificado en la ruta del archivo.  No hay garantía que una representación encontrada es la representación de C. Esta fase de carga debe detectar el siguiente error

Si no hay representación de C alguna en el sistema, se debe lanzar la excepción del tipo ClassNotFoundException.

## Utilizando un Classloader definido por el usuario

Los pasos para cargar y crear una clase/interfaz C que no extiende de una clase del tipo array denotado N usando un classloader L definido por el usuario

Primero, la JVM, determina si L ha sido ya registrado como el loader que inicializa a la clase C, es decir N. Si es así la clase es C y no se necesita realizar creación alguna. 

De otra manera la JVM, invoca loadClass(N) en L. El valor retornado por la invocación es la clase/interfaz C creada ya. La JVM entonces registra que L es el loader que inicializo a C.

En más detalle:

Cuando el método loadClass que está definido en L es invocado con el nombre N de una clase C, L debe realizar alguna de las dos operaciones para cargar C:

* L puede crear un array de bytes representando C como los bytes en una estructura ClassFile, entonces debe invocar el método defineClass de la clase ClassLoader. Invocando defineClass hace que JVM para derivar una clase o interfaz de nombre N utilizando L del array de bytes.
* El classloader L puede delegar la carga de C en otro classloader como lo mencionamos anteriormente, L2. Esto se realiza solamente pasando el argumento N directamente o indirectamente a una invocación de L2. El resultado de esta invocación es C. 

En cualquiera de los dos casos si L no puede cargar a C por medio de su nombre N, debe lanzar una excepción del tipo ClassNotFoundException.

## Creando clases del tipo Array o subclases de este tipo

Para crear una clase del tipo array o que herede de array, llamado C, utilizando un nombre N, mediante un loader L, se realizarán los siguientes pasos.
Si L ya ha sido registrado como la clase que define a C, entonces no necesita realizarse creación alguna y se usa este loader. De otra manera:

Si el tipo del componente es una referencia, se realiza una búsqueda recursiva utilizando el class loader L, para crear el componente de tipo de C

La JVM crea una nueva clase, indicando el tipo de componente y la cantidad de dimensiones.

Si el tipo del componente una referencia, se lo marca como que ha sido definido por el classloader del tipo del componente.

Entonces L se lo registra que define a la clase C.

Si no se definió un tipo de accesibilidad en especial a la clase (public, protected, private), se lo setea por default como public.

## Linkeo

La JVM permite que se pueda disponer del código para la ejecución a través de la carga, linkeo e inicialización. La carga como vimos es el proceso de traer una forma binaria para una clase a la JVM. Linkeo es el proceso de incorporar el tipo de dato binario en el estado de ejecución de la JVM. Dicho proceso está dividido en tres subpasos: verificación, preparación y resolución. La verificación permite que el tipo este propiamente formado y pueda ser ejecutado por la JVM. Preparación involucra la alocación de memoria necesario por el tipo, como memoria para cualquier variable de la clase. Resolución es el proceso de transformar referencias simbólicas en el pool de constantes en referencias directas. Estas implementaciones pueden demorar el proceso de resolución cada vez que una referencia simbólica es utilizado en el programa. Luego de la verificación, preparación, y opcionalmente la resolución se completaron, el tipo está listo para inicialización. Durante esta etapa las variables de clase se les da sus propios valores iniciales.

![Link]

Si bien la JVM, es flexible en cuando se realizan los procesos de carga, y linkeo, cuando se trata de la fase final de inicialización, es estricto. Todas las implementaciones deben inicializar cada clase en su primer uso activo. Cualquiera de estas acciones permiten que se inicialice dicho proceso:

Una nueva instancia de una clase es creado( en bytecodes o implícitamente, mediante reflection, clonado o deserialización).

* La invocación de un método estático declarado por una clase. 
* El uso o asignación de un campo estático declarado por una clase, excepto para campos estáticos que son finales e inicializados por una expresión constante en tiempo de ejecución.
* La invocación de ciertos métodos reflectivos en la API de Java, como los métodos en la clase Class on clases en el paquete java.lang.reflect.
* La designación de una clase como la inicial, cuando una JVM inicializa.
* Todos los usos de otro tipo son pasivos, que no se disparan la inicialización del tipo.

Como se mencionó previamente, la inicialización de una clase requiere previa inicialización de sus superclases. Aplicado recursivamente, esta regla significa que todas las superclases de una clase deben ser inicializadas antes de la inicialización de esta clase. No es lo mismo con las interfaces, porque una subinterface o clase que implementa una interfaces. Entonces la inicialización de una clase requiere la inicialización previa de sus superclases pero no de sus superinterfaces. 

Entonces un tipo como debe ser inicializado ante su primer uso activo, el mismo cuando deba ser inicializado si no fue linkeado deberá pasar por este proceso antes, y si no fue cargado, se ejecuta este paso. Entonces el proceso por el que pasa un tipo es solamente disparado por la inicialización y no por la carga.

## Proceso de Linkeo

Como ya se describió previamente la carga de un tipo, sea una clase o una interfaz, se debe luego pasar por la fase de linkeo que se separa en tres partes, a continuación se describirán las fases del linkeo.

## Verificación

El primer paso del linkeo implica la verificación, que es asegurar que el tipo cumple con las semánticas del lenguaje y no viola la integridad de la jvm. Este paso es otro en el que es flexible, solo debe ser ejecutado cuando se necesita y los diseñadores pueden decidir cuando y como verificar los tipos. La JVM lista todas las excepciones que deberían lanzar este proceso y en qué circunstancias hacerlo. Si bien la JVM indica cuales son las razones y en qué contexto deberían lanzarse los errores, no indica formalmente o estrictamente como llevar a cabo como y en qué orden debería hacerse la detección de errores.

Muchos de estos chequeos son probablemente realizados en ciertos tiempos de acuerdo a la implementación de la jvm. Por ejemplo, durante la fase de carga, la jvm debe parsear el stream del binario que representa el tipo de la clase/interfaz y construir las estructuras a partir del pool de constantes. En este punto solo se realizan algunos chequeos mínimos, que pueden involucrar que solo no se rompa la jvm cuando se parsea el binario y que se está esperando el formato indicado. Incluso aunque algunos de estos pasos se realizan previo a la fase de linkeo, aún siguen formando parte de este paso, y en caso de obtener algún error debería lanzarse en ese momento y sin embargo estos controles se  agrupan dentro de una categoría llamada verificación. Otro chequeo realizado posteriormente es que todas las clases menos Object tengan una superclase, esto se puede hacer en la fase de carga, para asegurar que todas las superclases también se carguen.

Otro chequeo, que en general se hace después de la verificación oficial es la de chequear referencias simbólicas. Como se lo mencionó antes, esto involucra que se tengan que buscar referencias a clases, interfaces, variables y métodos a referencias simbólicas en el constant pool, y reemplazar las referencias simbólicas por referencias directas. Además en este tiempo también se hace el chequeo de permisos de los objetos que se están resolviendo de su referencia simbólica a la referencia concreta.

Entonces que se chequea en la fase propiamente dicha de verificación? Todo lo que no se mencionó hasta ahora, algunos ejemplos incluyen:

Chequear que la clase finales no se subclaseen
Chequear que los métodos finales no se sobrescriben
Asegurarse que no haya declaraciones de métodos incorrectos (colisión de nombres de métodos con la misma firma, tipos de parámetro, chequeo del tipo de retorno). También se verifica que la clases y todos sus superclases tengan aún su código binario compatible con la jvm y que puedan comunicarse entre sí.

Chequear que las entradas del constant pool sean consistentes unos con otros, de acuerdo a los tipos y sus valores asociados, que estén bien formados y tipados. También se hace el chequeo del bytecode si es válido. En este caso no hay nada estricto en cuanto a cómo se deben chequear, se puede hacer todo el chequeo del bytecode previa ejecución o a medida que se van ejecutando las instrucciones.

## Cargando Constraints
Asegurar el tipado seguro de referencias en la presencia de class loaders es importante. Es posible que cuando dos clases distintas de class loaders cargan una clase o interfaz llamada N, el nombre N pueda denotar una clase diferente uno del otro loader.

Cuando una clase o interfaz C = <N1, L1> crea una referencia simbólica a un campo o método de otra clase o interfaz D = <N2, L2>, la referencia simbólica incluye un descriptor especificando el tipo de campo, o el retorno y los tipos de los argumentos. Es esencial que cualquier tipo de nombre N mencionado en un campo, variable o descriptor de método, denotan la misma clase cuando se carga con L1 y/o L2.

Para asegurar esto, la JVM impone la carga de constrains de la forma NL1 = NL2, durante la fase de preparación y linkeo. Para asegurar estos constraints, la JVM, en ciertos tiempo, registrará que un loader en particular es el inicializador de una clase, después de registrar una clase, la JVM debe validar que no se esté violando la integridad de qué otro loader sea el inicializador de esa misma clase. Si sucede esto se lanza una excepción del tipo LinkageError, y la operación de registración falla. De esa manera cuando se carga un constraint, la JVM también chequea que dicho constraint no se haya ya definido, sino se lanza la misma excepción. Estos son los dos momentos en los que se realiza dicho chequeo.

Por ej.

Existe un loader en L en el que L fue registrado que es el inicializador de la clase C con nombre N, por otro lado existe un loader L2 que ha sido registrado como el inicializador de la clase C2, con nombre N. La equivalencia por transitividad dice que NL = NL2 pero C != C2.

## Preparación
Después de que la JVM cargó la clase y realizó las verificaciones de su correspondiente fase, entonces pasa a la fase preparación, en esta fase, la JVM aloca memoria para las variables de la clase y los setea a los valores iniciales declarados. Las variables de clase no son inicializados hasta la fase de inicialización. La alocación de memoria se hace de acuerdo al tipo de dato declarado, en la siguiente tabla:

| **Tipo**      | **Valor inicial**  |
|:-------------:|:------------------:|
| int           | 0                  |
| long          | 0L                 |
| short         | (short) 0          |
| char          | '\u0000'           |
| byte          | (byte) 0           |
| boolean       | false              |
| reference     | null               |
| float         | 0.0f               |
| double        | 0.0d               |

Aunque el booleano aparece en esta tabla, en realidad, la JVM tiene muy poco soporte para booleanos, y estos se traducen a tipo int, que es 0 para false y 1 para true..... Entonces en el caso de los booleanos, estos se traducen a ints y se inicializan a menos que explícitamente se ponga en True a 0.

## Resolución

La fase final del linkeo es la resolución y es el proceso de localizar las clases, interfaces, variables y métodos referidos de manera simbólica del tipo de constant pool, y el reemplazo de estas referencias por referencias directas. Esta fase es opcional, a menos que cada referencia simbólica es primero usada por el programa.

Para más información sobre las etapas de esta fase referirse a [][10]  y [][11] 

Fase de inicialización
Si bien esta fase no forma parte del linkeo ya, solo mencionaremos brevemente que sucede, en esta etapa.


El paso final para que una clase/interfaz esté listo para ejecución es la inicialización, el proceso en el que se setean los valores iniciales finales. En este caso más puntualmente se setean los valores iniciales de las variables de clase, estos valores se designan de acuerdo al tipo de la variable. En esta fase también se precalculan los valores declarados como estáticos. por ej.

~~~scala
class Ejemplo1 {

    // "= 3 * (int) (Math.random() * 5.0)" is the class variable
    // initializer
    static int size = 3 * (int) (Math.random() * 5.0);
}
~~~

así como las declaraciones dentro de un bloque static

~~~scala
class Example1b {

    static int size;

    // This is the static initializer
    static {

        size = 3 * (int) (Math.random() * 5.0);
    }
}
~~~

Todas las variables de clase y estáticas iniciales de un tipo son colectados por el compilador Java y puestos en un método especial. Para las clases es el método de inicialización de clase y para interfaces el método de inicialización de interface. En las clases e interfaces este método es llamado "" (si en serio). Métodos regulares de una aplicación Java no pueden invocar este método, solo puede hacerse mediante la JVM.

Esta fase de inicialización consiste en dos pasos.

Inicializar la superclase directa de una clase, si no fue inicializado ya, ejecutar el método de inicialización, si se definió o necesita definirse y ejecutarse. Cuando se inicializa la superclase directa, estos dos pasos deben realizarse también. En el caso de las interfaces no se necesita inicializar su superinterfaz, solo consiste en una fase, que es la de ejecutar el método de inicialización de la interface, si necesita hacerse esto. También la jvm debe asegurarse de que el proceso este correspondientemente sincronizados, por ejemplo, si múltiples threads necesita inicializar una clase, solo uno podrá realizar esto, mientras que el resto espera. Cuando uno termina, este debe notificar del cambio al resto de los threads.

## Classes
La construcción principal alrededor de la cual gira Java (y, en consecuencia la JVM) es la Clase. A diferencia de otros lenguajes más dinámicos, las de Java son construcciones puramente *estáticas*, lo que hace que sea muy difícil alterarlas durante la ejecución; sin embargo, si lo que buscamos es realizar alguna tarea de *introspection*, Java provee los medios para obtener una descripción en runtime de las clases que permite realizar todo tipo de consultas.

Ojo! No hay que confundir a las clases (instancias de **Class[T]**) con las construcciones que usamos para acceder a los métodos estáticos: *Class[T] != T*

Para obtener una de estas representaciones de un tipo de Java, Scala ofrece la siguiente interfaz:

~~~scala
// obtener la clase a partir de su identificador
classOf[MiClase] //en Java sería MiClase.class

//obtener la clase a partir de una instancia
instancia.getClass
~~~

Una vez adquirida una de estas representaciones de un tipo podemos usar [su interfaz][9] para realizar todo tipo de consultas:

~~~scala
trait T { def f: Int }
class C extends T {
  def m(x: Int) = x
  var f = 5
}

classOf[Any] // returns Class[java.lang.Object]
val classC = classOf[C]
val c = new C

classC.getName // returns "C" : String
classC.getSuperclass // returns java.lang.Object : Class[_]
classC.getInterfaces // returns Array(T) : Array[Class[_]]
classC.isEnum // returns false
classC.isAnnotationPresent(classOf[SomeAnnotation]) // returns false

classC.getDeclaredFields // returns Array(C.f) : Array[Field]
classC.getFields // returns Array() ~> fields públicos (con heredados)
val fieldF = classC.getDeclaredField("f")
fieldF.getType // returns int : Class[_]
fieldF.getAnnotations // returns Array() : Array[Annotation]	fieldF.get(c) // Excepción! El campo es privado => no es accesible
fieldF.setAccessible(true)
fieldF.get(c) // returns 5
fieldF.set(c, 8)
fieldF.get(c) // returns 8

classC.getDeclaredMethods // returns Array(C.f_$eq(int), C.f(), C.m(int)) : Array[Method]
val methodM = classC.getMethod("m", classOf[Int])
methodM.getName // returns "m" : String
methodM.getParameters // returns Array(int arg0) : Array[Parameter]
methodM.getParameterTypes // returns Array(int) : Array[Class[_]]
methodM.getReturnType // returns int : Class[_]
methodM.getTypeParameters // returns Array():Array[TypeVariable[Method]]
methodM.isVarArgs // returns false
methodM.invoke(c, new Integer(3)) // returns 3 : Object

classC.getDeclaredConstructors // returns Array(C())
val constructorC = classC.getConstructor()
constructorC.newInstance() // returns a new C
~~~

# Reflection en Scala

## Universes
Los universos son el punto de entrada al framework de reflection. Existen dos tipos principales de universo, de *runtime* y de *compilación*, los cuales sirven para acceder a la estructura de tipos existente en tiempo de ejecución y en tiempo de compilación, respectivamente.

Para analizar el programa en ejecución, obtener atributos y métodos y ejecutar de forma dinámica necesitamos importar el universo de runtime, lo cual puede hacerse de la siguiente forma:

~~~scala
import scala.reflect.runtime.{universe => ru}
~~~

[La API oficial de Universe][4] provee información detallada de cómo utilizar los universos.

## Types
Los tipos encapsulan la información referente a muchos aspectos de clases y traits. Esto incluye un listado completo de sus miembros (métodos, campos, alias de tipo, definiciones anidadas, etc.) y la posibilidad de compararse entre ellos.

Para obtener un tipo puedo ejecutar el siguiente código:

~~~scala
import scala.reflect.runtime.universe._

class A

typeOf[A] //returns the Type A

weakTypeOf[List[Int]] // for types with type arguments this is how you should do it
~~~

Los usos más comunes para un tipo son compararlo con otros tipos o realizar chequeos en cuanto a sus miembros definidos.

~~~scala
import scala.reflect.runtime.universe._

class A{
  def m = 5
 val f = 3
}

class B extends A
type A2 = A

// Igualdad
typeOf[A] =:= typeOf[A] // true ~> A es el mismo tipo que A
typeOf[A] =:= typeOf[B] // false ~> A no es el mismo tipo que B
typeOf[A] == typeOf[A2] // false ~> == no chequea alias
typeOf[A] =:= typeOf[A2] // true ~> =:= se da cuenta que son el mismo tipo

// Subtipado
typeOf[A] <:< typeOf[A] // true ~> A es subtipo de A
typeOf[B] <:< typeOf[A] // true ~> B es subtipo de A
typeOf[A] <:< typeOf[B] // false ~> A no es subtipo de B
typeOf[Int] <:< typeOf[Long] // false ~> Int no es realmente subtipo de Long
typeOf[Int] weak_<:< typeOf[Long] // true ~> pero Sí hace algo parecido
weakTypeOf[List[B]] <:< weakTypeOf[List[A]] // true
weakTypeOf[List[A]] <:< weakTypeOf[List[B]] // false

// Declaraciones
typeOf[A].declarations // returns SynchronizedOps(constructor A, method m, value f, value f)
typeOf[A].takesTypeArgs // returns false
typeOf[A].typeParams // returns List()
~~~

[La API oficial de Types][7] provee información detallada de cómo utilizar los types.

## TypeTags
Como ya se mencionó antes, los tipos de Scala se borran al ser compilados para ejecutar en la JVM. Esto significa que, si inspeccionamos en runtime una instancia, no tenemos acceso a toda la información disponible previo a la compilación. Un ejemplo de esto son los *Tipos Paramétricos* que son eliminados al compilar.

Esto puede a veces producir comportamiento inesperado:

~~~scala
def detectorDeEnteros(list : List[Any]) = list match {
  case list: List[Int] => true // WARNING! El [Int] no existe en runtime. Sólo List
  case other => false
}
	
detectorDeEnteros(List("no", "somos", "enteros")) // returns true
~~~

Los *TypeTags* son, a grandes razgos, objetos que pueden ser usados para preservar durante la ejecución toda la información de un tipo disponible al momento de compilar. Estas estructuras son generadas siempre por el compilador, y pueden obtenerse de varias formas:

~~~scala
import scala.reflect.runtime.universe._

// Usando el método typeTag
val tt1 = typeTag[List[Int]]

// Usando un parámetro implicito. Si el compilador no encuentra un valor en contexto, lo genera.
def obtenerTypeTag[T](implicit tt: TypeTag[T]) = tt
val tt2 = obtenerTypeTag[List[Int]]

// Usando un Context Bound en un Type Parameter
def obtenerTypeTagDeOtraForma[T: TypeTag] = implicitly[TypeTag[T]]
~~~

Teniendo esto en cuenta, el código anterior podría reescribirse así:

~~~scala
import scala.reflect.runtime.universe._

def detectorDeEnteros[T: TypeTag](list : List[T]) =
  typeTag[T].tpe =:= typeOf[Int]

detectorDeEnteros(List("no", "somos", "enteros")) // returns false
~~~

Los TypeTags pueden ser usados en conjunto con el extractor TypeRef para analizar un tipo:

~~~scala
import scala.reflect.runtime.universe._

def explotar[T: TypeTag] = typeTag[T].tpe match {
  case TypeRef(typePrefix, symbol, typeArguments) => (typePrefix, symbol, typeArguments)
}

val (typePrefix, symbol, typeArguments) = explotar[List[Int]]

typePrefix // package object scala : Symbol
symbol // type List : Symbol
typeArguments // List(Int) : List[Type]
~~~

[La API oficial de TypeTags][8] provee información detallada de cómo utilizar los types.

## Symbols
Los *símbolos* vinculan nombres a los elementos del metamodelo. Todas las cosas a las que se les puede dar un nombre en Scala tienen un símbolo asociado.

Los *símbolos* contienen también toda la información asociada a las entidades y una amplia interfaz para consultarla, lo cual los convierte (junto con los *Types*) en la abstracción central para realizar *introspection*.

Los *símbolos* se organizan en una jerarquía que refleja la estructura básica de las entidades a las que están asociados (por ejemplo, el símbolo que representa un parámetro de un método es hijo del símbolo asociado a dicho método el cual, a su vez, es hijo del símbolo del trait, clase u objeto que lo define, etc.). Distintos tipos de símbolo existen para reflejar las distintas entidades, con sus distintas interfaces de consulta.

Algunos métodos de la API exponen un tipo de retorno más genérico que el que uno busca. Por ejemplo, si busco las declaraciones de una clase puedo obtener una lista con muchos tipos de símbolo (*MethodSímbol*, *ModuleSímbol*, etc). pero la lista va a ser de tipo List[**TermSymbol**]. Para estos casos existen varios métodos de conversión *as<Abstracción>* para convertir un símbolo a su versión más específica.

~~~scala
import scala.reflect.runtime.universe._

class C[T] { def test[U](x: T)(y: U): Int = ??? }

// member retorna una instancia de Symbol
val testMember: Symbol = typeOf[C[Int]].member(TermName("test"))

// como sabemos que es un método, podemos obtener un MethodSymbol que tiene una interfaz más rica
val testMethod: MethodSymbol = testMember.asMethod
~~~

### TypeSymbols
Representan un tipo, class o trait, así como también tipos paramétricos. Proveen más que nada información sobre la varianza.

### ClassSymbols
Son un caso particular de *TypeSymbol*. Proveen toda la información contenida en la declaración de una clase o trait.

~~~scala
import scala.reflect.runtime.universe._
 
object C
class C[T] {
  …
}

val classSymbol: ClassSymbol = ??? // Más adelante vemos cómo conseguirlo

classSymbol.isCaseClass // returns false
classSymbol.isModule  // returns false
classSymbol.isTrait  // returns false
classSymbol.companion // returns object C : Symbol
classSymbol.isPublic // returns true
classSymbol.typeParams // returns List(type T)
classSymbol.toType // returns the Type
classSymbol.name // returns C : TypeName
classSymbol.primaryConstructor // returns constructor C : Symbol
~~~

### TermSymbols
Representan las declaraciones de val, var, def u object, así como también packages y value parameters

### MethodSymbols
Casos particulares de *TermSymbol*. Representan las declaraciones de def.

~~~scala
import scala.reflect.runtime.universe._
 
class C[T] {
  def m[U>:T](x: T)(y: U): Int = ???
}

val methodSymbol = typeOf[C[Int]].decl("m": TermName).asMethod

methodSymbol.typeSignatureIn(typeOf[C[Int]]) // returns Type [U >: Int](x: Int)(y: U)Int
methodSymbol.typeSignatureIn(typeOf[C[String]]) // returns Type [U >: String](x: Int)(y: U)Int
methodSymbol.name // return m: methodSymbol.NameType
methodSymbol.owner // returns C: ClassSymbol
methodSymbol.isMethod // returns true
methodSymbol.isConstructor // returns false
methodSymbol.isPublic //returns true
methodSymbol.paramLists // returns List(List(value x), List(value y)) : List[List[Symbol]]
~~~

### ModuleSymbols
Representan las declaraciones de object. Permiten obtener la clase implícitamente asociada con la declaración del objeto ( es decir, la clase que sabemos que tiene que existir para que exista el objeto, pero que Scala no nos muestra).

[La API oficial de Symbols][6] provee información detallada de cómo utilizar los symbols.

## Mirrors
Los mirrors son construcciones que centralizan el acceso a la información de reflection, desacoplandola de las clases del modelo. Existen varios tipos de mirrors. El mirror a utilizar se elige en función del tipo de operación que se busca realizar. Se los suele clasificar en 2 grupos:

* **Classloader Mirrors**: Sirven para obtener las representaciones de los tipos y sus miembros. A partir de ellos se puede obtener *invoker mirrors*. Su utilidad principal es convertir nombres en *Symbols*.
* **Invoker Mirrors**: Mirrors especializados, que implementan las tareas más comunes (como invocar métodos y acceder a atributos).

### ReflectiveMirror
Se usan para cargar Symbols a partir de nombres y para obtener *Invoker Mirrors*.

~~~scala
val ru = scala.reflect.runtime.universe
val runtimeMirror = ru.runtimeMirror(getClass.getClassLoader)
~~~

### InstanceMirror
Se usan para crear *Invoker Mirrors* bindeados a la instancia para métodos y campos y para definiciones internas de clases y objetos. También pueden usarse para obtener el *Symbol* y *Type* asociados a la clase de la instance.

~~~scala
class C { def x = 2 }

val ru = scala.reflect.runtime.universe
val runtimeMirror = ru.runtimeMirror(getClass.getClassLoader)
val instanceMirror = runtimeMirror.reflect(new C)

val classSymbol = instanceMirror.symbol.asClass
val classType = classSymbol.toType
val javaClass = runtimeMirror.runtimeClass(classSymbol) // The concrete runtime java class for the symbol
~~~

### MethodMirror
Se usan para invocar métodos de instancia (que son los únicos existentes en Scala) y constructores.

~~~scala
class C { def x = 2 }

val ru = scala.reflect.runtime.universe
val runtimeMirror = ru.runtimeMirror(getClass.getClassLoader)
val methodSymbol = ru.typeOf[C].declaration(ru.TermName("x")).asMethod
val methodMirror = instanceMirror.reflectMethod(methodSymbol)
methodMirror.apply() //returns 2
~~~

### FieldMirror
Se usan para leer/escribir los atributos que Scala usa internamente para los campos.

~~~scala
class C { val x = 2 }

val runtimeMirror = ru.runtimeMirror(getClass.getClassLoader)
val instanceMirror = runtimeMirror.reflect(new C)

val fieldSymbol = ru.typeOf[C].decl(ru.TermName("x")).asTerm
fieldSymbol.isVal // returns false
fieldSymbol.isMethod // returns true

val backingFieldSymbol = fieldSymbol.accessed.asTerm
backingFieldX.isVal // return true
backingFieldX.isMethod // return false

val fieldMirror = instanceMirror.reflectField(fieldSymbol)
fieldMirror.get // returns 2
fieldMirror.set(3)
fieldMirror.get // returns 3

// Los fields también son métodos, por lo tanto puedo hacer esto
instanceMirror.reflectMethod(fieldSymbol.asMethod).apply() // returns 3
~~~

### ClassMirror
Se usan para crear *Invoker Mirrors* para los constructores.

~~~scala
case class C(x: Int)

val runtimeMirror = ru.runtimeMirror(getClass.getClassLoader)
val classSymbol = ru.typeOf[C].typeSymbol.asClass
val classMirror = runtimeMirror.reflectClass(classSymbol)
val constructorSymbol = ru.typeOf[C].decl(ru.nme.CONSTRUCTOR).asMethod
val constructorMirror: ru.MethodMirror = classMirror.reflectConstructor(constructorSymbol)
constructorMirror.apply(2) // returns C(2)
~~~

### ModuleMirror
Se usan para acceder a las instancias de los singleton objects.

~~~scala
object C { def x = 2 }
val runtimeMirror = ru.runtimeMirror(getClass.getClassLoader)
val moduleSymbol = ru.typeOf[C.type].termSymbol.asModule
val moduleMirror = runtimeMirror.reflectModule(moduleSymbol)
moduleMirror.instance // returns C
~~~

[La API oficial de Mirrors][5] provee información detallada de cómo utilizar los mirrors.





[1]: https://docs.oracle.com/javase/tutorial/reflect/
[2]: http://docs.scala-lang.org/overviews/reflection/overview.html
[3]: https://en.wikipedia.org/wiki/Mirror_(programming)
[4]: http://www.scala-lang.org/files/archive/nightly/docs/library/index.html#scala.reflect.api.Universe
[5]: http://www.scala-lang.org/files/archive/nightly/docs/library/index.html#scala.reflect.api.Mirrors
[6]: http://www.scala-lang.org/files/archive/nightly/docs/library/index.html#scala.reflect.api.Symbols
[7]: http://www.scala-lang.org/files/archive/nightly/docs/library/index.html#scala.reflect.api.Types
[8]: http://www.scala-lang.org/files/archive/nightly/docs/library/index.html#scala.reflect.api.TypeTags
[9]: https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html
[10]: https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-5.html
[11]:  https://www.artima.com/insidejvm/ed2/lifetype.html
[NivelesDeAbstraccion]: https://raw.githubusercontent.com/tadp-utn-frba/tadp-clases/scala-reflection/images/NivelesDeAbstraccion.png
[JerarquiaDeTipos]: https://raw.githubusercontent.com/tadp-utn-frba/tadp-clases/scala-reflection/images/JerarquiaDeTipos.png
[ArquitecturaJVM]: https://raw.githubusercontent.com/tadp-utn-frba/tadp-clases/scala-reflection/images/ArquitecturaJVM.gif
[InicializacionJVM]: https://raw.githubusercontent.com/tadp-utn-frba/tadp-clases/scala-reflection/images/InicializacionJVM.png
[Link]: https://raw.githubusercontent.com/tadp-utn-frba/tadp-clases/scala-reflection/images/Link.gif
