tadp2014c1-scala-objetofuncional-microprocesador
================================================

Este proyecto esta separado en varios proyectos pequeños de microporcesador en el que se iŕan introduciendo nuevos conceptos del paradigma funcional sobre el de objetos.

* objetos-puro: Ejercicio inicial resolviendo el problema del microprocesador aplicando el patrón [Visitor](http://en.wikipedia.org/wiki/Visitor_pattern).
* functional-mutable: Ejercicio pasando de objetos a funcional con Pattern Matching.
* functional-inmutable: Ejercicio cambiando el microporcesador para que haya inmutabilidad.

## Instalación

Para utilizar el proyecto desde el scala-ide, se neceita la dependencia sbt previamente, para ello hay que instalar diche dependencia:

* [Para Windows](http://www.scala-sbt.org/release/tutorial/Installing-sbt-on-Windows.html)
* [Para Linux](http://www.scala-sbt.org/release/tutorial/Installing-sbt-on-Linux.html)
* [Para Mac](http://www.scala-sbt.org/release/tutorial/Installing-sbt-on-Mac.html)
* [Instalando manualmente sbt](http://www.scala-sbt.org/release/tutorial/Manual-Installation.html)

una vez instalado sbt, basta desde la linea de comandos ir a cualquiera de los proyectos y ejecutar

```
sbt eclipse
```

Esto instalará todas las dependencias necesarias para usar el proyecto, luego de eso desde eclipse basta con importar el proyecto elegido. Scala IDE detectará automaticamente la estructura del proyecto, desde ahí el proyecto puede usarse normalmente desde la ide

### Ejecutando los tests desde sbt

Para ejecutar los tests desde sbt, basta con ejecutar el siguiente comando desde la consola en el raiz del proyecto elegido

```
sbt test
```
