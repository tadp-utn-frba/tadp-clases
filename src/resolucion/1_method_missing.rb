
require_relative "../clase/1_method_missing"
class Espia < BasicObject
  def initialize(objeto)
    @objeto = objeto
  end
  private def method_missing(name, *args)
    @objeto.send(:puts, "Recibido #{name} con parametros: #{args}")

    @objeto.send(name, *args)
  end
end



atila = Guerrero.new
atila_espiado = Espia.new(atila)

atila_espiado.to_s
