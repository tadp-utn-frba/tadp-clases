require_relative 'age'

def hacer_combatir(guerrero)
  oponente = Guerrero.new(20, 100, 20)
  guerrero.atacar(oponente)
  oponente.atacar(guerrero)
  guerrero.descansar
  oponente.descansar
  guerrero.energia
end

# ¿Cómo podríamos aprovechar method_missing para hacer un registrador de mensajes recibidos?