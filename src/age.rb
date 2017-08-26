module UnidadDefensiva

  attr_accessor :potencial_defensivo

  def ser_atacado(potencial_ofensivo)
    self.energia = self.energia - (potencial_ofensivo - self.potencial_defensivo)
  end

  def descansar
    self.energia += 10
  end

  def lastimado
    self.energia <= 40
  end

  def energia
    @energia ||= 100
  end

  def energia=(una_energia)
    if una_energia > 0
      @energia = una_energia
    else
      @energia = 0
    end
  end

end

module UnidadOfensiva
  attr_accessor :descansado

  def atacar(otro_guerrero)
    otro_guerrero.ser_atacado(self.potencial_ofensivo)
    self.descansado = false
  end

  def potencial_ofensivo
    if(self.descansado)
      self.potencial_ofensivo_base * 2
    else
      self.potencial_ofensivo_base
    end
    #lógica para duplicar potencial ofensivo al atacar
  end

  def descansar
    self.descansado = true
  end
end

class Guerrero
  include UnidadOfensiva
  alias_method :descansar_atacante, :descansar

  include UnidadDefensiva
  alias_method :descansar_defensor, :descansar

  attr_accessor :potencial_ofensivo_base, :peloton

  def initialize(energia=100, potencial_ofensivo=20, potencial_defensivo=10)
    self.potencial_ofensivo_base = potencial_ofensivo
    self.energia = energia
    self.potencial_defensivo = potencial_defensivo
  end

  def descansar
    self.descansar_atacante
    self.descansar_defensor
  end

  def ser_atacado(potencial_ofensivo)
    super
    self.interesados.each{ |un_interesado| un_interesado.fue_atacado(self) }
  end

  def interesados
    @interesados ||= []
  end


end

class Espadachin < Guerrero

  attr_accessor :arma

  def initialize(arma, potencial_ofensivo=20, potencial_defensivo=10)
    self.arma = arma
    super 70, potencial_ofensivo, potencial_defensivo
  end

  def atacar(otro_guerrero)
    otro_guerrero.ser_atacado(self.potencial_ofensivo + arma.potencial_ofensivo)
  end

end

class Kamikaze
  include UnidadDefensiva
  include UnidadOfensiva

  def atacar(otro_guerrero)
    super
    self.energia = 0
  end

  def potencial_ofensivo_base
    250
  end
end

class Espada
  attr_accessor :potencial_ofensivo

  def initialize(potencial_ofensivo)
    self.potencial_ofensivo = potencial_ofensivo
  end
end

class Muralla
  include UnidadDefensiva

  def initialize(energia=200, potencial_defensivo=0)
    self.energia = energia
    self.potencial_defensivo = potencial_defensivo
  end
end

class Misil
  include UnidadOfensiva

  def potencial_ofensivo_base
    1000
  end
end

class Peloton

  attr_accessor :accion, :retirado

  def guerreros
    @guerreros ||= []
  end

  def initialize(una_accion)
    self.accion = una_accion
  end

  def self.crear_con(*guerreros, &accion)
    peloton = Peloton.new accion

    guerreros.each { |un_guerrero|
      peloton.agregar(un_guerrero)
    }

    peloton
  end

  def self.descansador(*guerreros)
    crear_con(*guerreros, &bloque_descansador)
  end

  def self.bloque_descansador
    lambda {|peloton|
      peloton.guerreros.select {|g| g.lastimado}.each {|g|
        g.descansar
      }
    }
  end

  def self.cobarde(*guerreros)
    crear_con(*guerreros, &bloque_cobarde)
  end

  def self.bloque_cobarde
    lambda {|peloton|
      peloton.retirado = true
    }
  end

  def self.estrategico(*guerreros)
    crear_con(*guerreros) do |peloton|
      self.bloque_cobarde.call(peloton)
      peloton.accion = self.bloque_descansador
    end
  end

  def self.patotero(victima, guerreros)
    crear_con(*guerreros) do |peloton|
      peloton.guerreros.each { |un_guerrero|
        un_guerrero.atacar(victima)
      }
    end
  end

  def agregar(un_guerrero)
    self.guerreros << un_guerrero
    un_guerrero.interesados << self
  end

  def fue_atacado(un_guerrero)
    self.accion.call(self)
  end
end