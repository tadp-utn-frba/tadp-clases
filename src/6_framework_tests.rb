require 'colorize'
class TestSuite
  def initialize(&bloque)
    @tests = []
    instance_eval(&bloque)
  end

  def test(nombre, &bloque)
    @tests << Test.new(nombre, bloque)
  end

  def run(print_results = true)
    @tests.each do |test|
      test.run(print_results)
    end
  end
end

class Test
  def initialize(nombre, bloque)
    @nombre = nombre
    @bloque = bloque
    @printing_results = true
  end

  def run(print_results = true)
    @cortar_test = proc { return } # se podria hacer con excepciones tambien
    @printing_results = print_results
    puts "-- #{@nombre} --" if @printing_results
    instance_eval(&@bloque)
  end

  def assert(un_bool)
    if un_bool
      puts "Tuki".green if @printing_results
    else
      puts "Assert falló :(".yellow if @printing_results
      @cortar_test.call
    end
  end
end

def test_suite(&bloque)
  test_suite = TestSuite.new(&bloque)
  test_suite.run
end
