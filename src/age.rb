class Peloton
  attr_accessor :integrantes, :retirado, :estrategia

  def initialize(integrantes, &estrategia)
    self.integrantes = integrantes
    self.estrategia = estrategia
    self.integrantes.each { |integrante|
      integrante.peloton = self
    }
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

  # Anterior
  # def self.cobarde(integrantes)
  #   self.new(integrantes) {|peloton| 
  #     peloton.retirate
  #   }
  # end
  #
  # def self.descansador(integrantes)
  #   self.new( integrantes) { |peloton|
  #     peloton.descansar
  #   }
  # end
  # def lastimado(defensor)
  #   self.estrategia.call(self)
  # end

  #V1 con instance_eval
  # def self.cobarde(integrantes)
  #   self.new(integrantes) { retirate }
  # end
  #
  # def self.descansador(integrantes)
  #   self.new( integrantes) { descansar }
  # end

  def lastimado
    self.instance_eval(&estrategia)
  end

  #V2 con métodos de clase definidos en runtime con :definir
  #Peloton.descansador(integrantes) -> NoMethodError
  #Peloton.definir(:descansador) {descansar}
  #Peloton.descansador(integrantes) -> el peloton

  def self.definir(nombre, &bloque)
    self.define_singleton_method(nombre){|integrantes|
      self.new(integrantes,&bloque)
    }
  end

  #V3 porque usar explicítamente el método definir es para la gilada
  #Peloton.descansador(integrantes) -> NoMethodError
  #Peloton.descansador = {descansar}
  #Peloton.descansador(integrantes) -> el peloton

  def self.method_missing(selector, *args)
    if selector[-1] == '='
      self.definir(selector[0..-2].to_sym, &args[0])
    else
      super(selector, *args)
    end
  end

  def self.respond_to_missing?(selector, include_all=false)
    selector[-1] == '=' || super(selector, include_all)
  end
end



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