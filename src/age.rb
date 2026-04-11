# mixin Atacante
module Atacante

  attr_accessor :potencial_ofensivo

  def atacar(un_defensor)
    if self.potencial_ofensivo > un_defensor.potencial_defensivo
      danio = self.potencial_ofensivo - un_defensor.potencial_defensivo
      un_defensor.sufri_danio(danio)
    end
  end

  def reposar
    self.potencial_ofensivo = self.potencial_ofensivo + 10
  end
end

# mixin Defensor
module Defensor

  # crea getter y setters
  attr_accessor :potencial_defensivo, :energia

  def sufri_danio(danio)
    energia = energia - danio
  end

  def reposar
    energia = energia + 10
  end
end

class Guerrero
  include Atacante
  alias_method :descansar_atacante, :descansar
  include Defensor
  alias_method :descansar_defensor, :descansar

  # constructor
  def initialize(potencial_ofensivo=20, energia=100, potencial_defensivo=10)
    self.potencial_ofensivo = potencial_ofensivo
    self.energia = energia
    self.potencial_defensivo = potencial_defensivo
  end

  def descansar(un_defensor)
    descansar_atacante
    descansar_defensor
  end
end

class Kamikaze
  # lo dejamos asi por ahora...
  include Defensor
  include Atacante

  def initialize(energia=100, potencial_defensivo=10)
    self.energia = energia
    self.potencial_defensivo = potencial_defensivo
    self.potencial_ofensivo = 250
  end

  # override al atacar de Atacante.
  def atacar(un_defensor)
    super(un_defensor)
    self.energia = 0
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

  def reposar
  end
end