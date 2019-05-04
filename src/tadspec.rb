class TADSpecException < StandardError
end

class Object
  def deberia(bloque)
    raise TADSpecException unless self.instance_eval &bloque
  end
end

module Matchers
  def ser(valor)
    return igual(valor) unless valor.is_a?(Proc)
    valor
  end

  def igual(valor)
    Proc.new { self == valor }
  end

  def mayor_a(valor)
    Proc.new { self > valor }
  end

  def menor_a(valor)
    Proc.new { self < valor }
  end

  def uno_de_estos(lista_valores)
    Proc.new { lista_valores.include? self }
  end

  def entender(sym)
    Proc.new { self.respond_to? sym }
  end

  def explotar_con(exception)
    Proc.new do
      self.call
      false
    rescue exception
      true
    end
  end

  def respond_to_missing?(sym)
    ser_prefix = 'ser_'
    tener_prefix = 'tener_'
    sym.to_s.start_with?(ser_prefix) || sym.to_s.start_with?(tener_prefix) || super
  end

  def method_missing(sym, *args, &block)
    ser_prefix = 'ser_'
    tener_prefix = 'tener_'
    if sym.to_s.start_with? ser_prefix
      nombre_metodo = sym.to_s.delete_prefix(ser_prefix)
      Proc.new { self.send nombre_metodo, *args, &block}
    elsif sym.to_s.start_with? tener_prefix
      nombre_metodo = sym.to_s.delete_prefix(tener_prefix)

      bloque_assertion = args[0]
      Proc.new { self.send(nombre_metodo).instance_exec(&bloque_assertion) }
    else
      super
    end
  end
end

class TADSpec
  def self.testear(suite)
    suite.include Matchers
    suite.instance_methods
      .select{ |selector| selector.to_s.start_with? 'testear_' }
      .each { |selector| suite.new.send selector }
  end
end