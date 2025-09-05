# frozen_string_literal: true

module TadTest
  def testear_que(nombre, &block)
    tests[nombre] = block
  end

  def tests
    @tests ||= {}
  end

  def correr
    tests.each do |nombre, block|
      contexto = ContextoTest.new
      puts "#{nombre} -> #{contexto.instance_eval(&block)}"
    end
  end
end

class ContextoTest
  def assert(condicion)
    condicion ? 'PASS' : 'FAIL'
  end

  def method_missing(nombre, *args, &block)
    if nombre.start_with?('deberian_ser_')
      coso = nombre.to_s.sub('deberian_ser_', '')
      if args[0].respond_to?(coso.to_sym)
        assert(args[0].send(coso, args[1]))
      else
        "El primer parámetro no entiende #{coso}"
      end
    else
      super
    end
  end
end

class Object
  def iguales(otro)
    self == otro
  end
end
