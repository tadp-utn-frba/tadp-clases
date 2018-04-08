module Atacante

  attr_accessor :potencial_ofensivo, :descansado

  def atacar(un_defensor)
    poder_de_ataque = if self.descansado
                        self.potencial_ofensivo * 2
                      else
                        self.potencial_ofensivo
                      end
    if poder_de_ataque > un_defensor.potencial_defensivo
      danio = poder_de_ataque - un_defensor.potencial_defensivo
      un_defensor.sufri_danio(danio)
    end
    self.descansado = false
  end

  def descansar
    self.descansado = true
  end

end

module Defensor

  attr_accessor :potencial_defensivo, :energia, :energia_max

  def sufri_danio(danio)
    self.energia= self.energia - danio
  end

  def descansar
    self.energia = self.energia_max
  end

  def energia=(value)
    @energia_max = value if self.energia_max.nil?
    @energia = value
  end

end

class Guerrero
  include Atacante
  alias_method :descansar_atacante, :descansar
  include Defensor

  attr_accessor :peloton

  def initialize(potencial_ofensivo=20, energia=100, potencial_defensivo=10)
    self.potencial_ofensivo = potencial_ofensivo
    self.energia = energia
    self.potencial_defensivo = potencial_defensivo
  end

  def descansar
    super()
    descansar_atacante
  end

  def cansado?
    self.energia < 40
  end

  def recibi_danio(danio)
    super(danio)
    self.peloton.recibi_danio()
  end

end

class Espadachin < Guerrero
  attr_accessor :espada

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

class Kamikaze < Guerrero

  alias_method :descansar, :descansar_atacante

  def atacar(un_defensor)
    super(un_defensor)
    self.energia = 0
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
    self.energia_max = energia
  end

end

#Peloton.descansador([..])

class Peloton
  attr_accessor :soldados, :estrategia

  def self.descansador(soldados = [])
    self.new(soldados) { |peloton| peloton.descansar() }
  end

  def self.cobarde(soldados = [])
    self.new(soldados) { |peloton| peloton.salir_corriendo() }
  end

  def initialize(soldados = [], &estrategia)
    self.soldados = soldados
    self.soldados.each { |x| x.peloton = self }
    self.estrategia = estrategia
  end

  def descansar
    self.soldados.select { |soldado| soldado.cansado? }
      .each do |soldado|
        soldado.descansar()
      end
  end

  def recibi_danio
    estrategia.call(self)
  end

  def sali_corriendo
    puts "Retirada!!"
  end
end