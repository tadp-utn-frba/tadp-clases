tadp2015c2-scala-objetofuncional-microprocesador
================================================

Este proyecto esta separado en varios proyectos pequeños de microporcesador en el que se iŕan introduciendo nuevos conceptos del paradigma funcional sobre el de objetos.

* objetos-puro: Ejercicio inicial resolviendo el problema del microprocesador aplicando el patrón [Visitor](http://en.wikipedia.org/wiki/Visitor_pattern).
* funcional-mutable: Reemplazamos el Visitor por conceptos del paradigma Funcional.
* funcional-inmutable: Volvemos al microprocesador inmutable para trabajar en condiciones más cercanas a Funcional.
* funcional-fold: Aplicamos orden superior para controlar el flujo del programa y evitamos las excepciones.
* funcional-monadas: Refinamos la solución utilizando pseudo-mónadas.

## Instalación

Para utilizar el proyecto desde el Scala-IDE es necesario tener instalado SBT (la herramienta de buildeo de Scala):

* [Para Windows](http://www.scala-sbt.org/release/tutorial/Installing-sbt-on-Windows.html)
* [Para Linux](http://www.scala-sbt.org/release/tutorial/Installing-sbt-on-Linux.html)
* [Para Mac](http://www.scala-sbt.org/release/tutorial/Installing-sbt-on-Mac.html)
* [Instalando manualmente sbt](http://www.scala-sbt.org/release/tutorial/Manual-Installation.html)

una vez instalado sbt, basta desde la linea de comandos ir a cualquiera de los proyectos y ejecutar

```
sbt eclipse
```

Esto instalará todas las dependencias necesarias para usar el proyecto, luego de eso desde eclipse basta con importar el proyecto elegido. Scala-IDE detectará automaticamente la estructura del proyecto, desde ahí el proyecto puede usarse normalmente.

### Ejecutando los tests desde sbt

Para ejecutar los tests desde sbt, basta con ejecutar el siguiente comando desde la consola en el raiz del proyecto elegido

```
sbt test
```
