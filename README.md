# tadp-age-instace_eval-method_missing

Continuación del ejergicio de Age of Empires con los pelotones que pueden ser descansadores, cobardes o cualquier otra estrategia usando bloques sin tener que definir explícitamente los métodos de clase.

Se cambian los bloques que reciben a los pelotones por bloques sin parámetros para evaluarlos en el contexto del pelotón, introduciendo instance_eval.

Se usa define_singleton_method para definir los métodos de clase dinámicamente, y como alternativa a usar el método de clase para definir las estrategias, se redefine method_missing para poder definir los métodos de clase con mensajes como :descansador=.
