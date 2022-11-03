# Gimnasio Pokémon (2020-1C)

## Descripción General del Dominio

Nuestro objetivo es construir un **simulador** que permita analizar el estado físico de un Pokémon en un momento dado, así como también el impacto que podría tener en él realizar ciertas actividades, sin la necesidad de someter a la criaturita a estas pruebas realmente. 

Para esto vamos a conocer algunas cuestiones básicas acerca de estos animalitos:


### **Características (Stats)**

Cada Pokémon cuenta con 3 características principales que representan su estado físico en un momento determinado: **fuerza, velocidad **y** energía** (de la cual conocemos sus valores **actual** y **máximo**).

Además de estas características físicas, poseen cierta madurez, representada como la **experiencia** que van adquiriendo con el tiempo, que determinará su **nivel** (siempre se inicia con exp = 0 y nivel = 1).


### **Especie**

Todos los Pokémon pertenecen a alguna especie, que determina los cambios que sufren al **subir de nivel**, y su **tipo elemental**. 

El **Tipo** representa la afinidad de los miembros de una Especie a cierto elemento o naturaleza y su destreza natural (o no) para realizar ciertas actividades. 

Todas las especies poseen un **Tipo Principal**, sin embargo, algunas especies poseen también un **Tipo Secundario**, que denota un menor grado de afinidad a otra naturaleza.

## Parte 1: Actividades

En el Gimnasio cuentan con un repertorio de **Actividades** para que los Pokémon realicen a modo de entrenamiento. Llevar a cabo una Actividad puede repercutir de muchas formas diversas y el sistema debe poder anticiparse a estos cambios, prediciendo cómo puede verse afectado el Pokémon entrenado. Por esta razón, se desea poder calcular cómo quedaría el Pokémon al realizar una actividad:

**Descansar:**

Cuando un Pokémon descansa recupera al máximo su energía. 

**Nadar:**

Por cada minuto de nado, un Pokémon pierde 1 punto de Energía y gana 20 de Experiencia.

Los Pokémon de **Tipo Agua** ganan, además, 10 puntos de Velocidad por hora.

**Levantar Pesas:**
*   Cuando un Pokémon levanta pesas, gana 1 punto de experiencia por cada kg levantado.
*   Si un Pokémon levanta más de 10 kg por punto de fuerza, pierde 10 de energía y no gana exp.
*   Los Pokémon de **Tipo Principal Pelea** ganan el doble de puntos.
*   Los Pokémon de **Tipo Fantasma** **NO PUEDEN** levantar pesas (es decir, son incapaces de realizar la actividad, sin importar el peso a levantar ni ningún otro factor).

<span style="text-decoration:underline;">Nota</span>: si solo dice “Tipo X”, sin aclarar principal o secundario, se considera que aplica para cualquiera.


## Parte 2: Niveles

El **Nivel** Representa el crecimiento general del Pokémon y puede ser usado para estimar, a grandes rasgos, qué tan fuerte es (a mayor nivel, más poderoso).

Cada vez que un Pokémon aumenta su experiencia (realizando actividades) existe la posibilidad que suba de nivel, incrementando así sus stats.

La cantidad de experiencia necesaria y los incrementos de stats están determinados por la especie.

En concreto, la especie determina:



*   un multiplicador (constante) por cada stat (fuerza, velocidad, energía máxima)
*   resistencia evolutiva, que representa de cierta manera cuánto “le cuesta” desarrollarse

Los stats en un momento dado de un Pokémon se determinan de la siguiente forma:

```
stat = nivel * constante * stat_base (los stats base son aquellos con los que “nace” el Pokémon lvl 1)
```

La experiencia para subir de nivel se calcula con la siguiente fórmula:

```
experiencia_siguiete_nivel = (2^nivel_actual - 1) * resistencia_evolutiva
```


## Parte 3: Estados

Los stats de un Pokémon reflejan los rasgos generales de su estado de salud; sin embargo, existen también ciertas aflicciones que pueden afectarlo. Los cuadros conocidos se detallan a continuación:



*   **<span style="text-decoration:underline;">Dormido</span>**: El Pokémon está profundamente dormido. Muy exhausto para hacer nada.
*   **<span style="text-decoration:underline;">Paralizado</span>**: El Pokémon no puede moverse en absoluto.
*   **<span style="text-decoration:underline;">K.O.</span>**: El Pokémon quedó inconsciente, al borde de la muerte.

Hay que tener en cuenta que un Pokémon puede sufrir, a lo sumo, una aflicción a la vez. Estos estados alteran la forma en la que los pokemones realizan actividades y se desarrollan de la siguiente manera:

**Al descansar**

Además de recuperar toda su energía, si antes de descansar el Pokémon no sufre de ningún Estado y tiene menos del 50% de Energía, se queda **Dormido**.

**Al Nadar**

Los Pokémon Tipo Fuego quedan **K.O.** automáticamente, sin poder llegar a nadar ni un minuto.

**Al Levantar Pesas**

Si un Pokémon se excede levantando pesas, además del comportamiento habitual, queda **Paralizado**.

Si un Pokémon **Paralizado** intenta levantar pesas, queda automáticamente **K.O.** sin ningún otro efecto.

Hay que tener en cuenta que un Pokémon **no puede realizar ninguna Actividad** si se encuentra **K.O.**

También hay que considerar que un Pokémon **Dormido** "ignora" las actividades que se le asignan, es decir, acepta la orden, pero no hace nada y, por lo tanto, no sufre ningún cambio. En este estado, un Pokémon puede "ignorar" hasta 3 actividades; después de eso se despierta y se comporta normalmente.

Por último, similar a Dormido, un Pokémon **Paralizado** ignora la siguiente actividad a realizar, pero a diferencia del primero, vuelve inmediatamente al estado Normal, perdiendo 10 puntos de energía.


## Parte 4: Evolución

A medida que los Pokémon van progresando, existe la posibilidad que sufran una transformación importante que los haga cambiar de especie. A esto se le llama "**Evolucionar**". 

Ciertas especies (no todas) tienen esta habilidad, al alcanzar cierta condición, basada en sus stats actuales. Para estos casos, se sabe a qué **especie evoluciona** (única por especie) y dicha **condición**.

Una condición puede ser, por ej., que cuenten con una fuerza > 100, o tengan al menos 80% de energía.

Dado que un Pokémon solo puede verse afectado por realizar actividades, es solo luego de este momento cuando es propenso a evolucionar.

Tener en cuenta también que, cuando un Pokémon evoluciona, mantiene la misma experiencia y stats base, pero su nivel y stats actuales se recalculan en base a la nueva especie.


## Parte 5: Análisis de Rutinas

Queremos ahora crear rutinas, que son simplemente conjuntos ordenados de actividades a las que les damos un nombre, y analizar en qué estado terminaría un Pokémon luego de realizarla.

También se quiere, dado un conjunto de rutinas, encontrar la que maximiza cierto criterio al realizarla.

Tener en cuenta que un Pokémon podría no ser capaz de terminar la rutina (por ejemplo, a un Fantasma se le pide que levante pesas).

Lo mismo puede ocurrir al analizar múltiples rutinas, si ninguna pudiera realizarse/cumpliera el criterio.

Los criterios pueden ser de los más variados, pero contamos con algunos ejemplos:



*   aquella que lo haga subir al mayor Nivel posible
*   aquella que lo deje con la mayor cantidad de energía posible
*   la más extensa (en cantidad de actividades) que mantenga su energía por encima del 50%


## TP Individual

En esta instancia de evaluación, se pide extender los requerimientos del enunciado original, permitiendo soportar el caso de Eevee, que cuenta con múltiples evoluciones.

El requerimiento entonces es permitir que las especies ahora cuenten con cualquier cantidad de evoluciones (0, 1 o más). Cada evolución tendrá su propia condición evolutiva, y en caso que existan dos evoluciones posibles en un momento determinado, deberá elegirse la que se encuentre primero.