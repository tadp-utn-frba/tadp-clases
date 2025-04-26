class Guerrero
  @metodos_de_combate = [
    :recibir_danio, :atacar
  ]

  def initialize()
    @salud = 100
  end

  def construir_casita
  end
  def atacar(otro)
  end
  def recibir_danio
  end
end


class A
  def initialize(&un_proc)
    @un_proc = un_proc
  end

  def m
    @un_proc.call
  end
end

x = 10
a = A.new do
  puts x
end

# a.m

class X
  def m
    saraza = 15
    saraza
    # Primero lo busca como variable
    # local. Si no lo encuentra
    # lo busca como si fuese un
    # envío de mensaje.
  end
  def saraza
    10
  end
end
X.new.m

class Contador
  def aumentar
    x = 0
    proc { x += 1 }
  end
end

un_contador = Contador.new.aumentar
otro_contador = Contador.new.aumentar
puts un_contador.call()
puts un_contador.call()
puts un_contador.call()
puts un_contador.call()
puts un_contador.call()
puts un_contador.call()

# A.new.m
# def m
#   puts x
# end
# self.m()
# scope -> contexto
# gate -> puerta
# flat context -> contexto aplanado


# module MisConstantes
#   class A
#
#   end
#   class B
#
#   end
# end
class CreadorDeProc
  def dame_un_proc(&bloque)
    bloque
  end
end