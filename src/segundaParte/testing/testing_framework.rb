def assert(un_booleano)
  puts "FAIL".red if !un_booleano
  puts "PASS".green if un_booleano
end

class TestSuite
  def initialize(&definicion_de_las_suite)
    instance_eval(&definicion_de_las_suite)
  end

  def test(&definicion_del_test)
    definicion_del_test.call
  end

  def ejecutar

  end
end