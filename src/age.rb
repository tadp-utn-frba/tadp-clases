module UnidadDefensiva

  attr_accessor :energia, :potencial_defensivo

  def ser_atacado(potencial_ofensivo)
    self.energia = self.energia - (potencial_ofensivo - self.potencial_defensivo)
  end

end

module UnidadOfensiva
  def atacar(otro_guerrero)
    otro_guerrero.ser_atacado(self.potencial_ofensivo)
  end
end

class Guerrero
  include UnidadDefensiva
  include UnidadOfensiva

  attr_accessor :potencial_ofensivo

  def initialize(energia=100, potencial_ofensivo=20, potencial_defensivo=10)
    self.potencial_ofensivo = potencial_ofensivo
    self.energia = energia
    self.potencial_defensivo = potencial_defensivo
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

  def potencial_ofensivo
    1000
  end
end