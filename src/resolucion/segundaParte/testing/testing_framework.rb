require 'rainbow/refinement'
using Rainbow

def assert(un_booleano)
  puts "FAIL".red if !un_booleano
  puts "PASS".green if un_booleano
end

class TestSuite
  attr_accessor :nombre_del_test
  def initialize(nombre_del_test,&definicion_de_las_suite)
    @nombre_del_test = nombre_del_test
    instance_eval(&definicion_de_las_suite)
  end

  def test(&definicion_del_test)
    definicion_del_test.call
  end

  def ejecutar

  end
end