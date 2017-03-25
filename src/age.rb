module Atacante

  attr_accessor :potencial_ofensivo, :descansado

  def atacar(un_defensor)
    if self.potencial_ofensivo > un_defensor.potencial_defensivo
      danio = self.potencial_ofensivo - un_defensor.potencial_defensivo
      un_defensor.sufri_danio(danio)
    end
    self.descansado = false
  end

  def potencial_ofensivo
    self.descansado ? @potencial_ofensivo * 2 : @potencial_ofensivo
  end

  def descansar
    self.descansado = true
  end

end

module Defensor

  attr_accessor :potencial_defensivo, :energia

  def sufri_danio(danio)
    self.energia= self.energia - danio
  end

  def descansar
    self.energia += 10
  end

end

class Guerrero
  include Atacante
  alias_method :descansar_atacante, :descansar

  include Defensor
  alias_method :descansar_defensor, :descansar

  attr_accessor :peloton

  def initialize(potencial_ofensivo=20, energia=100, potencial_defensivo=10)
    self.potencial_ofensivo = potencial_ofensivo
    self.energia = energia
    self.potencial_defensivo = potencial_defensivo
  end

  def descansar
    self.descansar_atacante
    self.descansar_defensor
  end

  def lastimado
    self.peloton.lastimado if self.peloton
  end

  def sufri_danio(un_danio)
    super(un_danio)
    self.lastimado if cansado
  end

  def cansado
    self.energia <= 40
  end

end

class Espadachin < Guerrero

  attr_accessor :espada

  #constructor
  def initialize(espada)
    super(20, 100, 2)
    self.espada= espada
  end

  def potencial_ofensivo
    super() + self.espada.potencial_ofensivo
  end
end

class Espada
  attr_accessor :potencial_ofensivo

  def initialize(potencial_ofensivo)
    self.potencial_ofensivo= potencial_ofensivo
  end
end

class Misil
  include Atacante

  def initialize(potencial_ofensivo=200)
    self.potencial_ofensivo = potencial_ofensivo
  end
end

class Muralla
  include Defensor

  def initialize(potencial_defensivo= 50, energia = 200)
    self.potencial_defensivo = potencial_defensivo
    self.energia = energia
  end

end

class Kamikaze
  include Defensor
  include Atacante

  def initialize(energia=100, potencial_defensivo=10)
    self.potencial_ofensivo = 250
    self.energia = energia
    self.potencial_defensivo = potencial_defensivo
  end

  def atacar(un_defensor)
    super(un_defensor)
    self.energia = 0
  end

end

class Peloton

  attr_accessor :integrantes, :estrategia, :retirado

  def self.cobarde(integrantes)
    self.new(integrantes) { |peloton|
      peloton.retirate
    }
  end

  def self.descansador(integrantes)
    self.new(integrantes) { |peloton|
      peloton.descansar
    }
  end

  def initialize(integrantes, &estrategia)
    self.integrantes = integrantes
    self.estrategia = estrategia
    self.integrantes.each { |integrante|
      integrante.peloton = self
    }
  end

  def lastimado
    self.estrategia.call(self)
  end

  def descansar
    cansados = self.integrantes.select { |integrante|
      integrante.cansado
    }
    cansados.each { |integrante|
      integrante.descansar
    }
  end

  def retirate
    self.retirado = true
  end

end